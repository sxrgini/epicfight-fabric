package yesman.epicfight.platform.fabric.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.platform.fabric.event.FabricEvent;

public abstract class RenderLivingEvent<T extends LivingEntity, M extends net.minecraft.client.model.EntityModel<T>> extends FabricEvent {
    private final LivingEntity entity;
    private final LivingEntityRenderer<?, ?> renderer;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource buffer;
    private final int packedLight;

    protected RenderLivingEvent(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.entity = entity;
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.buffer = buffer;
        this.packedLight = packedLight;
    }

    @SuppressWarnings("unchecked")
    public T getEntity() { return (T)this.entity; }
    @SuppressWarnings("unchecked")
    public LivingEntityRenderer<T, M> getRenderer() { return (LivingEntityRenderer<T, M>)this.renderer; }
    public float getPartialTick() { return this.partialTick; }
    public PoseStack getPoseStack() { return this.poseStack; }
    public MultiBufferSource getMultiBufferSource() { return this.buffer; }
    public int getPackedLight() { return this.packedLight; }

    public static class Pre<T extends LivingEntity, M extends net.minecraft.client.model.EntityModel<T>> extends RenderLivingEvent<T, M> {
        public Pre(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            super(entity, renderer, partialTick, poseStack, buffer, packedLight);
        }
    }
}
