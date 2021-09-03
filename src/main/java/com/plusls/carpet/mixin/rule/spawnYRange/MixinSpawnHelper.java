package com.plusls.carpet.mixin.rule.spawnYRange;

import com.plusls.carpet.PcaSettings;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
    @Redirect(method = "method_8657", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 2))
    private static int modifySpawnY(Random random, int bound) {
        int max = bound, min = 0;
        if (PcaSettings.spawnYMax != PcaSettings.INT_DISABLE) {
            max = PcaSettings.spawnYMax + 1;
        }
        if (PcaSettings.spawnYMin != PcaSettings.INT_DISABLE) {
            min = PcaSettings.spawnYMin;
        }
        if (min >= max) {
            return 0;
        }
        int newBound = max - min;
        return random.nextInt(newBound) + min;
    }
}
