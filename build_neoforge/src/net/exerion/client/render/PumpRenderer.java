package net.exerion.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.exerion.block.entity.PumpBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class PumpRenderer implements BlockEntityRenderer<PumpBlockEntity, PumpRenderer.PumpRenderState> {
    public PumpRenderer(BlockEntityRendererProvider.Context context) {
    }

    public PumpRenderState createRenderState() {
        return new PumpRenderState();
    }

    public void extractRenderState(PumpBlockEntity blockEntity, PumpRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase((BlockEntity)blockEntity, (BlockEntityRenderState)renderState, crumblingOverlay);
    }

    public void submit(PumpRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
    }

    public static class PumpRenderState extends BlockEntityRenderState {
    }
}
