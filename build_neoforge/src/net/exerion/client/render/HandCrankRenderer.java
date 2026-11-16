package net.exerion.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.exerion.block.entity.HandCrankBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class HandCrankRenderer implements BlockEntityRenderer<HandCrankBlockEntity, HandCrankRenderer.HandCrankRenderState> {
    public HandCrankRenderer(BlockEntityRendererProvider.Context context) {
    }

    public HandCrankRenderState createRenderState() {
        return new HandCrankRenderState();
    }

    public void extractRenderState(HandCrankBlockEntity blockEntity, HandCrankRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase((BlockEntity)blockEntity, (BlockEntityRenderState)renderState, crumblingOverlay);
    }

    public void submit(HandCrankRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
    }

    public static class HandCrankRenderState extends BlockEntityRenderState {
    }
}
