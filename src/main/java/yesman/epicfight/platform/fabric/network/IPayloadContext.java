package yesman.epicfight.platform.fabric.network;

import net.minecraft.world.entity.player.Player;

/// A minimal stand-in for NeoForge's `IPayloadContext`, as only the receiving player is ever needed.
@FunctionalInterface
public interface IPayloadContext {
    Player player();
}
