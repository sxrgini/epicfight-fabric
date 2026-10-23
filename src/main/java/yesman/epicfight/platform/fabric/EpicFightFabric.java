package yesman.epicfight.platform.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.EpicFight;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationManager.AnimationRegistryEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.api.data.reloader.SkillReloadListener;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.impl.VanillaEntityEventHooks;
import yesman.epicfight.api.event.impl.VanillaPlayerEventHooks;
import yesman.epicfight.api.ex_cap.listeners.ConditionalReloadListener;
import yesman.epicfight.api.ex_cap.listeners.ItemPresetReloadListener;
import yesman.epicfight.api.ex_cap.listeners.MovesetReloadListener;
import yesman.epicfight.api.ex_cap.listeners.WeaponModifierReloadListener;
import yesman.epicfight.client.online.cosmetics.Emote;
import yesman.epicfight.config.CommonConfig;
import yesman.epicfight.config.ServerConfig;
import yesman.epicfight.data.loot.EpicFightLootTables;
import yesman.epicfight.data.loot.OnSkillBookDroppedByEntity;
import yesman.epicfight.epicskins.animation.EpicSkinsAnimations;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.main.EpicFightExtensibleEnums;
import yesman.epicfight.main.EpicFightExtensions;
import yesman.epicfight.network.EntityPairingPacketType;
import yesman.epicfight.network.EntityPairingPacketTypes;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.EpicFightNetworkManager.PayloadBundleBuilder;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.platform.ModPlatformProvider;
import yesman.epicfight.platform.fabric.client.EpicFightFabricClient;
import yesman.epicfight.platform.fabric.network.FabricNetworkSender;
import yesman.epicfight.platform.fabric.registry.DeferredRegister;
import yesman.epicfight.platform.fabric.registry.ModRegistryBuilder;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.registry.entries.EpicFightAttributes;
import yesman.epicfight.registry.entries.EpicFightCommandArgumentTypes;
import yesman.epicfight.registry.entries.EpicFightCreativeTabs;
import yesman.epicfight.registry.entries.EpicFightEntityTypes;
import yesman.epicfight.registry.entries.EpicFightItems;
import yesman.epicfight.registry.entries.EpicFightMobEffects;
import yesman.epicfight.registry.entries.EpicFightPotions;
import yesman.epicfight.server.commands.AnimatorCommand;
import yesman.epicfight.server.commands.PlayerModeCommand;
import yesman.epicfight.server.commands.PlayerSkillCommand;
import yesman.epicfight.server.commands.PlayerStaminaCommand;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.ItemKeywordReloadListener;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;
import yesman.epicfight.world.capabilities.skill.PlayerSkills;
import yesman.epicfight.world.gamerule.EpicFightGameRules;
import yesman.epicfight.world.gamerule.EpicFightGameRules.ConfigurableGameRule;
import yesman.epicfight.world.item.SkillBookItem;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.platform.fabric.event.EntityAttributeCreationEvent;
import yesman.epicfight.platform.fabric.event.EntityAttributeModificationEvent;

import java.util.Comparator;
import java.util.Optional;

