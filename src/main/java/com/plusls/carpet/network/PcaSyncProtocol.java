package com.plusls.carpet.network;

import carpet.patches.EntityPlayerMPFake;
import com.plusls.carpet.ModInfo;
import com.plusls.carpet.PcaMod;
import com.plusls.carpet.PcaSettings;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class PcaSyncProtocol {

    public static final ReentrantLock lock = new ReentrantLock(true);
    public static final ReentrantLock pairLock = new ReentrantLock(true);
    // 发送包
    private static final Identifier ENABLE_PCA_SYNC_PROTOCOL = ModInfo.id("enable_pca_sync_protocol");
    private static final Identifier DISABLE_PCA_SYNC_PROTOCOL = ModInfo.id("disable_pca_sync_protocol");
    private static final Identifier UPDATE_ENTITY = ModInfo.id("update_entity");
    private static final Identifier UPDATE_BLOCK_ENTITY = ModInfo.id("update_block_entity");
    // 响应包
    private static final Identifier SYNC_BLOCK_ENTITY = ModInfo.id("sync_block_entity");
    private static final Identifier SYNC_ENTITY = ModInfo.id("sync_entity");
    private static final Identifier CANCEL_SYNC_BLOCK_ENTITY = ModInfo.id("cancel_sync_block_entity");
    private static final Identifier CANCEL_SYNC_ENTITY = ModInfo.id("cancel_sync_entity");
    private static final Map<ServerPlayerEntity, Pair<Identifier, BlockPos>> playerWatchBlockPos = new HashMap<>();
    private static final Map<ServerPlayerEntity, Pair<Identifier, Entity>> playerWatchEntity = new HashMap<>();
    private static final Map<Pair<Identifier, BlockPos>, Set<ServerPlayerEntity>> blockPosWatchPlayerSet = new HashMap<>();
    private static final Map<Pair<Identifier, Entity>, Set<ServerPlayerEntity>> entityWatchPlayerSet = new HashMap<>();
    private static final Set<ServerPlayerEntity> playerSet = new HashSet<>();
    private static final MutablePair<Identifier, Entity> identifierEntityPair = new MutablePair<>();
    private static final MutablePair<Identifier, BlockPos> identifierBlockPosPair = new MutablePair<>();

    // 通知客户端服务器已启用 PcaSyncProtocol
    public static void enablePcaSyncProtocol(@NotNull ServerPlayerEntity player) {
        // 在这写如果是在 BC 端的情况下，ServerPlayNetworking.canSend 在这个时机调用会出现错误
        ModInfo.LOGGER.debug("Try enablePcaSyncProtocol: {}", player.getName().asString());
        // bc 端比较奇怪，canSend 工作不正常
        // if (ServerPlayNetworking.canSend(player, ENABLE_PCA_SYNC_PROTOCOL)) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ServerPlayNetworking.send(player, ENABLE_PCA_SYNC_PROTOCOL, buf);
        ModInfo.LOGGER.debug("send enablePcaSyncProtocol to {}!", player.getName().asString());
        lock.lock();
        lock.unlock();
    }

    // 通知客户端服务器已停用 PcaSyncProtocol
    public static void disablePcaSyncProtocol(@NotNull ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ServerPlayNetworking.send(player, DISABLE_PCA_SYNC_PROTOCOL, buf);
        ModInfo.LOGGER.debug("send disablePcaSyncProtocol to {}!", player.getName().asString());
    }

    // 通知客户端更新 Entity
    // 包内包含 World 的 Identifier, entityId, entity 的 nbt 数据
    // 传输 World 是为了通知客户端该 Entity 属于哪个 World
    public static void updateEntity(@NotNull ServerPlayerEntity player, @NotNull Entity entity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeIdentifier(DimensionType.getId(entity.getEntityWorld().getDimension().getType()));
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
        buf.writeIdentifier(DimensionType.getId(world.getDimension().getType()));
        buf.writeBlockPos(blockEntity.getPos());
        buf.writeCompoundTag(blockEntity.toTag(new CompoundTag()));
        ServerPlayNetworking.send(player, UPDATE_BLOCK_ENTITY, buf);
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(SYNC_BLOCK_ENTITY, PcaSyncProtocol::syncBlockEntityHandler);
        ServerPlayNetworking.registerGlobalReceiver(SYNC_ENTITY, PcaSyncProtocol::syncEntityHandler);
        ServerPlayNetworking.registerGlobalReceiver(CANCEL_SYNC_BLOCK_ENTITY, PcaSyncProtocol::cancelSyncBlockEntityHandler);
        ServerPlayNetworking.registerGlobalReceiver(CANCEL_SYNC_ENTITY, PcaSyncProtocol::cancelSyncEntityHandler);
        ServerPlayConnectionEvents.JOIN.register(PcaSyncProtocol::onJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(PcaSyncProtocol::onDisconnect);
    }

    private static void onDisconnect(ServerPlayNetworkHandler serverPlayNetworkHandler, MinecraftServer minecraftServer) {
        if (PcaSettings.pcaSyncProtocol) {
            ModInfo.LOGGER.debug("onDisconnect remove: {}", serverPlayNetworkHandler.player.getName().asString());
            lock.lock();
            playerSet.remove(serverPlayNetworkHandler.player);
            lock.unlock();
        }
    }

    private static void onJoin(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        if (PcaSettings.pcaSyncProtocol) {
            enablePcaSyncProtocol(serverPlayNetworkHandler.player);
        }
    }

    // 客户端通知服务端取消 BlockEntity 同步
    private static void cancelSyncBlockEntityHandler(MinecraftServer server, ServerPlayerEntity player,
                                                     ServerPlayNetworkHandler handler, PacketByteBuf buf,
                                                     PacketSender responseSender) {
        if (!PcaSettings.pcaSyncProtocol) {
            return;
        }
        ModInfo.LOGGER.debug("{} cancel watch blockEntity.", player.getName().asString());
        PcaSyncProtocol.clearPlayerWatchBlock(player);
    }

    // 客户端通知服务端取消 Entity 同步
    private static void cancelSyncEntityHandler(MinecraftServer server, ServerPlayerEntity player,
                                                ServerPlayNetworkHandler handler, PacketByteBuf buf,
                                                PacketSender responseSender) {
        if (!PcaSettings.pcaSyncProtocol) {
            return;
        }
        ModInfo.LOGGER.debug("{} cancel watch entity.", player.getName().asString());
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
        ModInfo.LOGGER.debug("{} watch blockpos {}: {}", player.getName().asString(), pos, blockState);

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

        Pair<Identifier, BlockPos> pair = new ImmutablePair<>(DimensionType.getId(player.getEntityWorld().getDimension().getType()), pos);
        lock.lock();
        playerSet.add(player);
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
        if (!PcaSettings.pcaSyncProtocol) {
            return;
        }
        int entityId = buf.readInt();
        ServerWorld world = player.getServerWorld();
        Entity entity = world.getEntityById(entityId);
        if (entity == null) {
            ModInfo.LOGGER.debug("Can't find entity {}.", entityId);
        } else {
            clearPlayerWatchData(player);
            if (entity instanceof PlayerEntity) {
                if (PcaSettings.pcaSyncPlayerEntity == PcaSettings.PCA_SYNC_PLAYER_ENTITY_OPTIONS.NOBODY) {
                    return;
                } else if (PcaSettings.pcaSyncPlayerEntity == PcaSettings.PCA_SYNC_PLAYER_ENTITY_OPTIONS.BOT) {
                    if (!(entity instanceof EntityPlayerMPFake)) {
                        return;
                    }
                } else if (PcaSettings.pcaSyncPlayerEntity == PcaSettings.PCA_SYNC_PLAYER_ENTITY_OPTIONS.OPS) {
                    if (!(entity instanceof EntityPlayerMPFake) && server.getPermissionLevel(player.getGameProfile()) < 2) {
                        return;
                    }
                } else if (PcaSettings.pcaSyncPlayerEntity == PcaSettings.PCA_SYNC_PLAYER_ENTITY_OPTIONS.OPS_AND_SELF) {
                    if (!(entity instanceof EntityPlayerMPFake) &&
                            server.getPermissionLevel(player.getGameProfile()) < 2 &&
                            entity != player) {
                        return;
                    }
                } else if (PcaSettings.pcaSyncPlayerEntity == PcaSettings.PCA_SYNC_PLAYER_ENTITY_OPTIONS.EVERYONE) {

                } else {
                    // wtf????
                    ModInfo.LOGGER.warn("syncEntityHandler wtf???");
                    return;
                }
            }
            ModInfo.LOGGER.debug("{} watch entity {}: {}", player.getName().asString(), entityId, entity);
            updateEntity(player, entity);

            Pair<Identifier, Entity> pair = new ImmutablePair<>(DimensionType.getId(entity.getEntityWorld().getDimension().getType()), entity);
            lock.lock();
            playerSet.add(player);
            playerWatchEntity.put(player, pair);
            if (!entityWatchPlayerSet.containsKey(pair)) {
                entityWatchPlayerSet.put(pair, new HashSet<>());
            }
            entityWatchPlayerSet.get(pair).add(player);
            lock.unlock();
        }
    }

    private static MutablePair<Identifier, Entity> getIdentifierEntityPair(Identifier identifier, Entity entity) {
        pairLock.lock();
        identifierEntityPair.setLeft(identifier);
        identifierEntityPair.setRight(entity);
        pairLock.unlock();
        return identifierEntityPair;
    }

    private static MutablePair<Identifier, BlockPos> getIdentifierBlockPosPair(Identifier identifier, BlockPos pos) {
        pairLock.lock();
        identifierBlockPosPair.setLeft(identifier);
        identifierBlockPosPair.setRight(pos);
        pairLock.unlock();
        return identifierBlockPosPair;
    }

    // 工具
    private static @Nullable
    Set<ServerPlayerEntity> getWatchPlayerList(@NotNull Entity entity) {
        return entityWatchPlayerSet.get(getIdentifierEntityPair(DimensionType.getId(entity.getEntityWorld().getDimension().getType()), entity));
    }

    private static @Nullable
    Set<ServerPlayerEntity> getWatchPlayerList(@NotNull World world, @NotNull BlockPos blockPos) {
        return blockPosWatchPlayerSet.get(getIdentifierBlockPosPair(DimensionType.getId(world.getDimension().getType()), blockPos));
    }

    public static boolean syncEntityToClient(@NotNull Entity entity) {
        if (entity.getEntityWorld().isClient()) {
            return false;
        }
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
            if (world.isClient()) {
                return false;
            }
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
        lock.lock();
        playerWatchBlockPos.clear();
        playerWatchEntity.clear();
        blockPosWatchPlayerSet.clear();
        entityWatchPlayerSet.clear();
        for (ServerPlayerEntity player : playerSet) {
            disablePcaSyncProtocol(player);
        }
        playerSet.clear();
        lock.unlock();
    }

    // 启用 PcaSyncProtocol
    public static void enablePcaSyncProtocolGlobal() {
        if (PcaMod.server == null) {
            return;
        }
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
