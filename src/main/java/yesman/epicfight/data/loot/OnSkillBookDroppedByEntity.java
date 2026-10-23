package yesman.epicfight.data.loot;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.registry.RegisterMobSkillBookLootTableEvent;

import java.util.HashMap;
import java.util.Map;

/// Replaces the NeoForge global loot modifier: invoked by a mixin on [LootTable] for every generated loot.
public class OnSkillBookDroppedByEntity {
	private static final Map<EntityType<?>, LootTable> SKILL_LOOT_TABLE = Maps.newHashMap();

	public static void registerEntitySkillLootTable() {
		Map<EntityType<?>, LootTable.Builder> builders = new HashMap<> ();
        RegisterMobSkillBookLootTableEvent skillBookLootTableRegistryEvent = new RegisterMobSkillBookLootTableEvent(builders);
        EpicFightEventHooks.Registry.SKILLBOOK_LOOT_TABLE.post(skillBookLootTableRegistryEvent);
		SKILL_LOOT_TABLE.clear();

		builders.forEach((k, v) -> {
			SKILL_LOOT_TABLE.put(k, v.build());
		});
	}

	@SuppressWarnings("deprecation")
	public static ObjectArrayList<ItemStack> modify(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		// Equivalent to the condition "minecraft:killed_by_player"
		if (!context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) {
			return generatedLoot;
		}
		
		Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);

		if (entity != null && SKILL_LOOT_TABLE.containsKey(entity.getType())) {
			// Call getRandomItemsRaw to avoid recursive call of the modifier
			SKILL_LOOT_TABLE.get(entity.getType()).getRandomItemsRaw(context, generatedLoot::add);
		}

		return generatedLoot;
	}
}
