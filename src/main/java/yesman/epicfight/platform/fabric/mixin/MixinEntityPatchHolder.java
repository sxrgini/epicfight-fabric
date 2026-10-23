package yesman.epicfight.platform.fabric.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import yesman.epicfight.platform.fabric.EntityPatchHolder;
import yesman.epicfight.world.capabilities.provider.AttachmentEntityPatchProvider;

@Mixin(Entity.class)
public abstract class MixinEntityPatchHolder implements EntityPatchHolder {
    @Unique
    private AttachmentEntityPatchProvider epicfight$patchProvider;

    @Override
    public AttachmentEntityPatchProvider epicfight$getPatchProvider() {
        if (this.epicfight$patchProvider == null) {
            this.epicfight$patchProvider = new AttachmentEntityPatchProvider((Entity)(Object)this);
        }

        return this.epicfight$patchProvider;
    }
}
