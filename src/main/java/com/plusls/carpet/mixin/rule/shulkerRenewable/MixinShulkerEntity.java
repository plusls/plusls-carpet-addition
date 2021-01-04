package com.plusls.carpet.mixin.rule.shulkerRenewable;

import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.rule.shulkerRenewable.INewShulkerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerEntity.class)
public abstract class MixinShulkerEntity extends GolemEntity implements Monster, INewShulkerEntity {

    @Final
    @Shadow
    protected static TrackedData<Byte> COLOR;

    @Shadow
    protected abstract boolean isClosed();

    @Shadow
    protected abstract boolean tryTeleport();

    protected MixinShulkerEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    private boolean tp = false;

    private void spawnNewShulker() {
        Vec3d lv = getPos();
        Box lv2 = getBoundingBox();
        if (isClosed() || !tryTeleport())
            return;
        int i = this.world.getEntitiesByType(EntityType.SHULKER, lv2.expand(8.0D), Entity::isAlive).size();
        float f = (i - 1) / 5.0F;
        if (this.world.random.nextFloat() < f)
            return;
        ShulkerEntity lv3 = EntityType.SHULKER.create(this.world);
        if (lv3 == null) {
            return;
        }
        DyeColor lv4 = getColor();
        if (lv4 != null)
            ((INewShulkerEntity) lv3).setColor(lv4);
        lv3.refreshPositionAfterTeleport(lv);
        this.world.spawnEntity(lv3);
    }

    @Inject(method = "tryTeleport", at = @At(value = "HEAD"))
    private void setTpFlag(CallbackInfoReturnable<Boolean> cir) {
        tp = true;
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/GolemEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0))
    private void clearTpFlag(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        tp = false;
    }

    @Inject(method = "damage", at = @At(value = "RETURN"))
    private void tryRenewShulker(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (PcaSettings.shulkerRenewable && cir.getReturnValue()) {
            if (!tp && source.isProjectile()) {
                Entity lv2 = source.getSource();
                if (lv2 != null && lv2.getType() == EntityType.SHULKER_BULLET)
                    spawnNewShulker();
            }
        }
    }

    @Override
    public void setColor(DyeColor color) {
        this.dataTracker.set(COLOR, (byte) color.getId());
    }

    @Nullable
    @Override
    public DyeColor getColor() {
        byte b = this.dataTracker.get(COLOR);
        if (b > 15)
            return null;
        return DyeColor.byId(b);
    }
}
