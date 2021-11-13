package com.plusls.carpet.util.rule.gravestone;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class DeathInfo {
    public final long deathTime;
    public final int xp;
    public final SimpleInventory inventory;

    public DeathInfo(long deathTime, int xp, SimpleInventory inv) {
        this.deathTime = deathTime;
        this.xp = xp;
        this.inventory = inv;
    }

    public static DeathInfo fromTag(NbtCompound tag) {
        long deathTime = tag.getLong("DeathTime");
        int xp = tag.getInt("XP");
        SimpleInventory inventory = new SimpleInventory(GravestoneUtil.PLAYER_INVENTORY_SIZE);
        inventory.readNbtList(tag.getList("Items", NbtElement.COMPOUND_TYPE));
        return new DeathInfo(deathTime, xp, inventory);
    }

    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putLong("DeathTime", this.deathTime);
        tag.putInt("XP", this.xp);
        tag.put("Items", this.inventory.toNbtList());
        return tag;
    }
}