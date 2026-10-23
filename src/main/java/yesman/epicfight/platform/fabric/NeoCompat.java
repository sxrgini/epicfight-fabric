package yesman.epicfight.platform.fabric;

import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.LevelReader;

/// Small replacements for convenience methods that NeoForge patches into vanilla classes.
public final class NeoCompat {
	private NeoCompat() {}
	
	public static <T> Holder<T> holderOrThrow(RegistryAccess registryAccess, ResourceKey<T> key) {
		return registryAccess.registryOrThrow(key.registryKey()).getHolderOrThrow(key);
	}
	
	public static <T> Holder<T> holderOrThrow(LevelReader level, ResourceKey<T> key) {
		return holderOrThrow(level.registryAccess(), key);
	}
	
	public static <T> Optional<Holder.Reference<T>> holder(RegistryAccess registryAccess, ResourceKey<T> key) {
		return registryAccess.registry(key.registryKey()).flatMap(registry -> registry.getHolder(key));
	}
	
	public static ItemAttributeModifiers attributeModifiers(ItemStack itemStack) {
		return itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
	}
	
	public static boolean hasAttribute(AttributeSupplier supplier, Holder<Attribute> attribute) {
		return supplier.instances.keySet().stream().anyMatch(holder -> holder.unwrapKey().equals(attribute.unwrapKey()));
	}
	
	public static int enchantmentLevel(ItemStack itemStack, Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
		return net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack);
	}
	
	public static EquipmentSlot equipmentSlot(ItemStack itemStack) {
		return itemStack.getItem() instanceof net.minecraft.world.item.Equipable equipable ? equipable.getEquipmentSlot() : EquipmentSlot.MAINHAND;
	}
}
