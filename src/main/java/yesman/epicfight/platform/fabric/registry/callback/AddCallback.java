package yesman.epicfight.platform.fabric.registry.callback;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/// Mirrors NeoForge's `AddCallback`. Invoked for each entry that is added to the registry.
@FunctionalInterface
public interface AddCallback<T> {
    void onAdd(Registry<T> registry, int id, ResourceKey<T> key, T value);
}
