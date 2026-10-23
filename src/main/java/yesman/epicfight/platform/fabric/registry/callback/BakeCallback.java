package yesman.epicfight.platform.fabric.registry.callback;

import net.minecraft.core.Registry;

/// Mirrors NeoForge's `BakeCallback`. On Fabric this is run once all deferred entries are written.
@FunctionalInterface
public interface BakeCallback<T> {
    void onBake(Registry<T> registry);
}
