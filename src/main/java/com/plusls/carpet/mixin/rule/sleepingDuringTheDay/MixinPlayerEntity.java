package com.plusls.carpet.mixin.rule.sleepingDuringTheDay;

import com.plusls.carpet.PcaSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    // 白天不会被叫醒
    @Redirect(method = "tick()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;wakeUp(ZZ)V", ordinal = 0))
    void redirectWakeUp(PlayerEntity player, boolean updateSleepTimer, boolean updateSleepingPlayers) {
        if (!PcaSettings.sleepingDuringTheDay) {
            player.wakeUp(updateSleepTimer, updateSleepingPlayers);
        }
    }

    // 在白天也能睡觉
    @Redirect(method = "trySleep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isDay()Z", ordinal = 0))
    boolean redirectIsDay(World world) {
        boolean ret = world.isDay();
        if (ret && PcaSettings.sleepingDuringTheDay) {
            ret = false;
        }
        return ret;
    }
}
