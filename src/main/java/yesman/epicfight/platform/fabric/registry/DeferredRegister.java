package yesman.epicfight.platform.fabric.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/// A Fabric implementation of NeoForge's `DeferredRegister`.
///
/// Entries are queued with [#register(String, Supplier)] and written to the actual registry once
/// [#registerAll(Collection)] runs during mod initialization.
public class DeferredRegister<T> {
    public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        return new DeferredRegister<>(registryKey, namespace);
    }

    public static <T> DeferredRegister<T> create(Registry<T> registry, String namespace) {
        return new DeferredRegister<>(registry.key(), namespace);
    }

    private final ResourceKey<? extends Registry<T>> registryKey;
    private final String namespace;
    private final Map<DeferredHolder<T, ? extends T>, Function<ResourceLocation, ? extends T>> entries = new LinkedHashMap<>();
    private boolean registered = false;

    protected DeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        this.registryKey = registryKey;
        this.namespace = namespace;
    }

    public <I extends T> @NotNull DeferredHolder<T, I> register(@NotNull String name, @NotNull Supplier<? extends I> sup) {
        return this.register(name, key -> sup.get());
    }

    public <I extends T> @NotNull DeferredHolder<T, I> register(@NotNull String name, @NotNull Function<ResourceLocation, ? extends I> func) {
        if (this.registered) {
            throw new IllegalStateException("Cannot register new entries to DeferredRegister after the registration finished: " + this.namespace + ":" + name);
        }

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
        DeferredHolder<T, I> holder = DeferredHolder.create(ResourceKey.create(this.registryKey, id));

        if (this.entries.keySet().stream().anyMatch(existing -> existing.getId().equals(id))) {
            throw new IllegalArgumentException("Duplicate registration " + id + " in " + this.registryKey.location());
        }

        this.entries.put(holder, func);
        return holder;
    }

    public Collection<DeferredHolder<T, ? extends T>> getEntries() {
        return Collections.unmodifiableCollection(this.entries.keySet());
    }

    public ResourceKey<? extends Registry<T>> getRegistryKey() {
        return this.registryKey;
    }

    public String getNamespace() {
        return this.namespace;
    }

    @SuppressWarnings("unchecked")
    private void registerEntries() {
        if (this.registered) {
            return;
        }

        this.registered = true;
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(this.registryKey.location());

        if (registry == null) {
            throw new IllegalStateException("Registry " + this.registryKey.location() + " is not available for registration");
        }

        this.entries.forEach((holder, factory) -> {
            T value = factory.apply(holder.getId());
            Registry.register(registry, holder.getKey(), value);
            holder.bind(registry.getHolderOrThrow(holder.getKey()));
        });
    }

    /// Writes all queued entries into the registries, ordered the same way NeoForge fires its registry events:
    /// vanilla registries first (in their bootstrap order), then registries added by mods in creation order.
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void registerAll(Collection<? extends DeferredRegister<?>> registers) {
        List<DeferredRegister<?>> sorted = new ArrayList<>(registers);

        sorted.sort(Comparator.comparingInt(register -> {
            Object registry = BuiltInRegistries.REGISTRY.get(register.registryKey.location());
            return registry == null ? Integer.MAX_VALUE : ((net.minecraft.core.Registry)BuiltInRegistries.REGISTRY).getId(registry);
        }));

        sorted.forEach(DeferredRegister::registerEntries);
    }

    @Nullable
    public Registry<T> getRegistry() {
        @SuppressWarnings("unchecked")
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(this.registryKey.location());
        return registry;
    }
}
