package yesman.epicfight.platform.fabric.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import yesman.epicfight.platform.fabric.client.FabricClientNetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/// Fabric implementation of NeoForge's `PacketDistributor`.
public final class FabricNetworkSender {
    private FabricNetworkSender() {}

    private static MinecraftServer server;

    public static void setServer(MinecraftServer server) {
        FabricNetworkSender.server = server;
    }

    public static void sendToServer(CustomPacketPayload message, CustomPacketPayload... others) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            FabricClientNetwork.send(message);

            for (CustomPacketPayload other : others) {
                FabricClientNetwork.send(other);
            }
        }
    }

    public static void sendToAll(CustomPacketPayload message, CustomPacketPayload... others) {
        if (server != null) {
            send(server.getPlayerList().getPlayers(), message, others);
        }
    }

    public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player, CustomPacketPayload... others) {
        send(List.of(player), message, others);
    }

    public static void sendToTracking(Entity entity, CustomPacketPayload message, CustomPacketPayload... others) {
        send(PlayerLookup.tracking(entity), message, others);
    }

    public static void sendToTrackingAndSelf(ServerPlayer entity, CustomPacketPayload message, CustomPacketPayload... others) {
        Set<ServerPlayer> players = new LinkedHashSet<>(PlayerLookup.tracking(entity));
        players.add(entity);
        send(players, message, others);
    }

    public static void sendToTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload message, CustomPacketPayload... others) {
        send(PlayerLookup.tracking(level, chunkPos), message, others);
    }

    private static void send(Collection<ServerPlayer> players, CustomPacketPayload message, CustomPacketPayload[] others) {
        if (players.isEmpty()) {
            return;
        }

        Packet<ClientGamePacketListener> packet;

        if (others.length == 0) {
            packet = castPacket(ServerPlayNetworking.createS2CPacket(message));
        } else {
            List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>();
            packets.add(castPacket(ServerPlayNetworking.createS2CPacket(message)));

            for (CustomPacketPayload other : others) {
                packets.add(castPacket(ServerPlayNetworking.createS2CPacket(other)));
            }

            packet = new ClientboundBundlePacket(packets);
        }

        for (ServerPlayer player : players) {
            // The connection is not established yet while the player data is being loaded (PlayerDataStorage#load)
            if (player.connection == null) {
                continue;
            }

            player.connection.send(packet);
        }
    }

    @SuppressWarnings("unchecked")
    private static Packet<ClientGamePacketListener> castPacket(Packet<?> packet) {
        return (Packet<ClientGamePacketListener>) packet;
    }
}
