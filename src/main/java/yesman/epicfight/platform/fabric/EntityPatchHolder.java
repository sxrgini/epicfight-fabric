package yesman.epicfight.platform.fabric;

import yesman.epicfight.world.capabilities.provider.AttachmentEntityPatchProvider;

/// Duck interface implemented by [net.minecraft.world.entity.Entity] through a mixin. It replaces NeoForge's data attachment
/// that stored the entity patch of an entity.
public interface EntityPatchHolder {
    AttachmentEntityPatchProvider epicfight$getPatchProvider();
}
