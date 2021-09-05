package com.plusls.carpet.mixin.rule.playerOperationLimiter;

import com.plusls.carpet.util.rule.playerOperationLimiter.SafeServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements SafeServerPlayerEntity {
    private int instaBreakCountPerTick = 0;
    private int placeBlockCountPerTick = 0;

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void resetOperationCountPerTick(CallbackInfo ci) {
        instaBreakCountPerTick = 0;
        placeBlockCountPerTick = 0;
    }

    @Override
    public int getInstaBreakCountPerTick() {
        return instaBreakCountPerTick;
    }

    @Override
    public int getPlaceBlockCountPerTick() {
        return placeBlockCountPerTick;
    }

    @Override
    public void addInstaBreakCountPerTick() {
        ++instaBreakCountPerTick;
    }

    @Override
    public void addPlaceBlockCountPerTick() {
        ++placeBlockCountPerTick;
    }

    @Override
    public boolean allowOperation() {
        return (instaBreakCountPerTick == 0 || placeBlockCountPerTick == 0) && (instaBreakCountPerTick <= 1 && placeBlockCountPerTick <= 2);
    }
}
