package com.plusls.carpet.network;

import com.plusls.carpet.PcaMod;
import com.plusls.carpet.PcaSettings;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class PcaSyncProtocol {

    // 发送包
    private static final Identifier ENABLE_PCA_SYNC_PROTOCOL = PcaMod.id("enable_pca_sync_protocol");
    private static final Identifier DISABLE_PCA_SYNC_PROTOCOL = PcaMod.id("disable_pca_sync_protocol");
    private static final Identifier UPDATE_ENTITY = PcaMod.id("update_entity");
    private static final Identifier UPDATE_BLOCK_ENTITY = PcaMod.id("update_block_entity");

    // 通知客户端服务器已启用 PcaSyncProtocol
    public static void enablePcaSyncProtocol(@NotNull ServerPlayerEntity player) {
        if (ServerPlayNetworking.canSend(player, ENABLE_PCA_SYNC_PROTOCOL)) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            ServerPlayNetworking.send(player, ENABLE_PCA_SYNC_PROTOCOL, buf);
            PcaMod.LOGGER.debug("send enablePcaSyncProtocol to {}!", player);
        }
    }

    // 通知客户端服务器已停用 PcaSyncProtocol
    public static void disablePcaSyncProtocol(@NotNull ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ServerPlayNetworking.send(player, DISABLE_PCA_SYNC_PROTOCOL, buf);
        PcaMod.LOGGER.debug("send disablePcaSyncProtocol to {}!", player);
    }

    // 通知客户端更新 Entity
    // 包内包含 World 的 Identifier, entityId, entity 的 nbt 数据
    // 传输 World 是为了通知客户端该 Entity 属于哪个 World
    public static void updateEntity(@NotNull ServerPlayerEntity player, @NotNull Entity entity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeIdentifier(entity.getEntityWorld().getRegistryKey().getValue());
        buf.writeInt(entity.getEntityId());
        buf.writeCompoundTag(entity.toTag(new CompoundTag()));
        ServerPlayNetworking.send(player, UPDATE_ENTITY, buf);
    }

    // 通知客户端更新 BlockEntity
    // 包内包含 World 的 Identifier, pos, blockEntity 的 nbt 数据
    // 传输 World 是为了通知客户端该 BlockEntity 属于哪个世界
    public static void updateBlockEntity(@NotNull ServerPlayerEntity player, @NotNull BlockEntity blockEntity) {
        World world = blockEntity.getWorld();

        // 在生成世界时可能会产生空指针
        if (world == null) {
            return;
        }

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeIdentifier(world.getRegistryKey().getValue());
        buf.writeBlockPos(blockEntity.getPos());
        buf.writeCompoundTag(blockEntity.toTag(new CompoundTag()));
        ServerPlayNetworking.send(player, UPDATE_BLOCK_ENTITY, buf);
    }


    // 响应包
    private static final Identifier SYNC_BLOCK_ENTITY = PcaMod.id("sync_block_entity");
    private static final Identifier SYNC_ENTITY = PcaMod.id("sync_entity");
    private static final Identifier CANCEL_SYNC_BLOCK_ENTITY = PcaMod.id("cancel_sync_block_entity");
    private static final Identifier CANCEL_SYNC_ENTITY = PcaMod.id("cancel_sync_entity");

    private static final Map<ServerPlayerEntity, Pair<Identifier, BlockPos>> playerWatchBlockPos = new HashMap<>();
    private static final Map<ServerPlayerEntity, Pair<Identifier, Entity>> playerWatchEntity = new HashMap<>();

    private static final Map<Pair<Identifier, BlockPos>, Set<ServerPlayerEntity>> blockPosWatchPlayerSet = new HashMap<>();
    private static final Map<Pair<Identifier, Entity>, Set<ServerPlayerEntity>> entityWatchPlayerSet = new HashMap<>();

    public static final ReentrantLock lock = new ReentrantLock(true);

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(SYNC_BLOCK_ENTITY, PcaSyncProtocol::syncBlockEntityHandler);
        ServerPlayNetworking.registerGlobalReceiver(SYNC_ENTITY, PcaSyncProtocol::syncEntityHandler);
        ServerPlayNetworking.registerGlobalReceiver(CANCEL_SYNC_BLOCK_ENTITY, PcaSyncProtocol::cancelSyncBlockEntityHandler);
        ServerPlayNetworking.registerGlobalReceiver(CANCEL_SYNC_ENTITY, PcaSyncProtocol::cancelSyncEntityHandler);
    }

    // 客户端通知服务端取消 BlockEntity 同步
    private static void cancelSyncBlockEntityHandler(MinecraftServer server, ServerPlayerEntity player,
                                                     ServerPlayNetworkHandler handler, PacketByteBuf buf,
                                                     PacketSender responseSender) {
        if (!PcaSettings.pcaSyncProtocol) {
            return;
        }
        PcaMod.LOGGER.debug("{} cancel watch blockEntity.", player.getName().asString());
        PcaSyncProtocol.clearPlayerWatchBlock(player);
    }

    // 客户端通知服务端取消 Entity 同步
    private static void cancelSyncEntityHandler(MinecraftServer server, ServerPlayerEntity player,
                                                ServerPlayNetworkHandler handler, PacketByteBuf buf,
                                                PacketSender responseSender) {
        if (!PcaSettings.pcaSyncProtocol) {
            return;
        }
        PcaMod.LOGGER.debug("{} cancel watch entity.", player.getName().asString());
        PcaSyncProtocol.clearPlayerWatchEntity(player);
    }

    // 客户端请求同步 BlockEntity
    // 包内包含 pos
    // 由于正常的场景一般不会跨世界请求数据，因此包内并不包含 World，以玩家所在的 World 为准
    private static void syncBlockEntityHandler(MinecraftServer server, ServerPlayerEntity player,
                                               ServerPlayNetworkHandler handler, PacketByteBuf buf,
                                               PacketSender responseSender) {
        if (!PcaSettings.pcaSyncProtocol) {
            return;
        }
        BlockPos pos = buf.readBlockPos();
        ServerWorld world = player.getServerWorld();
        BlockState blockState = world.getBlockState(pos);
        clearPlayerWatchData(player);
        PcaMod.LOGGER.debug("{} watch blockpos {}: {}", player.getName().asString(), pos, blockState);

        // 不是单个箱子则需要更新隔壁箱子
        if (blockState.getBlock() instanceof ChestBlock && blockState.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
            BlockPos posAdj = pos.offset(ChestBlock.getFacing(blockState));
            // The method in World now checks that the caller is from the same thread...
            BlockEntity blockEntityAdj = world.getWorldChunk(posAdj).getBlockEntity(posAdj);
            if (blockEntityAdj != null) {
                updateBlockEntity(player, blockEntityAdj);
            }
        }

        // 本来想判断一下 blockState 类型做个白名单的，考虑到 client 已经做了判断就不在服务端做判断了
        // 就算被恶意攻击应该不会造成什么损失
        // 大不了 op 直接拉黑
        // The method in World now checks that the caller is from the same thread...
        BlockEntity blockEntity = world.getWorldChunk(pos).getBlockEntity(pos);
        if (blockEntity != null) {
            updateBlockEntity(player, blockEntity);
        }

        Pair<Identifier, BlockPos> pair = new ImmutablePair<>(player.getEntityWorld().getRegistryKey().getValue(), pos);
        lock.lock();
        playerWatchBlockPos.put(player, pair);
        if (!blockPosWatchPlayerSet.containsKey(pair)) {
            blockPosWatchPlayerSet.put(pair, new HashSet<>());
        }
        blockPosWatchPlayerSet.get(pair).add(player);
        lock.unlock();
    }

    // 客户端请求同步 Entity
    // 包内包含 entityId
    // 由于正常的场景一般不会跨世界请求数据，因此包内并不包含 World，以玩家所在的 World 为准
    private static void syncEntityHandler(MinecraftServer server, ServerPlayerEntity player,
                                          ServerPlayNetworkHandler handler, PacketByteBuf buf,
                                          PacketSender responseSender) {
        int entityId = buf.readInt();
        ServerWorld world = player.getServerWorld();
        Entity entity = world.getEntityById(entityId);
        if (entity == null) {
            PcaMod.LOGGER.debug("Can't find entity {}.", entityId);
        } else {
            clearPlayerWatchData(player);
            PcaMod.LOGGER.debug("{} watch entity {}: {}", player.getName().asString(), entityId, entity);
            updateEntity(player, entity);

            Pair<Identifier, Entity> pair = new ImmutablePair<>(entity.getEntityWorld().getRegistryKey().getValue(), entity);
            lock.lock();
            playerWatchEntity.put(player, pair);
            if (!entityWatchPlayerSet.containsKey(pair)) {
                entityWatchPlayerSet.put(pair, new HashSet<>());
            }
            entityWatchPlayerSet.get(pair).add(player);
            lock.unlock();
        }
    }

    // 工具
    private static @Nullable Set<ServerPlayerEntity> getWatchPlayerList(@NotNull Entity entity) {
        return entityWatchPlayerSet.get(new ImmutablePair<>(entity.getEntityWorld().getRegistryKey().getValue(), entity));
    }

    private static @Nullable Set<ServerPlayerEntity> getWatchPlayerList(@NotNull World world, @NotNull BlockPos blockPos) {
        return blockPosWatchPlayerSet.get(new ImmutablePair<>(world.getRegistryKey().getValue(), blockPos));
    }

    public static boolean syncEntityToClient(@NotNull Entity entity) {
        lock.lock();
        Set<ServerPlayerEntity> playerList = getWatchPlayerList(entity);
        boolean ret = false;
        if (playerList != null) {
            for (ServerPlayerEntity player : playerList) {
                updateEntity(player, entity);
                ret = true;
            }
        }
        lock.unlock();
        return ret;
    }

    public static boolean syncBlockEntityToClient(@NotNull BlockEntity blockEntity) {
        boolean ret = false;
        World world = blockEntity.getWorld();
        BlockPos pos = blockEntity.getPos();
        // 在生成世界时可能会产生空指针
        if (world != null) {

            BlockState blockState = world.getBlockState(pos);
            lock.lock();
            Set<ServerPlayerEntity> playerList = getWatchPlayerList(world, blockEntity.getPos());

            if (blockState.getBlock() instanceof ChestBlock && blockState.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                // 如果是一个大箱子需要特殊处理
                // 上面不用 isOf 是为了考虑到陷阱箱的情况，陷阱箱继承自箱子
                BlockPos posAdj = pos.offset(ChestBlock.getFacing(blockState));
                if (playerList == null) {
                    playerList = getWatchPlayerList(world, posAdj);
                } else {
                    Set<ServerPlayerEntity> playerListAdj = getWatchPlayerList(world, posAdj);
                    // 如果左右箱子都有人在 watch，则需要 merge watch set
                    if (playerListAdj != null) {
                        playerList.addAll(playerListAdj);
                    }
                }
            }

            if (playerList != null) {
                for (ServerPlayerEntity player : playerList) {
                    updateBlockEntity(player, blockEntity);
                    ret = true;
                }
            }
            lock.unlock();
        }
        return ret;
    }

    private static void clearPlayerWatchEntity(ServerPlayerEntity player) {
        lock.lock();
        Pair<Identifier, Entity> pair = playerWatchEntity.get(player);
        if (pair != null) {
            Set<ServerPlayerEntity> playerSet = entityWatchPlayerSet.get(pair);
            playerSet.remove(player);
            if (playerSet.isEmpty()) {
                entityWatchPlayerSet.remove(pair);
            }
            playerWatchEntity.remove(player);
        }
        lock.unlock();
    }

    private static void clearPlayerWatchBlock(ServerPlayerEntity player) {
        lock.lock();
        Pair<Identifier, BlockPos> pair = playerWatchBlockPos.get(player);
        if (pair != null) {
            Set<ServerPlayerEntity> playerSet = blockPosWatchPlayerSet.get(pair);
            playerSet.remove(player);
            if (playerSet.isEmpty()) {
                blockPosWatchPlayerSet.remove(pair);
            }
            playerWatchBlockPos.remove(player);
        }
        lock.unlock();
    }

    // 停用 PcaSyncProtocol
    public static void disablePcaSyncProtocolGlobal() {
        Set<ServerPlayerEntity> allPlayerSet = new HashSet<>();

        lock.lock();
        allPlayerSet.addAll(playerWatchBlockPos.keySet());
        allPlayerSet.addAll(playerWatchEntity.keySet());
        playerWatchBlockPos.clear();
        playerWatchEntity.clear();
        blockPosWatchPlayerSet.clear();
        entityWatchPlayerSet.clear();
        lock.unlock();

        for (ServerPlayerEntity player : allPlayerSet) {
            disablePcaSyncProtocol(player);
        }
    }

    // 启用 PcaSyncProtocol
    public static void enablePcaSyncProtocolGlobal() {

        for (ServerPlayerEntity player : PcaMod.server.getPlayerManager().getPlayerList()) {
            enablePcaSyncProtocol(player);
        }
    }


    // 删除玩家数据
    public static void clearPlayerWatchData(ServerPlayerEntity player) {
        PcaSyncProtocol.clearPlayerWatchBlock(player);
        PcaSyncProtocol.clearPlayerWatchEntity(player);
    }
}