/// The common (both client and dedicated server) entrypoint of the Fabric port.
public class EpicFightFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ModPlatformProvider.initialize(new FabricModPlatform());

        this.registerConfigs();
        this.registerEnums();

        // Addons (e.g. Battle Arts) register their enums, event hooks, animation listeners and custom registries through
        // the "epicfight-addon" entrypoint. Their deferred registers are queued with AddonRegistrar.
        for (var addon : FabricLoader.getInstance().getEntrypointContainers("epicfight-addon", ModInitializer.class)) {
            addon.getEntrypoint().onInitialize();
        }

        this.registerAnimations();

        // Registries
        java.util.List<DeferredRegister<?>> allRegisters = new java.util.ArrayList<>(EpicFightRegistries.DEFERRED_REGISTRIES);
        allRegisters.addAll(AddonRegistrar.drain());
        DeferredRegister.registerAll(allRegisters);

        ModRegistryBuilder.bakeAll();

        EntityAttributeCreationEvent attributeCreation = new EntityAttributeCreationEvent();
        EpicFightAttributes.EventBus.entityAttributeCreationEvent(attributeCreation);
        AddonRegistrar.fireAttributes(attributeCreation);
        AddonRegistrar.firePost();
        EntityAttributeModificationEvent attributeModification = new EntityAttributeModificationEvent();
        EpicFightAttributes.EventBus.entityAttributeModificationEvent(attributeModification);
        attributeModification.apply();

        DynamicRegistries.registerSynced(EpicFightRegistries.Keys.EMOTE, Emote.CODEC, Emote.CODEC);

        // Common setup
        Armatures.registerEntityTypes();
        EpicFightCommandArgumentTypes.registerArgumentTypes();
        EpicFightPotions.addRecipes();
        EpicFightCapabilities.ITEM_CAPABILITY_PROVIDER.registerWeaponTypesByClass();
        EpicFightCapabilities.ENTITY_PATCH_PROVIDER.registerVanillaEntityPatches();
        EpicFightGameRules.registerGameRules();
        WeaponTypeReloadListener.registerDefaultWeaponTypes();
        EpicFightMobEffects.addOffhandModifier();
        EpicFightExtensibleEnums.initExtensibleEnums();
        EpicFightEntityTypes.registerSpawnPlacements();
        EpicFightEventHooks.Registry.SKILLBOOK_LOOT_TABLE.registerEvent(EpicFightLootTables::createSkillLootTable);

        EpicFightExtensions.register(EpicFight.MODID, new EpicFightExtensions(EpicFightCreativeTabs.ITEMS));

        // Networking
        EpicFightNetworkManager.registerPayloadTypes();
        EpicFightNetworkManager.registerServerReceivers();

        this.registerReloadListeners();
        this.registerBuiltInPack();
        this.registerGameEvents();
    }

    private void registerConfigs() {
        NeoForgeModConfigEvents.loading(EpicFight.MODID).register(config -> {
            CommonConfig.onConfigLoading(config);
            ServerConfig.onConfigLoading(config);
        });

        NeoForgeModConfigEvents.reloading(EpicFight.MODID).register(config -> {
            CommonConfig.onConfigLoading(config);
            ServerConfig.onConfigLoading(config);
        });

        NeoForgeConfigRegistry.INSTANCE.register(EpicFight.MODID, net.neoforged.fml.config.ModConfig.Type.COMMON, CommonConfig.SPEC);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            NeoForgeConfigRegistry.INSTANCE.register(EpicFight.MODID, net.neoforged.fml.config.ModConfig.Type.SERVER, ServerConfig.SPEC);
        }
    }

    private void registerEnums() {
        LivingMotion.ENUM_MANAGER.registerEnumCls(EpicFight.MODID, LivingMotions.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(EpicFight.MODID, SkillCategories.class);
        SkillSlot.ENUM_MANAGER.registerEnumCls(EpicFight.MODID, SkillSlots.class);
        Style.ENUM_MANAGER.registerEnumCls(EpicFight.MODID, Styles.class);
        WeaponCategory.ENUM_MANAGER.registerEnumCls(EpicFight.MODID, WeaponCategories.class);
        Faction.ENUM_MANAGER.registerEnumCls(EpicFight.MODID, Factions.class);
        EntityPairingPacketType.ENUM_MANAGER.registerEnumCls(EpicFight.MODID, EntityPairingPacketTypes.class);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            EpicFightFabricClient.registerClientEnums();
        }

        LivingMotion.ENUM_MANAGER.loadEnum();
        SkillCategory.ENUM_MANAGER.loadEnum();
        SkillSlot.ENUM_MANAGER.loadEnum();
        Style.ENUM_MANAGER.loadEnum();
        WeaponCategory.ENUM_MANAGER.loadEnum();
        Faction.ENUM_MANAGER.loadEnum();
        EntityPairingPacketType.ENUM_MANAGER.loadEnum();

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            EpicFightFabricClient.loadClientEnums();
        }
    }

    private void registerAnimations() {
        AnimationRegistryEvent.register(Animations::registerAnimations);
        AnimationRegistryEvent.register(EpicSkinsAnimations::registerAnimations);

        AnimationManager.addNoWarningModId(EpicFight.EPICSKINS_MODID);
        AnimationRegistryEvent animationRegistryEvent = AnimationRegistryEvent.post();
        animationRegistryEvent.getBuilders().stream().sorted(Comparator.comparing(AnimationManager.AnimationBuilder::namespace)).forEach(builder -> builder.task().accept(builder));
    }

    private void registerReloadListeners() {
        FabricReloadListeners listeners = new FabricReloadListeners(PackType.SERVER_DATA);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            listeners.add("animation_manager", AnimationManager::getInstance);
        }

        listeners
            .add("collider_preset", ColliderPreset::new)
            .add("skills", SkillReloadListener::new)
            .add("item_presets", ItemPresetReloadListener::new)
            .add("weapon_modifiers", WeaponModifierReloadListener::new)
            .add("conditionals", ConditionalReloadListener::new)
            .add("movesets", MovesetReloadListener::new)
            .add("weapon_types", WeaponTypeReloadListener::new)
            .add("item_keywords", ItemKeywordReloadListener::new)
            .add("item_capabilities", ItemCapabilityReloadListener::new)
            .add("mob_patches", MobPatchReloadListener::new);
    }

    private void registerBuiltInPack() {
        FabricLoader.getInstance().getModContainer(EpicFight.MODID).ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(
                EpicFight.identifier("epicfight_legacy"),
                container,
                Component.translatable("pack.epicfight_legacy.title"),
                ResourcePackActivationType.NORMAL
            );
        });
    }

    private void registerGameEvents() {
        // Commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PlayerModeCommand.register(dispatcher);
            PlayerSkillCommand.register(dispatcher);
            PlayerStaminaCommand.register(dispatcher);
            AnimatorCommand.register(dispatcher);
        });

        // Loot
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            EpicFightLootTables.onLootTableRegistry(key.location(), tableBuilder);
        });

        ServerLifecycleEvents.SERVER_STARTING.register(FabricNetworkSender::setServer);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> OnSkillBookDroppedByEntity.registerEntitySkillLootTable());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> OnSkillBookDroppedByEntity.registerEntitySkillLootTable());
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(EpicFightFabric::onDatapackSync);

        // Creative tab
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((tab, entries) -> {
            // Accept learnable skills for each mod by [EpicFightExtensions#skillBookCreativeTab].
            // If the extension doesn't exist, add them to [EpicFightCreativeTabs.ITEMS] tab.
            EpicFightRegistries.SKILL.keySet().stream().map(ResourceLocation::getNamespace).distinct().forEach(modid -> {
                Optional<EpicFightExtensions> extension = EpicFightExtensions.get(modid);
                boolean matches = extension.map(ext -> ext.skillBookCreativeTab().get() == tab).orElseGet(() -> tab == EpicFightCreativeTabs.ITEMS.get());

                if (matches) {
                    EpicFightRegistries.SKILL.holders().filter(skill ->
                        skill.value().getCategory().learnable() &&
                        skill.value().getCreativeTab() == null &&
                        skill.value().getRegistryName().getNamespace().equals(modid)
                    ).forEach(holder -> {
                        ItemStack stack = new ItemStack(EpicFightItems.SKILLBOOK.get());
                        SkillBookItem.setContainingSkill(holder, stack);
                        entries.accept(stack);
                    });
                }
            });

            EpicFightRegistries.SKILL.holders().filter(skill ->
                skill.value().getCategory().learnable() &&
                skill.value().getCreativeTab() == tab
            ).forEach(holder -> {
                ItemStack stack = new ItemStack(EpicFightItems.SKILLBOOK.get());
                SkillBookItem.setContainingSkill(holder, stack);
                entries.accept(stack);
            });
        });

        // Entities
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            VanillaEntityEventHooks.onJoinLevel(entity, level, false);
            VanillaEntityEventHooks.onAddedToLevel(entity);

            // Cancel spawning enderman on the main island where Ender Dragon exists
            if (entity.getType() == EntityType.ENDERMAN && VanillaEntityEventHooks.onEnderManSapwns((EnderMan)entity)) {
                entity.discard();
            }
        });

        ServerEntityEvents.EQUIPMENT_CHANGE.register((entity, slot, from, to) -> {
            VanillaEntityEventHooks.onEquipmentChanged(entity, from, to, slot);
        });

        // Players
        EntityTrackingEvents.START_TRACKING.register((target, player) -> VanillaPlayerEventHooks.onStartTracking(target, player));
        EntityTrackingEvents.STOP_TRACKING.register((target, player) -> VanillaPlayerEventHooks.onStopTracking(target, player));
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> VanillaPlayerEventHooks.onCloned(oldPlayer, newPlayer, !alive));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> VanillaPlayerEventHooks.onChagneDimension(player));

        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (!level.isClientSide() && VanillaPlayerEventHooks.onUseItemInServerSide(player)) {
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }

            return InteractionResultHolder.pass(player.getItemInHand(hand));
        });
    }

    /// Replaces NeoForge's `OnDatapackSyncEvent`.
    private static void onDatapackSync(ServerPlayer player, boolean joined) {
        if (joined) {
            PayloadBundleBuilder payloadBundleBuilder = PayloadBundleBuilder.create();

            EpicFightGameRules.GAME_RULES.values().stream().filter(ConfigurableGameRule::shouldSync).forEach(gamerule -> {
                payloadBundleBuilder.and(gamerule.getSyncPacket(player));
            });

            payloadBundleBuilder.send((first, others) -> EpicFightNetworkManager.sendToPlayer(first, player, others));

            if (!player.getServer().isSingleplayerOwner(player.getGameProfile())) {
                sendLevelData(player);
            } else {
                EpicFightCapabilities.getUnparameterizedEntityPatch(player, ServerPlayerPatch.class).ifPresent(serverplayerpatch -> {
                    PlayerSkills skillCapability = serverplayerpatch.getPlayerSkills();

                    skillCapability.listSkillContainers().forEach(skillContainer -> {
                        if (skillContainer.getSkill() != null) {
                            // Reload skill
                            skillContainer.setSkill(skillContainer.getSkill(), true);
                        }
                    });
                });
            }
        } else {
            sendLevelData(player);
        }
    }

    private static void sendLevelData(ServerPlayer player) {
        EpicFightCapabilities.getUnparameterizedEntityPatch(player, ServerPlayerPatch.class).ifPresent(serverplayerpatch -> {
            PlayerSkills skillCapability = serverplayerpatch.getPlayerSkills();

            skillCapability.listSkillContainers().forEach(skillContainer -> {
                if (skillContainer.getSkill() != null) {
                    // Reload skill
                    skillContainer.setSkill(skillContainer.getSkill(), true);
                }
            });

            SPDatapackSync skillParamsPacket = new SPDatapackSync(SPDatapackSync.PacketType.SKILL_PARAMS);
            SkillReloadListener.getSkillParams().forEach(skillParamsPacket::addTag);
            EpicFightNetworkManager.sendToPlayer(skillParamsPacket, player);
        });

        SPDatapackSync animationPacket = new SPDatapackSync(player.getServer().isResourcePackRequired() ? SPDatapackSync.PacketType.MANDATORY_RESOURCE_PACK_ANIMATION : SPDatapackSync.PacketType.RESOURCE_PACK_ANIMATION);
        SPDatapackSync armorPacket = new SPDatapackSync(SPDatapackSync.PacketType.ARMOR);
        SPDatapackSync weaponPacket = new SPDatapackSync(SPDatapackSync.PacketType.WEAPON);
        SPDatapackSync mobCapabilityPacket = new SPDatapackSync(SPDatapackSync.PacketType.MOB);
        SPDatapackSync itemPresetPacket = new SPDatapackSync(SPDatapackSync.PacketType.ITEM_PRESET);
        SPDatapackSync conditionalPacket = new SPDatapackSync(SPDatapackSync.PacketType.PROVIDER_CONDITIONAL);
        SPDatapackSync movesetPacket = new SPDatapackSync(SPDatapackSync.PacketType.MOVESET);
        SPDatapackSync weaponModifierPacket = new SPDatapackSync(SPDatapackSync.PacketType.MODIFIER);
        SPDatapackSync weaponTypePacket = new SPDatapackSync(SPDatapackSync.PacketType.WEAPON_TYPE);
        SPDatapackSync itemKeywordPacket = new SPDatapackSync(SPDatapackSync.PacketType.ITEM_KEYWORD);

        AnimationManager.getInstance().getResourcepackAnimationStream().forEach(animationPacket::addTag);
        ItemCapabilityReloadListener.getArmorDataStream().forEach(armorPacket::addTag);
        ItemCapabilityReloadListener.getWeaponDataStream().forEach(weaponPacket::addTag);
        MobPatchReloadListener.getDataStream().forEach(mobCapabilityPacket::addTag);
        WeaponTypeReloadListener.getWeaponTypeDataStream().forEach(weaponTypePacket::addTag);
        ItemKeywordReloadListener.getCompounds().forEach(itemKeywordPacket::addTag);

        EpicFightNetworkManager.PayloadBundleBuilder
            .beginWith(animationPacket)
            .and(itemPresetPacket)
            .and(weaponModifierPacket)
            .and(conditionalPacket)
            .and(movesetPacket)
            .and(weaponTypePacket)
            .and(armorPacket)
            .and(weaponPacket)
            .and(mobCapabilityPacket)
            .and(itemKeywordPacket)
            .send((first, others) -> EpicFightNetworkManager.sendToPlayer(first, player, others));
    }
}
