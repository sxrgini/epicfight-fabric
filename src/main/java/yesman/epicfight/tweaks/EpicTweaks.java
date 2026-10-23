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

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.gamerule.EpicFightGameRules;

/// Logic of the Epic Tweaks addon, called directly from Epic Fight (no mixins needed when embedded).
public final class EpicTweaks {
    private EpicTweaks() {
    }

    private static boolean isCombatItem(Item item) {
        return ClientConfig.combatCategorizedItems.contains(item);
    }

    private static boolean shouldBeVanilla(Item item) {
        return !isCombatItem(item) || ClientConfig.miningCategorizedItems.contains(item);
    }

    /// Replacement of PlayerPatch#toggleMode. Returns true if the toggle was handled here.
    public static boolean toggleMode(PlayerPatch<?> playerpatch) {
        if (!EpicTweaksConfig.enforceMode() || !EpicFightGameRules.CAN_SWITCH_PLAYER_MODE.getRuleValue(playerpatch.getOriginal().level())) {
            return false;
        }

        var stack = playerpatch.getOriginal().getMainHandItem();

        switch (playerpatch.getPlayerMode()) {
            case VANILLA -> {
                if (isCombatItem(stack.getItem()) || stack.is(Items.AIR)) {
                    playerpatch.toEpicFightMode(true);
                }
            }
            case EPICFIGHT -> {
                if (shouldBeVanilla(stack.getItem())) {
                    playerpatch.toVanillaMode(true);
                }
            }
        }

        return true;
    }

    /// Replacement of LocalPlayerPatch#updateHeldItem. Returns true if the held item change was handled here.
    public static boolean updateHeldItem(LocalPlayerPatch playerpatch) {
        if (!EpicTweaksConfig.autoSwitchMode() || !EpicFightGameRules.CAN_SWITCH_PLAYER_MODE.getRuleValue(playerpatch.getOriginal().level())) {
            return false;
        }

        var item = playerpatch.getOriginal().getMainHandItem().getItem();

        switch (playerpatch.getPlayerMode()) {
            case VANILLA -> {
                if (isCombatItem(item)) {
                    playerpatch.toEpicFightMode(true);
                }
            }
            case EPICFIGHT -> {
                if (shouldBeVanilla(item)) {
                    playerpatch.toVanillaMode(true);
                }
            }
        }

        return true;
    }

    /// Returns true if Epic Fight's first person rendering should be skipped (vanilla hand is drawn instead).
    public static boolean shouldRenderVanillaHand(LocalPlayerPatch playerpatch) {
        return playerpatch.isEpicFightMode()
            && EpicTweaksConfig.filterAnimationFirstPerson()
            && playerpatch.getCurrentLivingMotion() != LivingMotions.INACTION
            && !isCombatItem(playerpatch.getValidItemInHand(InteractionHand.MAIN_HAND).getItem());
    }
}
