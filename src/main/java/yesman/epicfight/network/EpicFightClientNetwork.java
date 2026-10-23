package yesman.epicfight.network;

import yesman.epicfight.network.server.SPSetRemotePlayerSkill;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/// Client-only registration of client-bound packet receivers.
public final class EpicFightClientNetwork {
	private EpicFightClientNetwork() {}

	public static void registerClientReceivers() {
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_ABSORPTION, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleAbsorption(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_ADD_LEARNED_SKILL, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleAddLearnedSkill(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_HANDLE_SKILL_DATA, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleSkillData(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_ANIMATOR_CONTROL, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleAnimatorControl(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_CHANGE_GAMERULE, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleChangeGameRule(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_CHANGE_LIVING_MOTION, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleChangeLivingMotion(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_CHANGE_PLAYER_MODE, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleChangePlayerMode(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_CHANGE_SKILL, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleChangeSkill(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_CLEAR_SKILLS, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleClearSkills(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_DATAPACK_SYNC, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleDataPack(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_ENTITY_PAIRING, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleEntityPairing(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_CREATE_FRACTURE, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleFracture(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_SET_PLAYER_Y_ROT, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleModelYRot(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_DISABLE_Y_ROT, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleDisableModelYRot(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_SET_LAST_ATTACK_RESULT, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleSetLastAttackResult(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_SET_PLAYER_MODE, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleSetPlayerMode(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_SET_GRAPPLING_TARGET, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleSetGrapplingTarget(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_MOB_EFFECT, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleMobEffect(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_MODIFY_EXPANDED_ENTITY_DATA, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleModifyExpandedEntityData(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_PLAY_UI_SOUND, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handlePlayUiSound(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_REMOVE_SKILL, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleRemoveSkill(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_SET_ATTACK_TARGET, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleSetAttackTarget(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(SPSetRemotePlayerSkill.CLIENT_BOUND_SET_REMOTE_PLAYER_SKILL, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleSetRemotePlayerSkill(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_SET_SKILL_CONTAINER_VALUE, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleSetSkillContainerValue(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_SKILL_FEEDBACK, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleSkillFeedback(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_UPDATE_PLAYER_INPUT, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleUpdatePlayerInput(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.CLIENT_BOUND_INIT_SKILLS, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleInitSkills(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.BI_DIRECTIONAL_MODIFY_ANIMATION_VARIABLE, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleAnimationVariablePacket(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.BI_DIRECTIONAL_SYNC_ANIMATION_POSITION, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleSyncAnimationPosition(payload, ctx::player));
		ClientPlayNetworking.registerGlobalReceiver(ManagedCustomPacketPayload.BI_DIRECTIONAL_SYNC_EMOTE_SLOTS, (payload, ctx) -> EpicFightClientBoundPayloadHandler.handleSyncEmoteSlot(payload, ctx::player));
	}
}
