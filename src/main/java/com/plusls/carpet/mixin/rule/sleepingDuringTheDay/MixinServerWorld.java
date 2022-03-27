package com.plusls.carpet.mixin.rule.sleepingDuringTheDay;

import com.plusls.carpet.PcaSettings;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiFunction;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends World {

    protected MixinServerWorld(LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> chunkManagerProvider, Profiler profiler, boolean isClient) {
        super(levelProperties, dimensionType, chunkManagerProvider, profiler, isClient);
    }

    // 根据当前时间设置夜晚和白天
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V", ordinal = 0))
    void redirectSetTimeOfDay(ServerWorld world, long timeOfDay) {
        if (this.isDay() && PcaSettings.sleepingDuringTheDay) {
            long currentTime = this.properties.getTimeOfDay();
            long currentDayTime = this.properties.getTimeOfDay() % 24000L;
            world.setTimeOfDay(currentTime + 13000L - currentDayTime);
        } else {
            world.setTimeOfDay(timeOfDay);
        }
    }
}
