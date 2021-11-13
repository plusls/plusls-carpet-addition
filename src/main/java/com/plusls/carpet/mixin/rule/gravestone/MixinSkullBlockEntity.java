package com.plusls.carpet.mixin.rule.gravestone;

import com.plusls.carpet.util.rule.gravestone.DeathInfo;
import com.plusls.carpet.util.rule.gravestone.MySkullBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "readNbt", at = @At(value = "RETURN"))
    private void postReadNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("DeathInfo", NbtElement.COMPOUND_TYPE)) {
            deathInfo = DeathInfo.fromTag(nbt.getCompound("DeathInfo"));
        }
    }

    @Inject(method = "writeNbt", at = @At(value = "RETURN"))
    private void postWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        if (deathInfo != null) {
            nbt.put("DeathInfo", deathInfo.toTag());
        }
    }
}
