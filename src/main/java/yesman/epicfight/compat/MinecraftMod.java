package yesman.epicfight.compat;

import org.jetbrains.annotations.NotNull;

/// Mod ids that Epic Fight has special handling for. Most compatibility modules of the
/// NeoForge edition have not been ported to Fabric yet.
public enum MinecraftMod {
    SIMPLY_TOOLTIPS("simplytooltips", true);

    private final @NotNull String modId;
    private final boolean isClientOnly;

    MinecraftMod(@NotNull String modId, boolean isClientOnly) {
        this.modId = modId;
        this.isClientOnly = isClientOnly;
    }

    public @NotNull String getModId() {
        return modId;
    }

    public boolean isClientOnly() {
        return isClientOnly;
    }
}
