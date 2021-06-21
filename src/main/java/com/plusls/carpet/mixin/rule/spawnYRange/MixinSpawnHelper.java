package com.plusls.carpet.mixin.rule.spawnYRange;

import com.plusls.carpet.PcaSettings;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
    @Redirect(method = "getSpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;nextBetween(Ljava/util/Random;II)I", ordinal = 0))
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
