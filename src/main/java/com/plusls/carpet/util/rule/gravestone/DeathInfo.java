package com.plusls.carpet.util.rule.gravestone;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.CompoundTag;

public class DeathInfo {
    public final long deathTime;
    public final int xp;
    public final SimpleInventory inventory;

    public DeathInfo(long deathTime, int xp, SimpleInventory inv) {
        this.deathTime = deathTime;
        this.xp = xp;
        this.inventory = inv;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("DeathTime", this.deathTime);
        tag.putInt("XP", this.xp);
        tag.put("Items", this.inventory.toNbtList());
        return tag;
    }

    public static DeathInfo fromTag(CompoundTag tag) {
        long deathTime = tag.getLong("DeathTime");
        int xp = tag.getInt("XP");
        SimpleInventory inventory = new SimpleInventory(GravestoneUtil.PLAYER_INVENTORY_SIZE);
        // COMPOUND_TYPE
        inventory.readNbtList(tag.getList("Items", 10));
        return new DeathInfo(deathTime, xp, inventory);
    }
}