package com.plusls.carpet.mixin.rule.useDyeOnShulkerBox;

import com.plusls.carpet.PcaSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DyeItem.class)
public abstract class MixinDyeItem extends Item {
    public MixinDyeItem(Settings settings) {
        super(settings);
    }

    @Shadow
    public abstract DyeColor getColor();

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!PcaSettings.useDyeOnShulkerBox) {
            return ActionResult.PASS;
        }
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState blockState = world.getBlockState(pos);

        if (blockState.isOf(Blocks.SHULKER_BOX)) {
            if (!world.isClient()) {
                ShulkerBoxBlockEntity blockEntity = (ShulkerBoxBlockEntity) world.getBlockEntity(pos);
                BlockState newBlockState = ShulkerBoxBlock.get(getColor()).getDefaultState().
                        with(ShulkerBoxBlock.FACING, blockState.get(ShulkerBoxBlock.FACING));

                if (world.setBlockState(pos, newBlockState)) {
                    ShulkerBoxBlockEntity newBlockEntity = (ShulkerBoxBlockEntity) world.getBlockEntity(pos);
                    assert blockEntity != null;
                    assert newBlockEntity != null;
                    newBlockEntity.readInventoryNbt(blockEntity.createNbt());
                    newBlockEntity.setCustomName(blockEntity.getCustomName());
                    newBlockEntity.markDirty();
                    context.getStack().decrement(1);
                }
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }
}
