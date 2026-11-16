package net.exerion.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.exerion.block.WaterWheelBlock;
import net.exerion.block.entity.WaterWheelBlockEntity;
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

public class WaterWheelRenderer implements BlockEntityRenderer<WaterWheelBlockEntity, WaterWheelRenderer.WaterWheelRenderState> {
    private final BlockRenderDispatcher blockRenderer;

    public WaterWheelRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.blockRenderDispatcher();
    }

    @Override
    public WaterWheelRenderState createRenderState() {
        return new WaterWheelRenderState();
    }

    @Override
    public void extractRenderState(WaterWheelBlockEntity blockEntity, WaterWheelRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase((BlockEntity)blockEntity, (BlockEntityRenderState)renderState, crumblingOverlay);
        renderState.spinning = blockEntity.getBlockState() != null && blockEntity.getBlockState().hasProperty(WaterWheelBlock.SPINNING) && blockEntity.getBlockState().getValue(WaterWheelBlock.SPINNING);
        renderState.facing = (blockEntity.getBlockState() != null && blockEntity.getBlockState().hasProperty(WaterWheelBlock.FACING)) ? blockEntity.getBlockState().getValue(WaterWheelBlock.FACING) : Direction.NORTH;
        if (renderState.spinning && blockEntity.getLevel() != null) {
            renderState.rotationAngle = ((float)blockEntity.getLevel().getGameTime() + partialTick) * 4.0f % 360.0f;
        } else {
            renderState.rotationAngle = 0.0f;
        }
    }

    @Override
    public void submit(WaterWheelRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (renderState.blockState == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        Direction facing = renderState.facing;
        float angle = renderState.rotationAngle;
        if (facing == Direction.NORTH || facing == Direction.SOUTH) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
        } else if (facing != null) {
            poseStack.mulPose(Axis.XP.rotationDegrees(angle));
        }
        poseStack.translate(-0.5, -0.5, -0.5);
        BlockStateModel model = this.blockRenderer.getBlockModel(renderState.blockState);
        RenderType renderType = ItemBlockRenderTypes.getRenderType(renderState.blockState);
        if (model != null && renderType != null) {
            collector.submitBlockModel(poseStack, renderType, model, 1.0f, 1.0f, 1.0f, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
        poseStack.popPose();
    }

    public static class WaterWheelRenderState extends BlockEntityRenderState {
        public boolean spinning;
        public Direction facing;
        public float rotationAngle;
    }
}
