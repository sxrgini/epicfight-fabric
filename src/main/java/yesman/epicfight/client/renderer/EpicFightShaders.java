package yesman.epicfight.client.renderer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightShaders {
	public static ShaderInstance positionColorNormalShader;
	
	@Nullable
	public static ShaderInstance getPositionColorNormalShader() {
		return positionColorNormalShader;
	}
	
	public static void init() {
		CoreShaderRegistrationCallback.EVENT.register(context -> {
			context.register(EpicFightMod.identifier("solid_model"), DefaultVertexFormat.POSITION_COLOR_NORMAL, reloadedShader -> {
				positionColorNormalShader = reloadedShader;
			});
		});
	}
}
