package com.plusls.carpet.mixin;

import com.plusls.carpet.PcaMod;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashReport.class)
public class MixinCrashReport {
    /*
    This mixins sole purpose is to get the extension classes loaded in the appropriate moment
    After carpet has loaded its settings class, and before anything else loads in the game
     */
    @Inject(method = "initCrashReport", at = @At("HEAD"))
    private static void gameStarted(CallbackInfo ci) {
        PcaMod.noop();
    }
}