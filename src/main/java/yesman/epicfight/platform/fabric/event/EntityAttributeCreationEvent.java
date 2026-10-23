package yesman.epicfight.platform.fabric.event;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

/// Registers default attributes of newly created living entity types.
public final class EntityAttributeCreationEvent {
    public void put(EntityType<? extends LivingEntity> entityType, AttributeSupplier attributes) {
        AttributeSupplier.Builder builder = AttributeSupplier.builder();
        attributes.instances.forEach((attribute, instance) -> builder.add(attribute, instance.getBaseValue()));
        FabricDefaultAttributeRegistry.register(entityType, builder);
    }
}
