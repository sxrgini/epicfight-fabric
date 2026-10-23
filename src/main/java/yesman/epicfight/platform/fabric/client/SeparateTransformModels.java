package yesman.epicfight.platform.fabric.client;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.main.EpicFightMod;

/// Replacement for NeoForge's `neoforge:separate_transforms` model loader. The resource pack ships a base model
/// plus `<name>_<context>_variant` models for the gui, fixed and ground display contexts, and
/// `MixinFabricItemRenderer` swaps to those variants while rendering.
public final class SeparateTransformModels {
    private SeparateTransformModels() {}

    public static final Set<String> NAMES = Set.of(
        "bokken",
        "diamond_greatsword",
        "diamond_longsword",
        "diamond_spear",
        "diamond_tachi",
        "golden_greatsword",
        "golden_longsword",
        "golden_spear",
        "golden_tachi",
        "iron_greatsword",
        "iron_longsword",
        "iron_spear",
        "iron_tachi",
        "netherite_greatsword",
        "netherite_longsword",
        "netherite_spear",
        "netherite_tachi",
        "stone_greatsword",
        "stone_longsword",
        "stone_spear",
        "stone_tachi",
        "uchigatana",
        "wooden_greatsword",
        "wooden_longsword",
        "wooden_spear",
        "wooden_tachi"
    );

    public static final String[] CONTEXTS = {"gui", "fixed", "ground"};

    public static ResourceLocation variant(String name, String context) {
        return EpicFightMod.identifier("item/" + name + "_" + context + "_variant");
    }
}
