package com.plusls.carpet.mixin.rule.playerOperationLimiter;

import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.rule.playerOperationLimiter.SafeServerPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    public ServerWorld world;

    @Inject(method = "method_14263", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;method_21717(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;)V", ordinal = 1), cancellable = true)
    private void checkOperationCountPerTick(BlockPos blockPos, PlayerActionC2SPacket.Action action, Direction direction, int i, CallbackInfo ci) {
        if (!PcaSettings.playerOperationLimiter) {
            return;
        }
        SafeServerPlayerEntity safeServerPlayerEntity = (SafeServerPlayerEntity) player;
        safeServerPlayerEntity.addInstaBreakCountPerTick();
        if (!safeServerPlayerEntity.allowOperation()) {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(blockPos, this.world.getBlockState(blockPos), action, false));
            ci.cancel();
        }
    }

}
