package yesman.epicfight.platform.fabric.event;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

/// Collects attributes that should be added to existing entity types, then applies them in one go.
/// This matches NeoForge's event with the same name, which patches the default attribute map of entity types.
public final class EntityAttributeModificationEvent {
    private final Map<EntityType<? extends LivingEntity>, Map<Holder<Attribute>, Double>> additions = new LinkedHashMap<>();

    public void add(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute, double defaultValue) {
        this.additions.computeIfAbsent(entityType, type -> new LinkedHashMap<>()).put(attribute, defaultValue);
    }

    public void add(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute) {
        this.add(entityType, attribute, attribute.value().getDefaultValue());
    }

    /// Applies all collected attribute additions by replacing the default attribute supplier of each entity type.
    public void apply() {
        this.additions.forEach((entityType, attributes) -> {
            AttributeSupplier existing = DefaultAttributes.getSupplier(entityType);
            AttributeSupplier.Builder builder = AttributeSupplier.builder();
            existing.instances.forEach((attribute, instance) -> builder.add(attribute, instance.getBaseValue()));
            attributes.forEach(builder::add);
            FabricDefaultAttributeRegistry.register(entityType, builder);
        });

        this.additions.clear();
    }
}
