package yesman.epicfight.platform.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.platform.fabric.client.FabricClientHooks;
import yesman.epicfight.platform.fabric.client.event.InputEvent;

/// Fires `InputEvent.InteractionKeyMappingTriggered` for attack, use item and pick block.
@Mixin(Minecraft.class)
public abstract class MixinFabricMinecraft {
    @Shadow @Final
    public Options options;
    @Shadow
    public LocalPlayer player;

    @Unique
    private boolean epicfight$fire(int button, KeyMapping keyMapping, InteractionHand hand) {
        InputEvent.InteractionKeyMappingTriggered event = FabricClientHooks.onClickInput(button, keyMapping, hand);

        if (event.isCanceled()) {
            if (event.shouldSwingHand() && this.player != null) {
                this.player.swing(hand);
            }

            return true;
        }

        return false;
    }

    @Inject(method = "startAttack()Z", at = @At("HEAD"), cancellable = true)
    private void epicfight$startAttack(CallbackInfoReturnable<Boolean> cir) {
        if (this.player != null && this.epicfight$fire(0, this.options.keyAttack, InteractionHand.MAIN_HAND)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "startUseItem()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0), cancellable = true)
    private void epicfight$startUseItem(CallbackInfo ci, @Local InteractionHand hand) {
        if (this.epicfight$fire(1, this.options.keyUse, hand)) {
            ci.cancel();
        }
    }

    @Inject(method = "pickBlock()V", at = @At("HEAD"), cancellable = true)
    private void epicfight$pickBlock(CallbackInfo ci) {
        if (this.player != null && this.epicfight$fire(2, this.options.keyPickItem, InteractionHand.MAIN_HAND)) {
            ci.cancel();
        }
    }
}
