package com.plusls.carpet.mixin.rule.playerSit;

import com.plusls.carpet.util.rule.playerSit.SitEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntity.class)
public abstract class MixinArmorStandEntity extends LivingEntity implements SitEntity {
    private boolean sitEntity = false;

    protected MixinArmorStandEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    protected abstract void setMarker(boolean marker);

    @Override
    public boolean isSitEntity() {
        return sitEntity;
    }

    @Override
    public void setSitEntity(boolean isSitEntity) {
        this.sitEntity = isSitEntity;
        this.setMarker(isSitEntity);
        this.setInvisible(isSitEntity);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (this.isSitEntity()) {
            this.updatePosition(this.getX(), this.getY() + 0.16, this.getZ());
            this.kill();
        }
        super.removePassenger(passenger);
    }

    @Inject(method = "writeCustomDataToTag", at = @At(value = "RETURN"))
    private void postWriteCustomDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        if (this.sitEntity) {
            nbt.putBoolean("SitEntity", true);
        }
    }

    @Inject(method = "readCustomDataFromTag", at = @At(value = "RETURN"))
    private void postReadCustomDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("SitEntity", 99)) {
            this.sitEntity = nbt.getBoolean("SitEntity");
        }
    }
}
