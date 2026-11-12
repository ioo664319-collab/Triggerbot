package ru.rocketway.triggerbot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocketTriggerBot implements ClientModInitializer {
    public static final String MOD_ID = "rockettriggerbot";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean ENABLED = false;
    public static double RANGE = 5.0;
    public static int DELAY_MS = 100;
    public static boolean IGNORE_MOBS = true;

    private long lastAttack = 0L;

    @Override
    public void onInitializeClient() {
        Keybinds.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ENABLED || client.player == null || client.world == null) return;
            long now = System.currentTimeMillis();
            if (now - lastAttack < DELAY_MS) return;
            if (client.currentScreen != null) return;

            var target = ru.rocketway.triggerbot.bot.TriggerBot.findTarget(client, RANGE, IGNORE_MOBS);
            if (target != null) {
                ru.rocketway.triggerbot.bot.TriggerBot.attack(client, target);
                lastAttack = now;
            }
        });

        LOGGER.info("[Rocket] TriggerBot initialized");
    }
}
