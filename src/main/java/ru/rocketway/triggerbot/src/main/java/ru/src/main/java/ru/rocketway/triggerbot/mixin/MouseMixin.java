package ru.rocketway.triggerbot.mixin;

import net.minecraft.client.Mouse;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.rocketway.triggerbot.RocketTriggerBot;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "updateMouse", at = @At("HEAD"))
    private void onUpdateMouse(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        if (!RocketTriggerBot.AIM_ASSIST) return;
        
        // Отключаем ассист, если едим или сидим
        if (client.player.isUsingItem() || client.player.isSneaking()) return;
        if (client.currentScreen != null) return;

        Entity target = ru.rocketway.triggerbot.bot.TriggerBot.findTarget(client, RocketTriggerBot.RANGE, RocketTriggerBot.IGNORE_MOBS);
        if (target != null) {
            // Вычисляем, куда нам нужно смотреть
            double diffX = target.getX() - client.player.getX();
            double diffY = (target.getY() + target.getHeight() / 2.0) - (client.player.getY() + client.player.getStandingEyeHeight());
            double diffZ = target.getZ() - client.player.getZ();
            double dist = MathHelper.sqrt((float)(diffX * diffX + diffZ * diffZ));

            float targetYaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX))) - 90.0F;
            float targetPitch = (float) (-Math.toDegrees(Math.atan2(diffY, dist)));

            float deltaYaw = MathHelper.wrapDegrees(targetYaw - client.player.getYaw());
            float deltaPitch = MathHelper.wrapDegrees(targetPitch - client.player.getPitch());

            // Делаем плавно (чем выше AIM_SPEED, тем сильнее тянет)
            float speed = (float) (RocketTriggerBot.AIM_SPEED * 0.01);
            
            // Чтобы не дергалось, ограничиваем максимальный поворот за кадр
            float maxTurn = 1.5F; 
            float moveYaw = MathHelper.clamp(deltaYaw * speed, -maxTurn, maxTurn);
            float movePitch = MathHelper.clamp(deltaPitch * speed, -maxTurn, maxTurn);

            // Двигаем камеру
            client.player.setYaw(client.player.getYaw() + moveYaw);
            client.player.setPitch(client.player.getPitch() + movePitch);
        }
    }
}
