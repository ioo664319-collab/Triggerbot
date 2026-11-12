package ru.rocketway.triggerbot.bot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class TriggerBot {
    public static Entity findTarget(MinecraftClient mc, double range, boolean ignoreMobs) {
        Entity best = null;
        double bestDist = range * range;
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            if (!(e instanceof LivingEntity living) || !living.isAlive()) continue;
            if (ignoreMobs && e instanceof MobEntity) continue;
            if (e instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) continue;
            double d = mc.player.squaredDistanceTo(e);
            if (d <= bestDist) { bestDist = d; best = e; }
        }
        return best;
    }

    public static void attack(MinecraftClient mc, Entity target) {
        if (mc.interactionManager == null) return;
        mc.player.lookAt(net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor.EYES, target.getPos());
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
}
