package com.plusls.carpet.mixin.rule.pcaSyncProtocol.item;

import com.plusls.carpet.PcaMod;
import com.plusls.carpet.network.PcaSyncProtocol;
import com.plusls.carpet.util.rule.pcaSyncProtocol.IItemStackMonitor;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements IItemStackMonitor {
    private Entity entityMonitor = null;

    @Inject(method = "setCount", at = @At("HEAD"))
    public void preSetCount(int count, CallbackInfo ci) {
        if (entityMonitor != null && count != ((ItemStack)(Object)this).getCount()) {
            if (PcaSyncProtocol.syncEntityToClient(entityMonitor)) {
                PcaMod.LOGGER.debug("update blockEntity ItemStack.setCount");
            }
            entityMonitor = null;
        }
    }

    @Override
    public void setEntityMonitor(Entity entity) {
        entityMonitor = entity;
    }
}