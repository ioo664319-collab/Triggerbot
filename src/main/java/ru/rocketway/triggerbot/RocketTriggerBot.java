package ru.rocketway.triggerbot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

public class RocketTriggerBot implements ClientModInitializer {
    public static final String MOD_ID = "rockettriggerbot";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean ENABLED = false;
    public static double RANGE = 3.0; // Радиус прицела (3 блока - стандарт в Майнкрафте)
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
                currentDelay += random.nextInt(101) - 50;
            }

            if (now - lastAttack < currentDelay) return;

            // ПРОВЕРКА: Куда смотрит твой прицел прямо сейчас
            if (client.crosshairTarget instanceof EntityHitResult hitResult) {
                Entity target = hitResult.getEntity();

                // Проверяем цель (жива ли, моб или игрок)
                if (!(target instanceof LivingEntity living) || !living.isAlive()) return;
                if (IGNORE_MOBS && target instanceof MobEntity) return;
                if (target instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) return;

                // Проверяем дистанцию от тебя до цели
                if (client.player.distanceTo(target) > RANGE) return;

                // ЛОГИКА КРИТОВ
                if (SMART_CRITS) {
                    // Бьем, если мы падаем (крит)
                    if (client.player.fallDistance > 0.0F && !client.player.isOnGround()) {
                        attack(client, target);
                        lastAttack = now;
                    }
                } else {
                    // Бьем без критов (просто прицелился - бот ударил)
                    attack(client, target);
                    lastAttack = now;
                }
            }
        });
    }

    private void attack(net.minecraft.client.MinecraftClient client, Entity target) {
        if (client.interactionManager != null) {
            // НИКАКОГО lookAt! Просто бьем ту цель, на которую смотришь.
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        }
    }
}
