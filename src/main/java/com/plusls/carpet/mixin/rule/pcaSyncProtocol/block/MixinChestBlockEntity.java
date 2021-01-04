package com.plusls.carpet.mixin.rule.pcaSyncProtocol.block;

import com.plusls.carpet.PcaMod;
import com.plusls.carpet.network.PcaSyncProtocol;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.util.Tickable;
import org.spongepowered.asm.mixin.Mixin;

// 由于陷阱箱继承自箱子，因此不用 mixin 陷阱箱
// implements ChestAnimationProgress 会出错 不知道为啥
@Mixin(ChestBlockEntity.class)
public abstract class MixinChestBlockEntity extends LootableContainerBlockEntity implements Tickable {

    protected MixinChestBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (PcaSyncProtocol.syncBlockEntityToClient(this)) {
            PcaMod.LOGGER.debug("update ChestBlockEntity: {}", this.pos);
        }
    }
}
