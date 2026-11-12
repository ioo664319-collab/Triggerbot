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
    public static int DELAY_MS = 250;
    public static boolean IGNORE_MOBS = true;
    public static boolean RANDOM_DELAY = true;

    private long lastAttack = 0L;
    private final Random random = new Random();

    @Override
    public void onInitializeClient() {
        Keybinds.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ENABLED || client.player == null || client.world == null) return;
            if (client.currentScreen != null) return; // Не бить, когда открыт инвентарь

            // НОВЫЕ ПРОВЕРКИ:
            if (client.player.isUsingItem()) return; // Не бить, когда едим/пьем/блокируем
            if (client.player.isSneaking()) return;  // Не бить, когда сидим (крадемся)

            long now = System.currentTimeMillis();
            
            // ИСПРАВЛЕННЫЙ РАНДОМ: разброс от -50 до +50 мс
            int currentDelay = DELAY_MS;
            if (RANDOM_DELAY) {
                currentDelay += (random.nextInt(101) - 50); 
            }

            if (now - lastAttack < currentDelay) return;

            if (client.crosshairTarget instanceof EntityHitResult hitResult) {
                Entity target = hitResult.getEntity();

                if (!(target instanceof LivingEntity living) || !living.isAlive()) return;
                if (IGNORE_MOBS && target instanceof MobEntity) return;
                if (target instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) return;
                if (client.player.distanceTo(target) > RANGE) return;

                // Если падаем - бьем критом
                if (client.player.fallDistance > 0.0F && !client.player.isOnGround()) {
                    attack(client, target);
                    lastAttack = now;
                } 
                // Если на земле - бьем обычным ударом
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
