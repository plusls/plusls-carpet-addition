package com.plusls.carpet.mixin.rule.pcaSyncProtocol.entity;

import com.plusls.carpet.PcaMod;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StorageMinecartEntity.class)
public abstract class MixinStorageMinecartEntity extends AbstractMinecartEntity implements Inventory, NamedScreenHandlerFactory {
    protected MixinStorageMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "markDirty", at = @At(value = "RETURN"))
    private void updateInventory(CallbackInfo ci) {
        if (PcaSyncProtocol.syncEntityToClient(this)) {
            PcaMod.LOGGER.debug("update StorageMinecartEntity inventory.");
        }
    }
}
