package yesman.epicfight.platform.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.data.loot.OnSkillBookDroppedByEntity;

import java.util.Optional;
import java.util.function.Consumer;

/// Replaces NeoForge's global loot modifier that adds skill books to mob drops.
@Mixin(LootTable.class)
public abstract class MixinFabricLootTable {
    @Shadow
    @org.spongepowered.asm.mixin.Final
    private Optional<ResourceLocation> randomSequence;

    @WrapMethod(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V")
    private void epicfight$modifyLoot(LootContext context, Consumer<ItemStack> output, Operation<Void> original) {
        ObjectArrayList<ItemStack> generated = new ObjectArrayList<>();
        original.call(context, (Consumer<ItemStack>)generated::add);
        OnSkillBookDroppedByEntity.modify(generated, context);
        generated.forEach(output);
    }

    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private void epicfight$getRandomItemsSeeded(LootParams params, long seed, Consumer<ItemStack> output, CallbackInfo ci) {
        ((LootTable)(Object)this).getRandomItems(new LootContext.Builder(params).withOptionalRandomSeed(seed).create(this.randomSequence), output);
        ci.cancel();
    }

    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private void epicfight$getRandomItems(LootParams params, Consumer<ItemStack> output, CallbackInfo ci) {
        ((LootTable)(Object)this).getRandomItems(new LootContext.Builder(params).create(this.randomSequence), output);
        ci.cancel();
    }
}
