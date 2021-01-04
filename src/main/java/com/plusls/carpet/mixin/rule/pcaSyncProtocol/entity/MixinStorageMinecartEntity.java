package com.plusls.carpet.mixin.rule.pcaSyncProtocol.entity;

import com.plusls.carpet.PcaMod;
import com.plusls.carpet.network.PcaSyncProtocol;
import com.plusls.carpet.util.IItemStackMonitor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StorageMinecartEntity.class)
public abstract class MixinStorageMinecartEntity extends AbstractMinecartEntity implements Inventory, NamedScreenHandlerFactory {
    protected MixinStorageMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getStack", at = @At(value = "RETURN"))
    private void preGetStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        ((IItemStackMonitor) (Object) cir.getReturnValue()).setEntityMonitor(this);
    }

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At(value = "HEAD"))
    private void preRemoveStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (PcaSyncProtocol.syncEntityToClient(this)) {
            PcaMod.LOGGER.debug("update StorageMinecartEntity inventory: removeStack(II).");
        }
    }

    @Inject(method = "removeStack(I)Lnet/minecraft/item/ItemStack;", at = @At(value = "HEAD"))
    private void preRemoveStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (PcaSyncProtocol.syncEntityToClient(this)) {
            PcaMod.LOGGER.debug("update StorageMinecartEntity inventory: removeStack(I).");
        }
    }

    @Inject(method = "setStack", at = @At(value = "HEAD"))
    void preSetStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (PcaSyncProtocol.syncEntityToClient(this)) {
            PcaMod.LOGGER.debug("update StorageMinecartEntity inventory: setStack.");
        }
    }
}
