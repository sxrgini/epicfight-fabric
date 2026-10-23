package yesman.epicfight.platform.fabric.registry.callback;

import net.minecraft.core.Registry;

/// Mirrors NeoForge's `ClearCallback`. Fabric never clears mod registries, so this is only invoked
/// before the bake step to reset stale state.
@FunctionalInterface
public interface ClearCallback<T> {
    void onClear(Registry<T> registry, boolean full);
}
