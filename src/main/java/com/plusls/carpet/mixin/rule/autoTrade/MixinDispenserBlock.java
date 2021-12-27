package com.plusls.carpet.mixin.rule.autoTrade;

import com.plusls.carpet.PcaSettings;
import com.plusls.carpet.util.rule.autoTrade.MyVillagerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DispenserBlock.class)
public class MixinDispenserBlock {
    private static final ItemDispenserBehavior itemDispenserBehavior = new ItemDispenserBehavior();

    private static void depleteItemInInventory(ItemStack itemStack, Inventory inventory) {
        Item item = itemStack.getItem();
        for (int i = 0; !itemStack.isEmpty() && i < inventory.getInvSize(); ++i) {
            ItemStack tmpItemStack = inventory.getInvStack(i);
            if (!tmpItemStack.isEmpty() && tmpItemStack.getItem() == item) {
                int count = Math.min(itemStack.getCount(), tmpItemStack.getCount());
                itemStack.setCount(itemStack.getCount() - count);
                tmpItemStack.setCount(tmpItemStack.getCount() - count);
            }
        }
    }

    private static ItemStack getItemFromInventory(ItemStack itemStack, Inventory inventory) {
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Item item = itemStack.getItem();
        ItemStack ret = new ItemStack(item, 0);
        for (int i = 0; i < inventory.getInvSize(); ++i) {
            ItemStack tmpStack = inventory.getInvStack(i);
            if (!tmpStack.isEmpty() && tmpStack.getItem() == item) {
                ret.setCount(Math.min(tmpStack.getCount() + ret.getCount(), ret.getMaxCount()));
                if (ret.getCount() == ret.getMaxCount()) {
                    break;
                }
            }
        }
        return ret;
    }

    @Inject(method = "dispense", at = @At(value = "HEAD"), cancellable = true)
    private void autoTrade(World world, BlockPos pos, CallbackInfo ci) {
        if (!PcaSettings.autoTrade || world.isClient) {
            return;
        }
        BlockState state = world.getBlockState(pos.down());
        boolean tradeAll;
        if (state.getBlock() == Blocks.EMERALD_BLOCK) {
            tradeAll = false;
        } else if (state.getBlock() == Blocks.DIAMOND_BLOCK) {
            tradeAll = true;
        } else {
            return;
        }
        BlockPos faceBlockPos = pos.offset(world.getBlockState(pos).get(DispenserBlock.FACING));
        List<AbstractTraderEntity> merchantEntityList = world.getEntities(AbstractTraderEntity.class,
                new Box(faceBlockPos), Entity::isAlive);
        if (merchantEntityList.size() < 1) {
            return;
        }
        AbstractTraderEntity merchantEntity = merchantEntityList.get(0);
        TraderOfferList offerList = merchantEntity.getOffers();
        if (offerList.size() == 0) {
            return;
        }

        int tradeId = world.getReceivedRedstonePower(pos);
        TradeOffer offer = offerList.get(tradeId > offerList.size() ? offerList.size() - 1 : tradeId - 1);
        ItemStack firstItemStack = offer.getAdjustedFirstBuyItem();
        ItemStack secondItemStack = offer.getSecondBuyItem();
        ItemStack firstDepleteItem = firstItemStack.copy();
        ItemStack secondDepleteItem = secondItemStack.copy();

        BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof DispenserBlockEntity)) {
            return;
        }
        DispenserBlockEntity dispenserBlockEntity = (DispenserBlockEntity) blockEntity;
        boolean success = false;
        while (!offer.isDisabled()) {
            ItemStack firstInventoryItemStack = getItemFromInventory(firstItemStack, dispenserBlockEntity);
            ItemStack secondInventoryItemStack = getItemFromInventory(secondItemStack, dispenserBlockEntity);
            int firstItemCount = firstInventoryItemStack.getCount();
            int secondItemCount = secondInventoryItemStack.getCount();
            if (offer.depleteBuyItems(firstInventoryItemStack, secondInventoryItemStack)) {
                firstDepleteItem.setCount(firstItemCount - firstInventoryItemStack.getCount());
                secondDepleteItem.setCount(secondItemCount - secondInventoryItemStack.getCount());
                depleteItemInInventory(firstDepleteItem, dispenserBlockEntity);
                depleteItemInInventory(secondDepleteItem, dispenserBlockEntity);
                offer.use();
                ItemStack outputItemStack = offer.getSellItem();
                itemDispenserBehavior.dispense(blockPointerImpl, outputItemStack);
                // make villager happy ~
                world.sendEntityStatus(merchantEntity, (byte) 14);
                if (merchantEntity instanceof MyVillagerEntity) {
                    ((MyVillagerEntity) merchantEntity).tradeWithoutPlayer(offer);
                }
                success = true;
            } else {
                break;
            }
            if (!tradeAll) {
                break;
            }
        }

        if (success) {
            ci.cancel();
        }
    }

}
