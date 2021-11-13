package com.plusls.carpet.mixin.rule.pcaSyncProtocol.entity;

import com.plusls.carpet.ModInfo;
import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Npc;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.village.Merchant;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MerchantEntity.class)
public abstract class MixinMerchantEntity extends PassiveEntity implements Npc, Merchant, InventoryChangedListener {
    @Final
    @Shadow
    private SimpleInventory inventory;

    protected MixinMerchantEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At(value = "RETURN"))
    private void addInventoryListener(EntityType<? extends MerchantEntity> entityType, World world, CallbackInfo info) {
        if (this.world.isClient()) {
            return;
        }
        this.inventory.addListener(this);
    }

    @Override
    public void onInventoryChanged(Inventory inventory) {
        if (PcaSettings.pcaSyncProtocol && PcaSyncProtocol.syncEntityToClient(this)) {
            ModInfo.LOGGER.debug("update villager inventory: onInventoryChanged.");
        }
    }
}