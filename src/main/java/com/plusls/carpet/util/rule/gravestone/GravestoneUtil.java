package com.plusls.carpet.util.rule.gravestone;

import com.plusls.carpet.PcaMod;
import com.plusls.carpet.PcaSettings;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Objects;


public class GravestoneUtil {
    public static final int NETHER_BEDROCK_MAX_Y = 127;
    public static final int SEARCH_RANGE = 5;
    public static final int PLAYER_INVENTORY_SIZE = 41;

    public static void init() {
        ServerPlayerEvents.ALLOW_DEATH.register((player, damageSource, damageAmount) -> {
            deathHandle(player);
            return true;
        });
    }

    private static void deathHandle(ServerPlayerEntity player) {
        World world = player.world;
        if (PcaSettings.gravestone && !world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            player.vanishCursedItems();
            SimpleInventory inventory = new SimpleInventory(PLAYER_INVENTORY_SIZE);
            for (ItemStack itemStack : player.inventory.main) {
                inventory.addStack(itemStack);
            }

            for (ItemStack itemStack : player.inventory.armor) {
                inventory.addStack(itemStack);
            }

            for (ItemStack itemStack : player.inventory.offHand) {
                inventory.addStack(itemStack);
            }
            int xp = player.totalExperience / 2;
            player.inventory.clear();

            // only need clear experienceLevel
            player.experienceLevel = 0;
            BlockPos gravePos = findGravePos(player);
            Objects.requireNonNull(world.getServer()).send(new ServerTask(world.getServer().getTicks(),
                    placeGraveRunnable(world,
                            gravePos,
                            new DeathInfo(System.currentTimeMillis(), xp, inventory),
                            player)));
        }
    }

    // find pos to place gravestone
    public static BlockPos findGravePos(ServerPlayerEntity player) {
        BlockPos.Mutable playerPos = new BlockPos((player.getPos())).mutableCopy();
        playerPos.setY(clampY(player, playerPos.getY()));
        if (canPlaceGrave(player, playerPos)) {
            return playerPos;
        }
        BlockPos.Mutable gravePos = new BlockPos.Mutable();
        for (int x = playerPos.getX() + SEARCH_RANGE; x >= playerPos.getX() - SEARCH_RANGE; x--) {
            gravePos.setX(x);
            int minY = clampY(player, playerPos.getY() - SEARCH_RANGE);
            for (int y = clampY(player, playerPos.getY() + SEARCH_RANGE); y >= minY; y--) {
                gravePos.setY(y);
                for (int z = playerPos.getZ() + SEARCH_RANGE; z >= playerPos.getZ() - SEARCH_RANGE; z--) {
                    gravePos.setZ(z);
                    if (canPlaceGrave(player, gravePos)) {
                        return drop(player, gravePos);
                    }
                }
            }
        }

        // search up
        gravePos.set(playerPos);
        while (player.world.getBlockState(gravePos).getBlock() == Blocks.BEDROCK) {
            gravePos.setY(gravePos.getY() + 1);
        }
        return gravePos;
    }

    // make sure to spawn graves on the suitable place
    public static int clampY(ServerPlayerEntity player, int y) {
        //don't spawn on nether ceiling, unless the player is already there.
        if (player.world.getRegistryKey() == World.NETHER && y < NETHER_BEDROCK_MAX_Y) {
            //clamp to 1 -- don't spawn graves the layer right above the void, so players can actually recover their items.
            return MathHelper.clamp(y, 1, NETHER_BEDROCK_MAX_Y - 1);
        } else {
            return MathHelper.clamp(y, 1, player.server.getWorldHeight() - 1);
        }
    }


    public static boolean canPlaceGrave(ServerPlayerEntity player, BlockPos pos) {

        BlockState state = player.world.getBlockState(pos);
        if (pos.getY() <= 1 || pos.getY() >= player.server.getWorldHeight() - 1) {
            return false;
        } else if (state.isAir()) {
            return true;
        }
        // block can replace
        else return state.canReplace(
                    new AutomaticItemPlacementContext(player.world, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
    }

    // players are blown up
    // reduce y pos
    public static BlockPos drop(ServerPlayerEntity player, BlockPos pos) {
        BlockPos.Mutable searchPos = new BlockPos.Mutable().set(pos);
        int i = 0;
        for (int y = pos.getY() - 1; y > 1 && i < 10; y--) {
            i++;
            searchPos.setY(clampY(player, y));
            if (!player.world.getBlockState(searchPos).isAir()) {
                searchPos.setY(clampY(player, y + 1));
                return searchPos;
            }
        }
        return pos;
    }

    public static Runnable placeGraveRunnable(World world, BlockPos pos, DeathInfo deathInfo, ServerPlayerEntity player) {
        return () -> {
            BlockState graveBlock = Blocks.PLAYER_HEAD.getDefaultState();

            // avoid setblockstate fail.
            while (!world.setBlockState(pos, graveBlock)) {
                PcaMod.LOGGER.warn(String.format("set gravestone at %d %d %d fail, try again.",
                        pos.getX(), pos.getY(), pos.getZ()));
            }
            SkullBlockEntity graveEntity = (SkullBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos));
            graveEntity.setOwner(player.getGameProfile());
            ((MySkullBlockEntity) graveEntity).setDeathInfo(deathInfo);
            graveEntity.markDirty();
        };
    }
}