package com.plusls.carpet.mixin.rule.pcaSyncProtocol.block;

import com.plusls.carpet.PcaMod;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.Tickable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable {

    protected MixinBrewingStandBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (PcaSyncProtocol.syncBlockEntityToClient(this)) {
            PcaMod.LOGGER.debug("update BrewingStandBlockEntity: {}", this.pos);
        }
    }
}