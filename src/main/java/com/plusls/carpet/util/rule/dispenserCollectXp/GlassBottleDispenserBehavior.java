package com.plusls.carpet.util.rule.dispenserCollectXp;

import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.dispenser.MyFallibleItemDispenserBehavior;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public class GlassBottleDispenserBehavior extends MyFallibleItemDispenserBehavior {
    private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

    public GlassBottleDispenserBehavior(DispenserBehavior oldDispenserBehavior) {
        super(oldDispenserBehavior);
    }

    public static void init() {
        DispenserBlock.registerBehavior(Items.GLASS_BOTTLE,
                new GlassBottleDispenserBehavior(DispenserBlock.BEHAVIORS.get(Items.GLASS_BOTTLE)));
    }

    private ItemStack replaceItem(BlockPointer pointer, ItemStack oldItem, ItemStack newItem) {
        oldItem.decrement(1);
        if (oldItem.isEmpty())
            return newItem.copy();
        if (((DispenserBlockEntity) pointer.getBlockEntity()).addToFirstFreeSlot(newItem.copy()) < 0)
            this.fallbackBehavior.dispense(pointer, newItem.copy());
        return oldItem;
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack itemStack) {
        if (!PcaSettings.dispenserCollectXp) {
            return itemStack;
        }
        BlockPos faceBlockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));

        List<ExperienceOrbEntity> xpEntityList = pointer.getWorld().getEntitiesByClass(ExperienceOrbEntity.class,
                new Box(faceBlockPos), Entity::isAlive);

        int currentXp = 0;
        // 运算次数不多，所以多循环几次也无所谓（放弃思考.jpg
        for (ExperienceOrbEntity xpEntity : xpEntityList) {
            for (; xpEntity.pickingCount > 0; --xpEntity.pickingCount) {
                currentXp += xpEntity.getExperienceAmount();
                if (xpEntity.pickingCount == 1) {
                    // 有残留经验也无所谓，直接把经验球销毁
                    // 付出点代价很合理
                    xpEntity.discard();
                }
                if (currentXp >= 8) {
                    setSuccess(true);
                    return this.replaceItem(pointer, itemStack, new ItemStack(Items.EXPERIENCE_BOTTLE));
                }
            }
        }
        return itemStack;
    }
}