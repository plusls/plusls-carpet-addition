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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {

    @Final
    @Shadow
    protected ServerPlayerEntity player;

    @Shadow
    protected ServerWorld world;

    @Inject(method = "processBlockBreakingAction", at=@At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;finishMining(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Ljava/lang/String;)V", ordinal = 1), cancellable = true)
    private void checkOperationCountPerTick(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, CallbackInfo ci) {
        if (!PcaSettings.playerOperationLimiter) {
            return;
        }
        SafeServerPlayerEntity safeServerPlayerEntity = (SafeServerPlayerEntity)player;
        safeServerPlayerEntity.addInstaBreakCountPerTick();
        if (!safeServerPlayerEntity.allowOperation()) {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, "insta mine"));
            ci.cancel();
        }
    }

}
