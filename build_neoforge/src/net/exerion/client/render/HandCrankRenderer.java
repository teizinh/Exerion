/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.SubmitNodeCollector
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
 *  net.minecraft.client.renderer.feature.ModelFeatureRenderer$CrumblingOverlay
 *  net.minecraft.client.renderer.state.CameraRenderState
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionfc
 */
package net.exerion.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.exerion.block.HandCrankBlock;
import net.exerion.block.entity.HandCrankBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class HandCrankRenderer
implements BlockEntityRenderer<HandCrankBlockEntity, HandCrankRenderState> {
    public HandCrankRenderer(BlockEntityRendererProvider.Context context) {
    }

    public HandCrankRenderState createRenderState() {
        return new HandCrankRenderState();
    }

    public void extractRenderState(HandCrankBlockEntity blockEntity, HandCrankRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase((BlockEntity)blockEntity, (BlockEntityRenderState)renderState, (ModelFeatureRenderer.CrumblingOverlay)crumblingOverlay);
        renderState.blockState = blockEntity.getBlockState();
        if (renderState.blockState != null && renderState.blockState.getBlock() instanceof HandCrankBlock) {
            renderState.cranking = (Boolean)renderState.blockState.getValue((Property)HandCrankBlock.CRANKING);
            renderState.rotationAngle = blockEntity.visualRotationAngle;
        }
    }

    public void submit(HandCrankRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (renderState.blockState == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(renderState.rotationAngle));
        poseStack.translate(-0.5, -0.5, -0.5);
        collector.submitBlock(poseStack, renderState.blockState, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static class HandCrankRenderState
    extends BlockEntityRenderState {
        public BlockState blockState;
        public boolean cranking;
        public float rotationAngle;
    }
}

