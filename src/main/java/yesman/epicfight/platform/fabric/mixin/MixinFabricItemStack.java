package yesman.epicfight.platform.fabric.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.event.impl.VanillaItemEventHooks;

import java.util.function.BiConsumer;

/// ItemAttributeModifierEvent
@Mixin(ItemStack.class)
public abstract class MixinFabricItemStack {
    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V", at = @At("TAIL"))
    private void epicfight$forEachModifierGroup(EquipmentSlotGroup slotGroup, BiConsumer<Holder<Attribute>, AttributeModifier> action, CallbackInfo ci) {
        VanillaItemEventHooks.onModifyItemAttribute((ItemStack)(Object)this, (attribute, modifier, group) -> {
            if (group == slotGroup) {
                action.accept(attribute, modifier);
            }
        });
    }

    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", at = @At("TAIL"))
    private void epicfight$forEachModifierSlot(EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> action, CallbackInfo ci) {
        VanillaItemEventHooks.onModifyItemAttribute((ItemStack)(Object)this, (attribute, modifier, group) -> {
            if (group.test(slot)) {
                action.accept(attribute, modifier);
            }
        });
    }
}
