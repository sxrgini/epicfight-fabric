package yesman.epicfight.platform.fabric.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/// A lazily bound [Holder] that mirrors NeoForge's `DeferredHolder`.
///
/// The value is looked up from the (Fabric-registered) registry the first time it is requested after
/// [DeferredRegister#registerAll()] has populated it.
public class DeferredHolder<R, T extends R> implements Holder<R>, Supplier<T> {
    protected final ResourceKey<R> key;
    private @Nullable Holder<R> holder;

    protected DeferredHolder(ResourceKey<R> key) {
        this.key = key;
    }

    public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<R> key) {
        return new DeferredHolder<>(key);
    }

    public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation valueName) {
        return new DeferredHolder<>(ResourceKey.create(registryKey, valueName));
    }

    public ResourceKey<R> getKey() {
        return this.key;
    }

    public ResourceLocation getId() {
        return this.key.location();
    }

    @ApiStatus.Internal
    void bind(Holder<R> holder) {
        this.holder = holder;
    }

    @SuppressWarnings("unchecked")
    private void tryBind() {
        if (this.holder != null) {
            return;
        }

        Registry<R> registry = (Registry<R>) BuiltInRegistries.REGISTRY.get(this.key.registry());

        if (registry != null) {
            registry.getHolder(this.key).ifPresent(found -> this.holder = found);
        }
    }

    public boolean isBoundNow() {
        this.tryBind();
        return this.holder != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        this.tryBind();

        if (this.holder == null) {
            throw new NullPointerException("Trying to access unbound value: " + this.key);
        }

        return (T) this.holder.value();
    }

    @Override
    public T value() {
        return this.get();
    }

    @Override
    public boolean isBound() {
        this.tryBind();
        return this.holder != null && this.holder.isBound();
    }

    @Override
    public boolean is(ResourceLocation id) {
        return id.equals(this.key.location());
    }

    @Override
    public boolean is(ResourceKey<R> resourceKey) {
        return resourceKey == this.key || resourceKey.equals(this.key);
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> predicate) {
        return predicate.test(this.key);
    }

    @Override
    public boolean is(TagKey<R> tag) {
        this.tryBind();
        return this.holder != null && this.holder.is(tag);
    }

    @Override
    public boolean is(Holder<R> other) {
        this.tryBind();
        return this.holder != null && this.holder.is(other);
    }

    @Override
    public Stream<TagKey<R>> tags() {
        this.tryBind();
        return this.holder != null ? this.holder.tags() : Stream.empty();
    }

    @Override
    public Either<ResourceKey<R>, R> unwrap() {
        return Either.left(this.key);
    }

    @Override
    public Optional<ResourceKey<R>> unwrapKey() {
        return Optional.of(this.key);
    }

    @Override
    public Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<R> owner) {
        this.tryBind();
        return this.holder != null && this.holder.canSerializeIn(owner);
    }

    @Override
    public String getRegisteredName() {
        return this.key.location().toString();
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Holder<?> other && other.kind() == Kind.REFERENCE && other.unwrapKey().map(otherKey -> otherKey.equals(this.key)).orElse(false);
    }

    @Override
    public String toString() {
        return "DeferredHolder{" + this.key + "}";
    }
}
