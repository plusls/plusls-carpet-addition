package com.plusls.carpet.mixin.rule.spawnYRange;

import com.plusls.carpet.PcaSettings;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
    @Redirect(method = "getRandomPosInChunkSection",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/MathHelper;nextBetween(Lnet/minecraft/util/math/random/Random;II)I",
                    ordinal = 0))
    private static int modifySpawnY(Random random, int min, int max) {
        if (PcaSettings.spawnYMax != PcaSettings.INT_DISABLE) {
            max = PcaSettings.spawnYMax;
        }
        if (PcaSettings.spawnYMin != PcaSettings.INT_DISABLE) {
            min = PcaSettings.spawnYMin;
        }
        if (min >= max) {
            max = min + 1;
        }
        return MathHelper.nextBetween(random, min, max);
    }
}
