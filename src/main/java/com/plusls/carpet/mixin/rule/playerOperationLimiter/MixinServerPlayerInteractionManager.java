package com.plusls.carpet.mixin.rule.playerOperationLimiter;

import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.rule.playerOperationLimiter.SafeServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class MixinServerPlayerInteractionManager {

    private static final String INSTA_MINE_REASON = "insta mine";

    @Final
    @Shadow
    protected ServerPlayerEntity player;

    @Shadow
    protected ServerWorld world;

    @Shadow
    protected abstract void method_41250(BlockPos pos, boolean success, int sequence, String reason);

    @Inject(method = "finishMining", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0), cancellable = true)
    private void checkOperationCountPerTick(BlockPos pos, int sequence, String reason, CallbackInfo ci) {
        if (!PcaSettings.playerOperationLimiter || !reason.equals(INSTA_MINE_REASON)) {
            return;
        }
        SafeServerPlayerEntity safeServerPlayerEntity = (SafeServerPlayerEntity) player;
        safeServerPlayerEntity.addInstaBreakCountPerTick();
        if (!safeServerPlayerEntity.allowOperation()) {
            this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, this.world.getBlockState(pos)));
            this.method_41250(pos, false, sequence, reason);
            ci.cancel();
        }
    }

}
