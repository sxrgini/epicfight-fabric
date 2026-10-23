package yesman.epicfight.platform.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/// Replacements for the commonly used static helpers of NeoForge's `EventHooks`.
public final class FabricHooks {
    private FabricHooks() {}

    public static boolean canEntityGrief(Level level, Entity entity) {
        if (entity instanceof Player) {
            return true;
        }

        return level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }

    public static String modVersion(String modid) {
        return net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(modid).map(container -> container.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
    }

    public static boolean onEntityDestroyBlock(LivingEntity entity, BlockPos pos, BlockState state) {
        return true;
    }

    public static void onUseItemStop(LivingEntity entity, ItemStack stack, int duration) {
    }
}
