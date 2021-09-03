package com.plusls.carpet.util.rule.gravestone;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;

public class DeathInfo {
    public final long deathTime;
    public final int xp;
    public final ArrayList<ItemStack> inventory;

    public DeathInfo(long deathTime, int xp, ArrayList<ItemStack> inv) {
        this.deathTime = deathTime;
        this.xp = xp;
        this.inventory = inv;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("DeathTime", this.deathTime);
        tag.putInt("XP", this.xp);
        tag.put("Items", toNbtList(this.inventory));
        return tag;
    }

    public static DeathInfo fromTag(CompoundTag tag) {
        long deathTime = tag.getLong("DeathTime");
        int xp = tag.getInt("XP");
        ArrayList<ItemStack> inventory = new ArrayList<>();
        // COMPOUND_TYPE
        readNbtList(inventory, tag.getList("Items", 10));
        return new DeathInfo(deathTime, xp, inventory);
    }


    public static void readNbtList(ArrayList<ItemStack> inventory, ListTag nbtList) {
        for (int i = 0; i < nbtList.size(); ++i) {
            ItemStack itemStack = ItemStack.fromTag(nbtList.getCompound(i));
            if (!itemStack.isEmpty()) {
                inventory.add(itemStack);
            }
        }
    }

    public static ListTag toNbtList(ArrayList<ItemStack> inventory) {
        ListTag nbtList = new ListTag();

        for (ItemStack itemStack : inventory) {
            if (!itemStack.isEmpty()) {
                nbtList.add(itemStack.toTag(new CompoundTag()));
            }
        }

        return nbtList;
    }
}