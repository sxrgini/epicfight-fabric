package yesman.epicfight.platform.fabric.mixin.client;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.gui.Font;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.events.engine.RenderEngine;

/// Replaces NeoForge's `EntityRenderersEvent.AddLayers`: after vanilla renderers are created, Epic Fight builds its patched renderers
/// with an identical renderer context.
@Mixin(EntityRenderDispatcher.class)
public abstract class MixinFabricEntityRenderDispatcher {
    @Shadow @Final
    private ItemRenderer itemRenderer;
    @Shadow @Final
    private BlockRenderDispatcher blockRenderDispatcher;
    @Shadow @Final
    private ItemInHandRenderer itemInHandRenderer;
    @Shadow @Final
    private Font font;
    @Shadow @Final
    private EntityModelSet entityModels;

    @Inject(method = "onResourceManagerReload(Lnet/minecraft/server/packs/resources/ResourceManager;)V", at = @At("TAIL"))
    private void epicfight$addLayers(ResourceManager resourceManager, CallbackInfo ci) {
        EntityRendererProvider.Context context = new EntityRendererProvider.Context((EntityRenderDispatcher)(Object)this, this.itemRenderer, this.blockRenderDispatcher, this.itemInHandRenderer, resourceManager, this.entityModels, this.font);
        RenderEngine.getInstance().epicfight$addLayers(context);
    }
}
