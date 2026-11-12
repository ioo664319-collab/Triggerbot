package ru.rocketway.triggerbot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

public class RocketTriggerBot implements ClientModInitializer {
    public static final String MOD_ID = "rockettriggerbot";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean ENABLED = false;
    public static double RANGE = 5.0;
    public static int DELAY_MS = 250;
    public static boolean IGNORE_MOBS = true;
    public static boolean SMART_CRITS = true;
    public static boolean RANDOM_DELAY = true;

    private long lastAttack = 0L;
    private final Random random = new Random();

    @Override
    public void onInitializeClient() {
        Keybinds.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ENABLED || client.player == null || client.world == null) return;
            if (client.currentScreen != null) return;

            long now = System.currentTimeMillis();
            int currentDelay = DELAY_MS;
            if (RANDOM_DELAY) {
                currentDelay += random.nextInt(101) - 50; // Разброс -50... +50
            }

            if (now - lastAttack < currentDelay) return;

            // Ищем цель в радиусе
            Entity target = ru.rocketway.triggerbot.bot.TriggerBot.findTarget(client, RANGE, IGNORE_MOBS);
            if (target == null) return;

            if (SMART_CRITS) {
                // Бьем только если падаем (крит)
                if (client.player.fallDistance > 0.0F && !client.player.isOnGround()) {
                    attack(client, target);
                    lastAttack = now;
                }
            } else {
                // Обычный удар
                attack(client, target);
                lastAttack = now;
            }
        });
    }

    private void attack(net.minecraft.client.MinecraftClient client, Entity target) {
        if (client.interactionManager != null) {
            client.player.lookAt(net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor.EYES, target.getPos());
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        }
    }
}
