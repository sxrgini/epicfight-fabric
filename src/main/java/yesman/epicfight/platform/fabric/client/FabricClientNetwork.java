package yesman.epicfight.platform.fabric.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/// Client-only network helpers, kept apart so dedicated servers never touch client networking classes.
public final class FabricClientNetwork {
    private FabricClientNetwork() {}

    public static void send(CustomPacketPayload payload) {
        if (ClientPlayNetworking.canSend(payload.type())) {
            ClientPlayNetworking.send(payload);
        }
    }
}
