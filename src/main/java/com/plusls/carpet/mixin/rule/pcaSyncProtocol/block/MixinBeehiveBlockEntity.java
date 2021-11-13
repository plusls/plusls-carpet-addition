package com.plusls.carpet.mixin.rule.pcaSyncProtocol.block;

import com.plusls.carpet.PcaMod;
import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(BeehiveBlockEntity.class)
public abstract class MixinBeehiveBlockEntity extends BlockEntity {

    public MixinBeehiveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tickBees", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V", shift = At.Shift.AFTER))
    private static void postTickBees(World world, BlockPos pos, BlockState state, List<BeehiveBlockEntity.Bee> bees, BlockPos flowerPos, CallbackInfo ci) {
        if (PcaSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(Objects.requireNonNull(world.getBlockEntity(pos)))) {
            PcaMod.LOGGER.debug("update BeehiveBlockEntity: {}", pos);
        }
    }

    @Inject(method = "tryReleaseBee", at = @At(value = "RETURN"))
    public void postTryReleaseBee(BlockState state, BeehiveBlockEntity.BeeState beeState, CallbackInfoReturnable<List<Entity>> cir) {
        if (PcaSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this) && cir.getReturnValue() != null) {
            PcaMod.LOGGER.debug("update BeehiveBlockEntity: {}", this.pos);
        }
    }

    @Inject(method = "readNbt", at = @At(value = "RETURN"))
    public void postFromTag(NbtCompound nbt, CallbackInfo ci) {
        if (PcaSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            PcaMod.LOGGER.debug("update BeehiveBlockEntity: {}", this.pos);
        }
    }

    @Inject(method = "tryEnterHive(Lnet/minecraft/entity/Entity;ZI)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;discard()V", ordinal = 0))
    public void postEnterHive(Entity entity, boolean hasNectar, int ticksInHive, CallbackInfo ci) {
        if (PcaSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            PcaMod.LOGGER.debug("update BeehiveBlockEntity: {}", this.pos);
        }
    }
}