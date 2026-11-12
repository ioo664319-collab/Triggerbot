package ru.rocketway.triggerbot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

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

            // Проверяем, на что наведен прицел (до 5 блоков)
            Entity target = client.crosshairTarget instanceof net.minecraft.entity.Entity ? (Entity) client.crosshairTarget : null;
            if (target == null) return; 

            // Проверки цели (жива ли, не моб, если включен игнор)
            if (!(target instanceof LivingEntity living) || !living.isAlive()) return;
            if (IGNORE_MOBS && target instanceof MobEntity) return;
            if (target instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) return;

            // Бьем без поворота камеры
            if (client.interactionManager != null) {
                client.interactionManager.attackEntity(client.player, target);
                client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                lastAttack = now;
            }
        });
    }
}
