/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.SubmitNodeCollector
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
 *  net.minecraft.client.renderer.feature.ModelFeatureRenderer$CrumblingOverlay
 *  net.minecraft.client.renderer.state.CameraRenderState
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package net.exerion.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.exerion.block.ModBlocks;
import net.exerion.block.entity.PumpBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PumpRenderer
implements BlockEntityRenderer<PumpBlockEntity, PumpRenderState> {
    public PumpRenderer(BlockEntityRendererProvider.Context context) {
    }

    public PumpRenderState createRenderState() {
        return new PumpRenderState();
    }

    public void extractRenderState(PumpBlockEntity blockEntity, PumpRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase((BlockEntity)blockEntity, (BlockEntityRenderState)renderState, (ModelFeatureRenderer.CrumblingOverlay)crumblingOverlay);
        renderState.blockState = blockEntity.getBlockState();
        renderState.pumpDepth = blockEntity.getPumpDepth();
        renderState.isPumping = blockEntity.isPumping();
        if (blockEntity.getLevel() != null) {
            renderState.gameTime = (float)blockEntity.getLevel().getGameTime() + partialTick;
        }
    }

    public void submit(PumpRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (renderState.blockState == null || renderState.pumpDepth == 0) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        BlockState pipeState = ModBlocks.STEEL_CASING.defaultBlockState();
        poseStack.pushPose();
        poseStack.translate(0.0, (double)(-renderState.pumpDepth) / 2.0, 0.0);
        poseStack.scale(0.25f, (float)renderState.pumpDepth, 0.25f);
        poseStack.translate(-0.5, -0.5, -0.5);
        collector.submitBlock(poseStack, pipeState, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
        if (renderState.isPumping) {
            poseStack.pushPose();
            float bobbingOffset = (Mth.sin((double)(renderState.gameTime * 0.5f)) + 1.0f) * 0.5f;
            float animatedDepth = (float)(-(renderState.pumpDepth - 1)) * bobbingOffset;
            poseStack.translate(0.0, (double)animatedDepth - 0.5, 0.0);
            poseStack.scale(0.35f, 0.5f, 0.35f);
            poseStack.translate(-0.5, -0.5, -0.5);
            BlockState headState = ModBlocks.COPPER_CABLE.defaultBlockState();
            collector.submitBlock(poseStack, headState, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    public static class PumpRenderState
    extends BlockEntityRenderState {
        public BlockState blockState;
        public int pumpDepth;
        public boolean isPumping;
        public float gameTime;
    }
}

