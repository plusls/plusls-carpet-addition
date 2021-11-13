package com.plusls.carpet.mixin.rule.pcaSyncProtocol.block;

import com.plusls.carpet.PcaMod;
import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.util.Tickable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractFurnaceBlockEntity.class, priority = 1001)
public abstract class MixinAbstractFurnaceBlockEntityLithumHopperFix extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider, Tickable {

    protected MixinAbstractFurnaceBlockEntityLithumHopperFix(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Dynamic
    @Inject(method = "method_5431", at = @At(value = "HEAD"), remap = false)
    //@Inject(method = "markDirty", at = @At(value = "HEAD"), remap = false)
    public void preMarkDirty(CallbackInfo ci) {
        if (PcaSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            PcaMod.LOGGER.debug("update AbstractFurnaceBlockEntity: {}", this.pos);
        }
    }
}