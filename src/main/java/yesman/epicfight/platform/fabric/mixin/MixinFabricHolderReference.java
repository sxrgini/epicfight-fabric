package yesman.epicfight.platform.fabric.mixin;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/// Gives reference holders key-based equality, like NeoForge does, so that a registry's own holders and
/// [yesman.epicfight.platform.fabric.registry.DeferredHolder] instances can be used interchangeably as map keys.
@Mixin(Holder.Reference.class)
public abstract class MixinFabricHolderReference<T> {
    @Shadow
    @Nullable
    private ResourceKey<T> key;

    @Override
    public int hashCode() {
        return this.key == null ? System.identityHashCode(this) : this.key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (this.key == null || !(obj instanceof Holder<?> other) || other.kind() != Holder.Kind.REFERENCE) {
            return false;
        }

        return other.unwrapKey().map(otherKey -> otherKey.equals(this.key)).orElse(false);
    }
}
