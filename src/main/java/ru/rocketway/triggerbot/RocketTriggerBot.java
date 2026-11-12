package ru.rocketway.triggerbot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
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
    
    // Настройки AIM ASSIST
    public static boolean AIM_ASSIST = false;
    public static double AIM_SPEED = 2.0; // Сила наведения (от 0.1 до 10.0)

    private long lastAttack = 0L;
    private final Random random = new Random();

    @Override
    public void onInitializeClient() {
        Keybinds.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (client.currentScreen != null) return;

            // ЛОГИКА AIM ASSIST (Плавное наведение)
            if (AIM_ASSIST) {
                Entity target = ru.rocketway.triggerbot.bot.TriggerBot.findTarget(client, RANGE, IGNORE_MOBS);
                if (target != null) {
                    // Вычисляем разницу между нашим прицелом и целью
                    double diffX = target.getX() - client.player.getX();
                    double diffY = (target.getY() + target.getHeight() / 2.0) - (client.player.getY() + client.player.getStandingEyeHeight());
                    double diffZ = target.getZ() - client.player.getZ();
                    double dist = MathHelper.sqrt((float)(diffX * diffX + diffZ * diffZ));

                    // Вычисляем идеальные углы поворота (Yaw - влево/вправо, Pitch - вверх/вниз)
                    float targetYaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX))) - 90.0F;
                    float targetPitch = (float) (-Math.toDegrees(Math.atan2(diffY, dist)));

                    // Плавно двигаем камеру к цели (магия AIM_SPEED)
                    float speed = (float) (AIM_SPEED * 0.1);
                    float currentYaw = client.player.getYaw();
                    float currentPitch = client.player.getPitch();

                    // Вычисляем разницу в градусах
                    float deltaYaw = MathHelper.wrapDegrees(targetYaw - currentYaw);
                    float deltaPitch = MathHelper.wrapDegrees(targetPitch - currentPitch);

                    // Если прицел уже наведен (разница меньше 1 градуса), не дергаем камеру
                    if (Math.abs(deltaYaw) > 1.0F || Math.abs(deltaPitch) > 1.0F) {
                        client.player.setYaw(currentYaw + deltaYaw * speed);
                        client.player.setPitch(currentPitch + deltaPitch * speed);
                    }
                }
            }

            // ЛОГИКА ТРИГГЕРБОТА (Удары)
            if (!ENABLED) return;
            if (client.player.isUsingItem() || client.player.isSneaking()) return;

            long now = System.currentTimeMillis();
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
