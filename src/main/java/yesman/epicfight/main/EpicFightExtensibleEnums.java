package yesman.epicfight.main;

import net.minecraft.world.item.Rarity;

/// Vanilla enums cannot be extended on Fabric without extra mixin libraries, so the custom "unique" rarity
/// falls back to an existing vanilla rarity.
public class EpicFightExtensibleEnums {
	public static void initExtensibleEnums() {
		EpicFightRairity.init();
	}
	
	public static class EpicFightRairity {
		public static Rarity UNIQUE;
		
		private static void init() {
			UNIQUE = Rarity.UNCOMMON;
		}
	}
}
