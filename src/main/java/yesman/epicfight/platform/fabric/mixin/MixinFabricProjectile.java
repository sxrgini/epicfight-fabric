package yesman.epicfight.platform.fabric.mixin;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.event.impl.VanillaEntityEventHooks;

/// ProjectileImpactEvent
@Mixin(Projectile.class)
public abstract class MixinFabricProjectile {
    @Inject(method = "hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;", at = @At("HEAD"), cancellable = true)
    private void epicfight$impact(HitResult hitResult, CallbackInfoReturnable<ProjectileDeflection> cir) {
        if (VanillaEntityEventHooks.onProjectileImpacts(hitResult, (Projectile)(Object)this)) {
            cir.setReturnValue(ProjectileDeflection.NONE);
        }
    }
}
