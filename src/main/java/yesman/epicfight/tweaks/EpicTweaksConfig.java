/*
 * This file is part of Epic Tweaks.
 * Copyright (c) 2024, HDainester, All rights reserved.
 *
 * Epic Tweaks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Epic Tweaks is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Epic Tweaks. If not, see <http://www.gnu.org/licenses/lgpl>.
 *
 * Embedded into the Epic Fight Fabric port by iiAmPanda (Fabric port).
 */
package yesman.epicfight.tweaks;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

/// Client config of the embedded Epic Tweaks addon (stored in epictweaks-client.toml).
public final class EpicTweaksConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ConfigValue<Boolean> FILTER_ANIMATION_FIRST_PERSON = BUILDER
        .comment("\nForces vanilla rendering in first person view while not",
            "holding a 'combat preferred' item (for non-combat motions).",
            "\nDefault: true")
        .define("filter_animation_first_person", true);

    private static final ConfigValue<Boolean> ENFORCE_MODE = BUILDER
        .comment("\nIf enabled the player cannot manually switch to vanilla mode while holding a",
            "'combat preferred' item or to combat mode while not holding a 'combat preferred' item.",
            "This option has no effect if the 'canSwitchPlayerMode' gamerule is disabled.",
            "\nDefault: true")
        .define("enforce_mode", true);

    private static final ConfigValue<Boolean> AUTO_SWITCH_MODE = BUILDER
        .comment("\nIf enabled the player will automatically switch to epic fight mode when switching to",
            "a 'combat preferred' item and back to vanilla mode when switching to any other item.",
            "This option has no effect if the 'canSwitchPlayerMode' gamerule is disabled.",
            "\nDefault: true")
        .define("autoswitch_mode", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private EpicTweaksConfig() {
    }

    public static boolean filterAnimationFirstPerson() {
        return FILTER_ANIMATION_FIRST_PERSON.get();
    }

    public static boolean enforceMode() {
        return ENFORCE_MODE.get();
    }

    public static boolean autoSwitchMode() {
        return AUTO_SWITCH_MODE.get();
    }
}
