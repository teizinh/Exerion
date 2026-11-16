package net.exerion.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.exerion.block.ShaftBlock;
import net.exerion.block.entity.ShaftBlockEntity;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class ShaftRenderer implements BlockEntityRenderer<ShaftBlockEntity, ShaftRenderer.ShaftRenderState> {
    private final BlockRenderDispatcher blockRenderer;

    public ShaftRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.blockRenderDispatcher();
    }

    @Override
    public ShaftRenderState createRenderState() {
        return new ShaftRenderState();
    }

    @Override
    public void extractRenderState(ShaftBlockEntity blockEntity, ShaftRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase((BlockEntity)blockEntity, (BlockEntityRenderState)renderState, crumblingOverlay);
        renderState.spinning = blockEntity.getBlockState() != null && blockEntity.getBlockState().hasProperty(ShaftBlock.SPINNING) && blockEntity.getBlockState().getValue(ShaftBlock.SPINNING);
        renderState.axis = (blockEntity.getBlockState() != null && blockEntity.getBlockState().hasProperty(ShaftBlock.AXIS)) ? blockEntity.getBlockState().getValue(ShaftBlock.AXIS) : Direction.Axis.Y;
        if (renderState.spinning && blockEntity.getLevel() != null) {
            renderState.rotationAngle = ((float)blockEntity.getLevel().getGameTime() + partialTick) * 8.0f % 360.0f;
        } else {
            renderState.rotationAngle = 0.0f;
        }
    }

    @Override
    public void submit(ShaftRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (renderState.blockState == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        if (renderState.axis == Direction.Axis.X) {
            poseStack.mulPose(Axis.XP.rotationDegrees(renderState.rotationAngle));
        } else if (renderState.axis == Direction.Axis.Z) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(renderState.rotationAngle));
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(renderState.rotationAngle));
        }
        poseStack.translate(-0.5, -0.5, -0.5);
        BlockStateModel model = this.blockRenderer.getBlockModel(renderState.blockState);
        RenderType renderType = ItemBlockRenderTypes.getRenderType(renderState.blockState);
        if (model != null && renderType != null) {
            collector.submitBlockModel(poseStack, renderType, model, 1.0f, 1.0f, 1.0f, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
        poseStack.popPose();
    }

    public static class ShaftRenderState extends BlockEntityRenderState {
        public boolean spinning;
        public Direction.Axis axis;
        public float rotationAngle;
    }
}
