package net.exerion.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.exerion.block.entity.ShaftBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class ShaftRenderer implements BlockEntityRenderer<ShaftBlockEntity, ShaftRenderer.ShaftRenderState> {
    public ShaftRenderer(BlockEntityRendererProvider.Context context) {
    }

    public ShaftRenderState createRenderState() {
        return new ShaftRenderState();
    }

    public void extractRenderState(ShaftBlockEntity blockEntity, ShaftRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase((BlockEntity)blockEntity, (BlockEntityRenderState)renderState, crumblingOverlay);
    }

    public void submit(ShaftRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
    }

    public static class ShaftRenderState extends BlockEntityRenderState {
    }
}
