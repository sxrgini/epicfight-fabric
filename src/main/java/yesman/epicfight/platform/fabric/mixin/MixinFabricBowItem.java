package yesman.epicfight.platform.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import yesman.epicfight.api.event.impl.VanillaPlayerEventHooks;

/// ArrowLooseEvent
@Mixin(BowItem.class)
public abstract class MixinFabricBowItem {
    @ModifyExpressionValue(method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BowItem;getPowerForTime(I)F"))
    private float epicfight$arrowLoose(float power, ItemStack stack, Level level, LivingEntity shooter, int timeCharged) {
        if (power >= 0.1F && shooter instanceof Player player) {
            VanillaPlayerEventHooks.onLooseArrow(player);
        }

        return power;
    }
}
