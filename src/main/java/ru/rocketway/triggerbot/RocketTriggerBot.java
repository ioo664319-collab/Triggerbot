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
    public static double RANGE = 3.0;
    public static double CPS = 8.0; // Клики в секунду (по умолчанию 8)
    public static boolean IGNORE_MOBS = true;
    public static boolean AUTO_REFILL = false; // Не бить, если 0 XP
    public static boolean ANTI_AFK = false; // Двигать головой

    public static boolean AIM_ASSIST = false;
    public static double AIM_SPEED = 2.0;

    private long lastAttack = 0L;
    private final Random random = new Random();
    private float afkTimer = 0;

    @Override
    public void onInitializeClient() {
        Keybinds.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (client.currentScreen != null) return;

            // --- ANTI-AFK (Двигаем головой, чтобы не кикнуло) ---
            if (ANTI_AFK) {
                afkTimer += 1.0f;
                if (afkTimer > 100) { // Каждые 5 секунд
                    client.player.setYaw(client.player.getYaw() + (random.nextFloat() * 4 - 2));
                    afkTimer = 0;
                }
            }

            // --- ТРИГГЕРБОТ ---
            if (!ENABLED) return;
            if (client.player.isUsingItem() || client.player.isSneaking()) return;

            // Auto-Refill: не бить, если 0 XP уровней (чтобы не тратить зачарования)
            if (AUTO_REFILL && client.player.experienceLevel <= 0) return;

            long now = System.currentTimeMillis();
            
            // Вычисляем задержку в мс на основе CPS (1000 / CPS)
            double baseDelay = 1000.0 / CPS;
            // Добавляем плавное колебание (синусоида + рандом)
            double wave = Math.sin(now / 200.0) * 20.0; // Плавные волны
            double randomOffset = (random.nextDouble() * 30.0) - 15.0; // Небольшой шум
            
            long currentDelay = (long) (baseDelay + wave + randomOffset);
            if (currentDelay < 30) currentDelay = 30; // Защита от слишком быстрых кликов

            if (now - lastAttack < currentDelay) return;

            if (client.crosshairTarget instanceof EntityHitResult hitResult) {
                Entity target = hitResult.getEntity();

                if (!(target instanceof LivingEntity living) || !living.isAlive()) return;
                if (IGNORE_MOBS && target instanceof MobEntity) return;
                if (target instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) return;
                if (client.player.distanceTo(target) > RANGE) return;

                if (client.player.fallDistance > 0.0F && !client.player.isOnGround()) {
                    attack(client, target);
                    lastAttack = now;
                } 
                else if (client.player.isOnGround()) {
                    attack(client, target);
                    lastAttack = now;
                }
            }
        });
    }

    private void attack(net.minecraft.client.MinecraftClient client, Entity target) {
        if (client.interactionManager != null) {
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        }
    }
}
