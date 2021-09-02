package com.plusls.carpet.mixin.rule.gravestone;

import com.plusls.carpet.util.rule.gravestone.DeathInfo;
import com.plusls.carpet.util.rule.gravestone.MySkullBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkullBlockEntity.class)
public class MixinSkullBlockEntity implements MySkullBlockEntity {
    private DeathInfo deathInfo;

    @Override
    public DeathInfo getDeathInfo() {
        return deathInfo;
    }

    @Override
    public void setDeathInfo(DeathInfo deathInfo) {
        this.deathInfo = deathInfo;
    }

    @Inject(method = "fromTag", at = @At(value = "RETURN"))
    private void postReadNbt(BlockState state, CompoundTag tag, CallbackInfo ci) {
        // COMPOUND_TYPE
        if (tag.contains("DeathInfo", 10)) {
            deathInfo = DeathInfo.fromTag(tag.getCompound("DeathInfo"));
        }
    }

    @Inject(method = "toTag", at = @At(value = "RETURN"))
    private void postWriteNbt(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        if (deathInfo != null) {
            nbt.put("DeathInfo", deathInfo.toTag());
        }
    }
}
