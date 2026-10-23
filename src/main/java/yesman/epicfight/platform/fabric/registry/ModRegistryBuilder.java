package yesman.epicfight.platform.fabric.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import yesman.epicfight.platform.fabric.registry.callback.AddCallback;
import yesman.epicfight.platform.fabric.registry.callback.BakeCallback;
import yesman.epicfight.platform.fabric.registry.callback.ClearCallback;

import java.util.ArrayList;
import java.util.List;

/// A Fabric implementation of NeoForge's `RegistryBuilder`.
public final class ModRegistryBuilder<T> {
    private static final List<Runnable> BAKE_TASKS = new ArrayList<>();

    private final ResourceKey<? extends Registry<T>> key;
    private boolean sync;
    private Object callback;

    public ModRegistryBuilder(ResourceKey<? extends Registry<T>> key) {
        this.key = key;
    }

    public ModRegistryBuilder<T> sync(boolean sync) {
        this.sync = sync;
        return this;
    }

    public ModRegistryBuilder<T> callback(Object callback) {
        this.callback = callback;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Registry<T> create() {
        FabricRegistryBuilder<T, ? extends Registry<T>> builder = FabricRegistryBuilder.createSimple((ResourceKey<Registry<T>>)(ResourceKey<?>)this.key);

        if (this.sync) {
            builder.attribute(RegistryAttribute.SYNCED);
        }

        Registry<T> registry = builder.buildAndRegister();

        if (this.callback instanceof AddCallback<?> addCallback) {
            AddCallback<T> typed = (AddCallback<T>) addCallback;
            RegistryEntryAddedCallback.event(registry).register((rawId, id, object) -> typed.onAdd(registry, rawId, ResourceKey.create(this.key, id), object));
        }

        if (this.callback instanceof BakeCallback<?> bakeCallback) {
            BakeCallback<T> typed = (BakeCallback<T>) bakeCallback;
            Object clear = this.callback;

            BAKE_TASKS.add(() -> {
                if (clear instanceof ClearCallback<?> clearCallback) {
                    ((ClearCallback<T>) clearCallback).onClear(registry, true);
                }

                typed.onBake(registry);
            });
        }

        return registry;
    }

    /// Runs all queued bake callbacks. Must be called once after every deferred register has finished.
    public static void bakeAll() {
        BAKE_TASKS.forEach(Runnable::run);
    }
}
