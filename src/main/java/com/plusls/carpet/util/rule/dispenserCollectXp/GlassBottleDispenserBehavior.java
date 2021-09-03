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

    public static void init() {
        DispenserBlock.registerBehavior(Items.GLASS_BOTTLE,
                new GlassBottleDispenserBehavior(DispenserBlock.BEHAVIORS.get(Items.GLASS_BOTTLE)));
    }

    public GlassBottleDispenserBehavior(DispenserBehavior oldDispenserBehavior) {
        super(oldDispenserBehavior);
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
        BlockPos faceBlockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));

        List<ExperienceOrbEntity> xpEntityList = pointer.getWorld().getEntities(ExperienceOrbEntity.class,
                new Box(faceBlockPos), Entity::isAlive);

        int currentXp = 0;
        // 运算次数不多，所以多循环几次也无所谓（放弃思考.jpg
        for (ExperienceOrbEntity xpEntity : xpEntityList) {
            currentXp += xpEntity.getExperienceAmount();
            if (currentXp >= 8) {
                setSuccess(true);
                if (currentXp == 8) {
                    xpEntity.remove();
                } else {
                    xpEntity.amount = currentXp - 8;
                }
                return this.replaceItem(pointer, itemStack, new ItemStack(Items.EXPERIENCE_BOTTLE));
            }
            xpEntity.remove();
        }
        return itemStack;
    }
}