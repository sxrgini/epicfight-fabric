package yesman.epicfight.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.platform.fabric.network.FabricNetworkSender;
import yesman.epicfight.network.client.*;
import yesman.epicfight.network.common.BiDirectionalAnimationVariable;
import yesman.epicfight.network.common.BiDirectionalSyncAnimationPositionPacket;
import yesman.epicfight.network.common.BiDirectionalSyncEmoteSlots;
import yesman.epicfight.network.server.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class EpicFightNetworkManager {
	/// Registers payload types for both directions. Must run on both physical sides.
	public static void registerPayloadTypes() {
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_ABSORPTION, SPAbsorption.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_ADD_LEARNED_SKILL, SPAddLearnedSkill.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_HANDLE_SKILL_DATA, SPHandleSkillData.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_ANIMATOR_CONTROL, SPAnimatorControl.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_CHANGE_GAMERULE, SPChangeGamerule.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_CHANGE_LIVING_MOTION, SPChangeLivingMotion.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_CHANGE_PLAYER_MODE, SPChangePlayerMode.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_CHANGE_SKILL, SPChangeSkill.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_CLEAR_SKILLS, SPClearSkills.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_DATAPACK_SYNC, SPDatapackSync.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_ENTITY_PAIRING, SPEntityPairingPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_CREATE_FRACTURE, SPCreateTerrainFracture.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_SET_PLAYER_Y_ROT, SPModifyPlayerData.SetPlayerYRot.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_DISABLE_Y_ROT, SPModifyPlayerData.DisablePlayerYRot.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_SET_LAST_ATTACK_RESULT, SPModifyPlayerData.SetLastAttackResult.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_SET_PLAYER_MODE, SPModifyPlayerData.SetPlayerMode.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_SET_GRAPPLING_TARGET, SPModifyPlayerData.SetGrapplingTarget.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_MOB_EFFECT, SPMobEffectControl.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_MODIFY_EXPANDED_ENTITY_DATA, SPModifyExpandedEntityData.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_PLAY_UI_SOUND, SPPlayUISound.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_REMOVE_SKILL, SPRemoveSkillAndLearn.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_SET_ATTACK_TARGET, SPSetAttackTarget.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(SPSetRemotePlayerSkill.CLIENT_BOUND_SET_REMOTE_PLAYER_SKILL, SPSetRemotePlayerSkill.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_SET_SKILL_CONTAINER_VALUE, SPSetSkillContainerValue.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_SKILL_FEEDBACK, SPSkillFeedback.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_UPDATE_PLAYER_INPUT, SPUpdatePlayerInput.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.CLIENT_BOUND_INIT_SKILLS, SPInitSkills.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.SERVER_BOUND_ANIMATOR_CONTROL, CPAnimatorControl.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.SERVER_BOUND_CHANGE_PLAYER_MODE, CPChangePlayerMode.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.SERVER_BOUND_CHANGE_SKILL, CPChangeSkill.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.SERVER_BOUND_EXECUTE_SKILL, CPSkillRequest.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.SERVER_BOUND_MODIFY_ENTITY_PATCH_Y_ROT, CPModifyEntityModelYRot.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.SERVER_BOUND_MODIFY_SKILL_DATA, CPHandleSkillData.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.SERVER_BOUND_PAIRING_ANIMATION_REGISTRY, CPPairingAnimationRegistry.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.SERVER_BOUND_SET_PLAYER_TARGET, CPSetPlayerTarget.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.SERVER_BOUND_SET_STAMINA, CPSetStamina.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.SERVER_BOUND_UPDATE_PLAYER_INPUT, CPUpdatePlayerInput.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.BI_DIRECTIONAL_MODIFY_ANIMATION_VARIABLE, BiDirectionalAnimationVariable.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.BI_DIRECTIONAL_MODIFY_ANIMATION_VARIABLE, BiDirectionalAnimationVariable.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.BI_DIRECTIONAL_SYNC_ANIMATION_POSITION, BiDirectionalSyncAnimationPositionPacket.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.BI_DIRECTIONAL_SYNC_ANIMATION_POSITION, BiDirectionalSyncAnimationPositionPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ManagedCustomPacketPayload.BI_DIRECTIONAL_SYNC_EMOTE_SLOTS, BiDirectionalSyncEmoteSlots.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(ManagedCustomPacketPayload.BI_DIRECTIONAL_SYNC_EMOTE_SLOTS, BiDirectionalSyncEmoteSlots.STREAM_CODEC);
	}

	/// Registers server-side receivers of client-bound packets.
	public static void registerServerReceivers() {
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.SERVER_BOUND_ANIMATOR_CONTROL, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleAnimatorControl(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.SERVER_BOUND_CHANGE_PLAYER_MODE, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleChangePlayerMode(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.SERVER_BOUND_CHANGE_SKILL, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleChangeSkill(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.SERVER_BOUND_EXECUTE_SKILL, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleExecuteSkill(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.SERVER_BOUND_MODIFY_ENTITY_PATCH_Y_ROT, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleModifyPlayerModelYRot(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.SERVER_BOUND_MODIFY_SKILL_DATA, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleSkillData(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.SERVER_BOUND_PAIRING_ANIMATION_REGISTRY, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handlePairingAnimationRegistry(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.SERVER_BOUND_SET_PLAYER_TARGET, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleSetPlayerTarget(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.SERVER_BOUND_SET_STAMINA, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleSetStamina(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.SERVER_BOUND_UPDATE_PLAYER_INPUT, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleUpdatePlayerInput(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.BI_DIRECTIONAL_MODIFY_ANIMATION_VARIABLE, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleAnimationVariablePacket(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.BI_DIRECTIONAL_SYNC_ANIMATION_POSITION, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleSyncAnimationPosition(payload, ctx::player));
		ServerPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.BI_DIRECTIONAL_SYNC_EMOTE_SLOTS, (payload, ctx) -> EpicFightServerBoundPayloadHandler.handleSyncEmoteSlot(payload, ctx::player));
	}

	@SuppressWarnings("unchecked")
	public static FriendlyByteBuf encodeObjectToBuffer(StreamEncoder<ByteBuf, ?> encoder, Object value) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		((StreamEncoder<ByteBuf, Object>)encoder).encode(buf, value);
		return buf;
	}
	
	public static void sendToServer(CustomPacketPayload message, CustomPacketPayload... others) {
		FabricNetworkSender.sendToServer(message, others);
	}
	
	public static void sendToAll(CustomPacketPayload message, CustomPacketPayload... others) {
		FabricNetworkSender.sendToAll(message, others);
	}

	public static void sendToAllPlayerTrackingThisEntity(CustomPacketPayload message, Entity entity, CustomPacketPayload... others) {
		FabricNetworkSender.sendToTracking(entity, message, others);
	}
	
	public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player, CustomPacketPayload... others) {
		FabricNetworkSender.sendToPlayer(message, player, others);
	}
	
	public static void sendToAllPlayerTrackingThisEntityWithSelf(CustomPacketPayload message, ServerPlayer entity, CustomPacketPayload... others) {
		FabricNetworkSender.sendToTrackingAndSelf(entity, message, others);
	}
	
	public static void sendToAllPlayerTrackingThisChunkWithSelf(CustomPacketPayload message, ServerLevel serverLevel, ChunkPos chunkPos, CustomPacketPayload... others) {
		FabricNetworkSender.sendToTrackingChunk(serverLevel, chunkPos, message, others);
	}
	
	public static class PayloadBundleBuilder {
		public static PayloadBundleBuilder create() {
			return new PayloadBundleBuilder();
		}
		
		public static PayloadBundleBuilder beginWith(CustomPacketPayload payload) {
			return new PayloadBundleBuilder().and(payload);
		}
		
		private final List<CustomPacketPayload> payloads = new ArrayList<> ();
		
		public PayloadBundleBuilder and(CustomPacketPayload payload) {
			this.payloads.add(payload);
			return this;
		}
		
		public void send(BiConsumer<CustomPacketPayload, CustomPacketPayload[]> sendTo) {
			if (this.payloads.size() == 1) {
				sendTo.accept(this.payloads.getFirst(), new CustomPacketPayload[0]);
			} else if (!this.payloads.isEmpty()) {
				sendTo.accept(this.payloads.getFirst(), this.payloads.subList(1, this.payloads.size()).toArray(new CustomPacketPayload[0]));
			}
		}
	}
}
