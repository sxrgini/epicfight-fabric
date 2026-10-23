package yesman.epicfight.platform.fabric.event;

import net.minecraft.world.entity.LivingEntity;

public class LivingJumpEvent extends FabricEvent {
    private final LivingEntity entity;

    public LivingJumpEvent(LivingEntity entity) { this.entity = entity; }

    public LivingEntity getEntity() { return this.entity; }
}
