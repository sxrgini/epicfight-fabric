package yesman.epicfight.platform.fabric.client;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionResultHolder;
import yesman.epicfight.EpicFight;
import yesman.epicfight.EpicFightClient;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.client.animation.property.JointMaskReloadListener;
import yesman.epicfight.api.client.event.impl.VanillaGUIEventHooks;
import yesman.epicfight.api.client.event.impl.VanillaGeneralClientEventHooks;
import yesman.epicfight.api.client.input.action.EpicFightInputAction;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.api.client.input.action.MinecraftInputAction;
import yesman.epicfight.api.client.model.ItemSkinsReloadListener;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.IEventBasedEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.gui.screen.SkillBookScreen;
import yesman.epicfight.client.gui.screen.config.ItemsPreferenceScreen;
import yesman.epicfight.client.gui.widgets.AnchoredButton;
import yesman.epicfight.client.gui.widgets.ColorDeterminator;
import yesman.epicfight.client.gui.widgets.common.WidgetTheme;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.particle.*;
import yesman.epicfight.client.renderer.EpicFightShaders;
import yesman.epicfight.client.renderer.blockentity.FractureBlockRenderer;
import yesman.epicfight.client.renderer.entity.DroppedNetherStarRenderer;
import yesman.epicfight.client.renderer.entity.WitherGhostRenderer;
import yesman.epicfight.client.renderer.entity.WitherSkeletonMinionRenderer;
import yesman.epicfight.client.renderer.patched.item.EpicFightItemProperties;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.client.renderer.shader.compute.loader.ComputeShaderProvider;
import yesman.epicfight.client.world.util.FakeLevel;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.platform.fabric.FabricReloadListeners;
import yesman.epicfight.platform.fabric.client.event.ClientTickEvent;
import yesman.epicfight.platform.fabric.client.event.ItemTooltipEvent;
import yesman.epicfight.platform.fabric.client.event.LevelTickEvent;
import yesman.epicfight.platform.fabric.client.event.RenderHighlightEvent;
import yesman.epicfight.platform.fabric.client.event.RenderLevelStageEvent;
import yesman.epicfight.platform.fabric.event.FabricGameEvents;
import yesman.epicfight.registry.entries.EpicFightBlockEntities;
import yesman.epicfight.registry.entries.EpicFightEntityTypes;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.world.capabilities.provider.CommonEntityPatchProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/// The client entrypoint of the Fabric port.
public class EpicFightFabricClient implements ClientModInitializer {
    /// Called by [yesman.epicfight.platform.fabric.EpicFightFabric] on the client before the enums are loaded.
    public static void registerClientEnums() {
        InputAction.ENUM_MANAGER.registerEnumCls(EpicFight.MODID, EpicFightInputAction.class);
        InputAction.ENUM_MANAGER.registerEnumCls("minecraft", MinecraftInputAction.class);
        WidgetTheme.ENUM_MANAGER.registerEnumCls(EpicFight.MODID + ":color_determinator_theme", ColorDeterminator.Theme.class);
        WidgetTheme.ENUM_MANAGER.registerEnumCls(EpicFight.MODID + ":anchored_button_built_in_theme", AnchoredButton.BuiltInTheme.class);
    }

    public static void loadClientEnums() {
        WidgetTheme.ENUM_MANAGER.loadEnum();
        InputAction.ENUM_MANAGER.loadEnum();
    }

