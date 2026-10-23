package yesman.epicfight.platform.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.event.impl.VanillaEntityEventHooks;
import yesman.epicfight.platform.fabric.DropCapture;

@Mixin(Entity.class)
public abstract class MixinFabricEntity {
    @Shadow
    private Entity vehicle;

    /// EntityEvent.EntityConstructing
    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
    private void epicfight$constructing(EntityType<?> type, Level level, CallbackInfo ci) {
        VanillaEntityEventHooks.onConstruct((Entity)(Object)this);
    }

    /// EntityEvent.Size
    @Inject(method = "getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;", at = @At("RETURN"), cancellable = true)
    private void epicfight$sizing(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        VanillaEntityEventHooks.onSizingEntity((Entity)(Object)this, cir::setReturnValue);
    }

    /// EntityMountEvent (mounting)
    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("RETURN"))
    private void epicfight$startRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            VanillaEntityEventHooks.onEntityMount((Entity)(Object)this, vehicle, true);
        }
    }

    /// EntityMountEvent (dismounting)
    @Inject(method = "removeVehicle()V", at = @At("HEAD"))
    private void epicfight$removeVehicle(CallbackInfo ci) {
        if (this.vehicle != null) {
            VanillaEntityEventHooks.onEntityMount((Entity)(Object)this, this.vehicle, false);
        }
    }

    /// Captures item entities while an entity is dropping its death loot (see [DropCapture])
    @WrapOperation(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean epicfight$captureDrops(Level level, Entity spawned, Operation<Boolean> original) {
        if (spawned instanceof ItemEntity itemEntity && DropCapture.capture((Entity)(Object)this, itemEntity)) {
            return true;
        }

        return original.call(level, spawned);
    }
}
