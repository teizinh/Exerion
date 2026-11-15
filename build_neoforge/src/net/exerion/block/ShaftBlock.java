/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package net.exerion.block;

import com.mojang.serialization.MapCodec;
import net.exerion.block.HandCrankBlock;
import net.exerion.block.WaterWheelBlock;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.block.entity.ShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ShaftBlock
extends BaseEntityBlock {
    public static final MapCodec<ShaftBlock> CODEC = ShaftBlock.simpleCodec(ShaftBlock::new);
    public static final BooleanProperty SPINNING = BooleanProperty.create((String)"spinning");
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public ShaftBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)SPINNING, (Comparable)Boolean.valueOf(false))).setValue(AXIS, (Comparable)Direction.Axis.Y));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{SPINNING, AXIS});
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShaftBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return ShaftBlock.createTickerHelper(type, ModBlockEntityTypes.SHAFT_ENTITY, (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace;
        BlockPos clickedPos;
        Level level = context.getLevel();
        BlockState stateAtClicked = level.getBlockState((clickedPos = context.getClickedPos()).relative((clickedFace = context.getClickedFace()).getOpposite()));
        if (stateAtClicked.getBlock() instanceof ShaftBlock) {
            return (BlockState)((BlockState)this.defaultBlockState().setValue(AXIS, (Comparable)((Direction.Axis)stateAtClicked.getValue(AXIS)))).setValue((Property)SPINNING, (Comparable)Boolean.valueOf(false));
        }
        if (stateAtClicked.getBlock() instanceof HandCrankBlock) {
            return (BlockState)((BlockState)this.defaultBlockState().setValue(AXIS, (Comparable)((Direction)stateAtClicked.getValue(HandCrankBlock.FACING)).getAxis())).setValue((Property)SPINNING, (Comparable)Boolean.valueOf(false));
        }
        if (stateAtClicked.getBlock() instanceof WaterWheelBlock) {
            return (BlockState)((BlockState)this.defaultBlockState().setValue(AXIS, (Comparable)((Direction)stateAtClicked.getValue(WaterWheelBlock.FACING)).getAxis())).setValue((Property)SPINNING, (Comparable)Boolean.valueOf(false));
        }
        return (BlockState)((BlockState)this.defaultBlockState().setValue(AXIS, (Comparable)clickedFace.getAxis())).setValue((Property)SPINNING, (Comparable)Boolean.valueOf(false));
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction.Axis axis = (Direction.Axis)state.getValue(AXIS);
        if (axis == Direction.Axis.X) {
            return Block.box((double)0.0, (double)6.0, (double)6.0, (double)16.0, (double)10.0, (double)10.0);
        }
        if (axis == Direction.Axis.Z) {
            return Block.box((double)6.0, (double)6.0, (double)0.0, (double)10.0, (double)10.0, (double)16.0);
        }
        return Block.box((double)6.0, (double)0.0, (double)6.0, (double)10.0, (double)16.0, (double)10.0);
    }
}

