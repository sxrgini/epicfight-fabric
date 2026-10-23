package yesman.epicfight.platform.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.event.impl.VanillaEntityEventHooks;
import yesman.epicfight.api.event.impl.VanillaPlayerEventHooks;
import yesman.epicfight.platform.fabric.DropCapture;
import yesman.epicfight.platform.fabric.event.FabricGameEvents;
import yesman.epicfight.platform.fabric.event.LivingJumpEvent;

import java.util.List;

/// Triggers the Epic Fight hooks that were driven by NeoForge's living entity events.
@Mixin(LivingEntity.class)
public abstract class MixinFabricLivingEntity {
    @Shadow
    protected boolean dead;

    @Unique
    private float epicfight$damageAfterReduction = Float.NaN;

    @Inject(method = "jumpFromGround()V", at = @At("TAIL"))
    private void epicfight$jump(CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        FabricGameEvents.BUS.post(new LivingJumpEvent(self));
        VanillaEntityEventHooks.onJump(self);
    }

    /// LivingIncomingDamageEvent
    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void epicfight$incomingDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (self.level().isClientSide() || self.isDeadOrDying() || self.isInvulnerableTo(source)) {
            return;
        }

        if (VanillaEntityEventHooks.onDamageIncomes(self, source, amount)) {
            cir.setReturnValue(false);
        }
    }

    /// LivingShieldBlockEvent
    @ModifyExpressionValue(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
    private boolean epicfight$shieldBlock(boolean blocked) {
        if (blocked) {
            VanillaEntityEventHooks.onBlockAttacksWithShield((LivingEntity)(Object)this);
        }

        return blocked;
    }

    /// LivingDamageEvent.Pre
    @ModifyExpressionValue(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float epicfight$damagePre(float damage, DamageSource source) {
        float[] holder = new float[] { damage };
        VanillaEntityEventHooks.onCalculateDamagePre((LivingEntity)(Object)this, source, damage, newDamage -> holder[0] = newDamage);
        this.epicfight$damageAfterReduction = holder[0];

        return holder[0];
    }

    /// LivingDamageEvent.Post
    @Inject(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At("TAIL"))
    private void epicfight$damagePost(DamageSource source, float damage, CallbackInfo ci) {
        if (!Float.isNaN(this.epicfight$damageAfterReduction)) {
            float finalDamage = this.epicfight$damageAfterReduction;
            this.epicfight$damageAfterReduction = Float.NaN;

            VanillaEntityEventHooks.onCalculateDamagePost((LivingEntity)(Object)this, source, finalDamage);
        }
    }

    /// LivingKnockBackEvent
    @Inject(method = "knockback(DDD)V", at = @At("HEAD"), cancellable = true)
    private void epicfight$knockback(double strength, double x, double z, CallbackInfo ci) {
        if (VanillaEntityEventHooks.onKnockedBack((LivingEntity)(Object)this)) {
            ci.cancel();
        }
    }

    /// LivingDeathEvent
    @Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD"))
    private void epicfight$die(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (!self.isRemoved() && !this.dead) {
            VanillaEntityEventHooks.onLivingDeath(self, source);
        }
    }

    /// LivingDropsEvent
    @WrapMethod(method = "dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V")
    private void epicfight$dropAllDeathLoot(ServerLevel level, DamageSource source, Operation<Void> original) {
        LivingEntity self = (LivingEntity)(Object)this;
        List<ItemEntity> drops = DropCapture.begin(self);

        try {
            original.call(level, source);
        } finally {
            DropCapture.end(self);
        }

        if (!VanillaEntityEventHooks.onDropItems(self, source, drops)) {
            drops.forEach(level::addFreshEntity);
        }
    }

    /// MobEffectEvent.Added
    @Inject(method = "onEffectAdded(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
    private void epicfight$effectAdded(MobEffectInstance instance, @Nullable Entity source, CallbackInfo ci) {
        VanillaEntityEventHooks.onMobEffectAdded(instance, (LivingEntity)(Object)this);
    }

    /// MobEffectEvent.Remove / Expired
    @Inject(method = "onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("TAIL"))
    private void epicfight$effectRemoved(MobEffectInstance instance, CallbackInfo ci) {
        VanillaEntityEventHooks.onMobEffectRemoved(instance, (LivingEntity)(Object)this);
    }

    /// LivingEntityUseItemEvent.Start
    @Inject(method = "startUsingItem(Lnet/minecraft/world/InteractionHand;)V", at = @At("HEAD"), cancellable = true)
    private void epicfight$startUsingItem(InteractionHand hand, CallbackInfo ci) {
        if (VanillaPlayerEventHooks.onStartUsingItem((LivingEntity)(Object)this, hand)) {
            ci.cancel();
        }
    }
}
