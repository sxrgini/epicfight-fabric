package yesman.epicfight.platform.fabric.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

/// Vanilla only keeps a single key mapping per physical key, but Epic Fight binds several actions (e.g. vanilla attack
/// and Epic Fight attack) to the same default key. NeoForge's key mapping lookup supports that, so emulate it here.
@Mixin(KeyMapping.class)
public abstract class MixinFabricKeyMapping {
    @Shadow
    @Final
    private static Map<String, KeyMapping> ALL;

    @Shadow
    private InputConstants.Key key;

    @Shadow
    private int clickCount;

    @Shadow
    public abstract void setDown(boolean down);

    @Inject(method = "click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V", at = @At("HEAD"), cancellable = true)
    private static void epicfight$clickAll(InputConstants.Key key, CallbackInfo ci) {
        for (KeyMapping keyMapping : ALL.values()) {
            MixinFabricKeyMapping accessor = (MixinFabricKeyMapping)(Object)keyMapping;

            if (accessor.key.equals(key)) {
                accessor.clickCount++;
            }
        }

        ci.cancel();
    }

    @Inject(method = "set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V", at = @At("HEAD"), cancellable = true)
    private static void epicfight$setAll(InputConstants.Key key, boolean held, CallbackInfo ci) {
        for (KeyMapping keyMapping : ALL.values()) {
            MixinFabricKeyMapping accessor = (MixinFabricKeyMapping)(Object)keyMapping;

            if (accessor.key.equals(key)) {
                accessor.setDown(held);
            }
        }

        ci.cancel();
    }
}
