package yesman.epicfight.platform.fabric.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.platform.fabric.client.event.MovementInputUpdateEvent;
import yesman.epicfight.platform.fabric.event.FabricGameEvents;

/// Fires `MovementInputUpdateEvent` right after the input is ticked.
@Mixin(LocalPlayer.class)
public abstract class MixinFabricLocalPlayer {
    @Inject(
        method = "aiStep()V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(ZF)V", shift = At.Shift.AFTER)
    )
    private void epicfight$movementInput(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer)(Object)this;
        FabricGameEvents.BUS.post(new MovementInputUpdateEvent(self, self.input));
    }
}