    @Override
    public void onInitializeClient() {
        EpicFightClient.initialize(new FabricClientModPlatform());
        // TODO: (MULTI_LOADER) see the note in the NeoForge edition: EpicFightKeyMappings should move to common.
        EpicFightKeyMappings.registerKeys();

        NeoForgeModConfigEvents.loading(EpicFight.MODID).register(ClientConfig::onConfigLoading);
        NeoForgeModConfigEvents.reloading(EpicFight.MODID).register(ClientConfig::onConfigLoading);
        NeoForgeConfigRegistry.INSTANCE.register(EpicFight.MODID, net.neoforged.fml.config.ModConfig.Type.CLIENT, ClientConfig.SPEC);
        // Embedded Epic Tweaks addon config
        NeoForgeConfigRegistry.INSTANCE.register(EpicFight.MODID, net.neoforged.fml.config.ModConfig.Type.CLIENT, yesman.epicfight.tweaks.EpicTweaksConfig.SPEC, "epictweaks-client.toml");

        IEventBasedEngine.init(FabricGameEvents.BUS);

        this.registerParticles();
        this.registerRenderers();
        this.registerHud();
        this.registerModelOverrides();
        this.registerReloadListeners();
        this.registerFabricCallbacks();

        EpicFightShaders.init();
        EpicFightItemProperties.registerItemProperties();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            RenderItemBase.initItemRenderers(client);
            ComputeShaderProvider.checkIfSupports();
            CommonEntityPatchProvider.ClientModule.registerClientPlayerPatches();
            SkillBookScreen.registerIconItems();

            if (ClientConfig.combatCategorizedItems.isEmpty() && ClientConfig.miningCategorizedItems.isEmpty()) {
                ItemsPreferenceScreen.resetItems();
            }

            ClientEngine.getInstance().getAuthHelper().loadPlayerSkin();
        });
    }

    private void registerParticles() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();

        registry.register(EpicFightParticles.ENDERMAN_DEATH_EMIT.get(), EnderParticle.EndermanDeathEmitProvider::new);
        registry.register(EpicFightParticles.HIT_BLUNT.get(), HitBluntParticle.Provider::new);
        registry.register(EpicFightParticles.HIT_BLADE.get(), new HitCutParticle.Provider());
        registry.register(EpicFightParticles.CUT.get(), CutParticle.Provider::new);
        registry.register(EpicFightParticles.NORMAL_DUST.get(), DustParticle.NormalDustProvider::new);
        registry.register(EpicFightParticles.DUST_EXPANSIVE.get(), DustParticle.ExpansiveDustProvider::new);
        registry.register(EpicFightParticles.DUST_CONTRACTIVE.get(), DustParticle.ContractiveDustProvider::new);
        registry.register(EpicFightParticles.EVISCERATE.get(), new EviscerateParticle.Provider());
        registry.register(EpicFightParticles.BLOOD.get(), BloodParticle.Provider::new);
        registry.register(EpicFightParticles.BLADE_RUSH_SKILL.get(), BladeRushParticle.Provider::new);
        registry.register(EpicFightParticles.GROUND_SLAM.get(), new GroundSlamParticle.Provider());
        registry.register(EpicFightParticles.GROUND_FRACTURE.get(), new GroundSlamParticle.BlockParticleProvider());
        registry.register(EpicFightParticles.BREATH_FLAME.get(), EnderParticle.BreathFlameProvider::new);
        registry.register(EpicFightParticles.FORCE_FIELD.get(), new ForceFieldParticle.Provider());
        registry.register(EpicFightParticles.FORCE_FIELD_END.get(), new ForceFieldEndParticle.Provider());
        registry.register(EpicFightParticles.ADRENALINE_PLAYER_BEATING.get(), new EntityAfterimageParticle.AdrenalineParticleProvider());
        registry.register(EpicFightParticles.WHITE_AFTERIMAGE.get(), new EntityAfterimageParticle.WhiteAfterimageProvider());
        registry.register(EpicFightParticles.LASER.get(), new LaserParticle.Provider());
        registry.register(EpicFightParticles.NEUTRALIZE.get(), new DustParticle.ExpansiveMetaParticle.Provider());
        registry.register(EpicFightParticles.BOSS_CASTING.get(), new DustParticle.ContractiveMetaParticle.Provider());
        registry.register(EpicFightParticles.TSUNAMI_SPLASH.get(), TsunamiSplashParticle.Provider::new);
        registry.register(EpicFightParticles.SWING_TRAIL.get(), new AnimationTrailParticle.Provider());
        registry.register(EpicFightParticles.PROJECTILE_TRAIL.get(), new ProjectileTrailParticle.Provider());
        registry.register(EpicFightParticles.FEATHER.get(), FeatherParticle.Provider::new);
        registry.register(EpicFightParticles.AIR_BURST.get(), new AirBurstParticle.Provider());
        registry.register(EpicFightParticles.ASH_DIRECTIONAL.get(), AshDirectionalParticle.Provider::new);
        registry.register(EpicFightParticles.CATHARSIS.get(), CatharsisParticle.Provider::new);
    }

    private void registerRenderers() {
        EntityRendererRegistry.register(EpicFightEntityTypes.AREA_EFFECT_BREATH.get(), NoopRenderer::new);
        EntityRendererRegistry.register(EpicFightEntityTypes.DROPPED_NETHER_STAR.get(), DroppedNetherStarRenderer::new);
        EntityRendererRegistry.register(EpicFightEntityTypes.DEATH_HARVEST_ORB.get(), NoopRenderer::new);
        EntityRendererRegistry.register(EpicFightEntityTypes.DODGE_LOCATION_INDICATOR.get(), NoopRenderer::new);
        EntityRendererRegistry.register(EpicFightEntityTypes.WITHER_GHOST_CLONE.get(), WitherGhostRenderer::new);
        EntityRendererRegistry.register(EpicFightEntityTypes.WITHER_SKELETON_MINION.get(), WitherSkeletonMinionRenderer::new);
        BlockEntityRenderers.register(EpicFightBlockEntities.FRACTURE.get(), FractureBlockRenderer::new);
    }

    private void registerHud() {
        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
            RenderEngine.getInstance().battleModeHUD.renderStaminaBar(guiGraphics, deltaTracker);
            RenderEngine.getInstance().battleModeHUD.renderNormalSkills(guiGraphics, deltaTracker);
            RenderEngine.getInstance().battleModeHUD.renderWeaponInnateSkill(guiGraphics, deltaTracker);
            RenderEngine.getInstance().battleModeHUD.renderChargingBar(guiGraphics, deltaTracker);
        });
    }

    private static ResourceLocation wrapItemModelPath(ResourceLocation rl) {
        return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "item/" + rl.getPath());
    }

    private void registerModelOverrides() {
        ModelLoadingPlugin.register(pluginContext -> {
            // Replacement of neoforge:separate_transforms
            for (String name : SeparateTransformModels.NAMES) {
                for (String context : SeparateTransformModels.CONTEXTS) {
                    pluginContext.addModels(SeparateTransformModels.variant(name, context));
                }
            }

            // ModelEvent.RegisterAdditional
            SkillCategory.ENUM_MANAGER.universalValues().stream().filter(skillCategory -> !skillCategory.bookIcon().equals(SkillCategory.DEFAULT_BOOK_ICON)).forEach(skillCategory -> {
                pluginContext.addModels(wrapItemModelPath(skillCategory.bookIcon()));
            });

            // ModelEvent.ModifyBakingResult
            ModelResourceLocation skillbookLocation = new ModelResourceLocation(SkillCategory.DEFAULT_BOOK_ICON, ModelResourceLocation.INVENTORY_VARIANT);

            pluginContext.modifyModelAfterBake().register((model, context) -> {
                if (model == null || !skillbookLocation.equals(context.topLevelId()) || model.getOverrides() != ItemOverrides.EMPTY) {
                    // The default categories are declared as predicate overrides in skillbook.json.
                    return model;
                }

                List<ItemOverrides.BakedOverride> skillCategoryOverrides = new ArrayList<>();

                SkillCategory.ENUM_MANAGER.universalValues().stream().filter(skillCategory -> !skillCategory.bookIcon().equals(SkillCategory.DEFAULT_BOOK_ICON)).sorted((c1, c2) -> {
                    return Integer.compare(c2.universalOrdinal(), c1.universalOrdinal());
                }).forEach(skillCategory -> {
                    ItemOverrides.PropertyMatcher[] propertyMatchers = new ItemOverrides.PropertyMatcher[1];
                    propertyMatchers[0] = new ItemOverrides.PropertyMatcher(0, skillCategory.universalOrdinal() / 100.0F);
                    BakedModel bakedModel = context.baker().bake(wrapItemModelPath(skillCategory.bookIcon()), BlockModelRotation.X0_Y0);
                    skillCategoryOverrides.add(new ItemOverrides.BakedOverride(propertyMatchers, bakedModel));
                });

                ItemOverrides overrides = model.getOverrides();
                overrides.overrides = skillCategoryOverrides.toArray(ItemOverrides.BakedOverride[]::new);
                overrides.properties = new ResourceLocation[] { EpicFight.identifier("skill") };

                return model;
            });
        });
    }

    private void registerReloadListeners() {
        new FabricReloadListeners(PackType.CLIENT_RESOURCES)
            .add("joint_masks", JointMaskReloadListener::new)
            .add("meshes", () -> Meshes.INSTANCE)
            .add("animations", AnimationManager::getInstance)
            .add("item_skins", () -> ItemSkinsReloadListener.INSTANCE)
            .add("compute_shaders", () -> (net.minecraft.server.packs.resources.ResourceManagerReloadListener)ComputeShaderProvider::epicfight$registerComputeShaders);
    }

    private void registerFabricCallbacks() {
        // Screen input (ScreenEvent.MouseButtonPressed.Pre, ...)
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.allowMouseClick(screen).register((s, mouseX, mouseY, button) -> !VanillaGUIEventHooks.onMouseButtonPressedInScreen(s));
            ScreenMouseEvents.allowMouseRelease(screen).register((s, mouseX, mouseY, button) -> !VanillaGUIEventHooks.onMouseButtonReleasedInScreen(s));
            ScreenKeyboardEvents.allowKeyPress(screen).register((s, key, scancode, modifiers) -> !VanillaGUIEventHooks.onKeyboardPressedInScreen(s, key));
        });

        // PlayerInteractEvent.RightClickItem (client side)
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (level.isClientSide() && VanillaGeneralClientEventHooks.onUseItemInClientSide(player, player.getItemInHand(hand), hand)) {
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }

            return InteractionResultHolder.pass(player.getItemInHand(hand));
        });

        yesman.epicfight.network.EpicFightClientNetwork.registerClientReceivers();

        // EntityJoinLevelEvent (client side)
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            yesman.epicfight.api.event.impl.VanillaEntityEventHooks.onJoinLevel(entity, level, false);
            yesman.epicfight.api.event.impl.VanillaEntityEventHooks.onAddedToLevel(entity);
        });

        // ClientPlayerNetworkEvent
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) {
                VanillaGeneralClientEventHooks.onPlayerLoggedIn(client.player);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (client.player != null) {
                VanillaGeneralClientEventHooks.onPlayerLoggedOut(client.player);
            }

            FakeLevel.unloadFakeLevel();
        });

        // LevelEvent.Load
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
            if (!(world instanceof FakeLevel)) {
                FakeLevel.getFakeLevel(world);
            }
        });

        // Ticks
        ClientTickEvents.START_CLIENT_TICK.register(client -> FabricGameEvents.BUS.post(new ClientTickEvent.Pre()));
        ClientTickEvents.END_CLIENT_TICK.register(client -> FabricGameEvents.BUS.post(new ClientTickEvent.Post()));
        ClientTickEvents.END_WORLD_TICK.register(world -> FabricGameEvents.BUS.post(new LevelTickEvent.Post(world)));

        // Rendering
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            FabricGameEvents.BUS.post(new RenderLevelStageEvent(RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS, context.matrixStack(), context.camera()));
        });

        WorldRenderEvents.BLOCK_OUTLINE.register((context, hitResult) -> {
            RenderHighlightEvent.Block event = new RenderHighlightEvent.Block();
            FabricGameEvents.BUS.post(event);
            return !event.isCanceled();
        });

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            FabricGameEvents.BUS.post(new ItemTooltipEvent(stack, Minecraft.getInstance().player, lines));
        });
    }
}
