package net.exerion.block;

import com.mojang.serialization.MapCodec;
import net.exerion.block.entity.CableBlockEntity;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.energy.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CableBlock
extends BaseEntityBlock {
    public static final MapCodec<CableBlock> CODEC = CableBlock.RecordCodecBuilderMapCodecHelper();
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    private final int maxTransfer;
    private final int maxCapacity;

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Block.box((double)6.0, (double)6.0, (double)6.0, (double)10.0, (double)10.0, (double)10.0);
        if (((Boolean)state.getValue(NORTH)).booleanValue()) {
            shape = Shapes.or((VoxelShape)shape, (VoxelShape)Block.box((double)6.0, (double)6.0, (double)0.0, (double)10.0, (double)10.0, (double)6.0));
        }
        if (((Boolean)state.getValue(SOUTH)).booleanValue()) {
            shape = Shapes.or((VoxelShape)shape, (VoxelShape)Block.box((double)6.0, (double)6.0, (double)10.0, (double)10.0, (double)10.0, (double)16.0));
        }
        if (((Boolean)state.getValue(WEST)).booleanValue()) {
            shape = Shapes.or((VoxelShape)shape, (VoxelShape)Block.box((double)0.0, (double)6.0, (double)6.0, (double)6.0, (double)10.0, (double)10.0));
        }
        if (((Boolean)state.getValue(EAST)).booleanValue()) {
            shape = Shapes.or((VoxelShape)shape, (VoxelShape)Block.box((double)10.0, (double)6.0, (double)6.0, (double)16.0, (double)10.0, (double)10.0));
        }
        if (((Boolean)state.getValue(DOWN)).booleanValue()) {
            shape = Shapes.or((VoxelShape)shape, (VoxelShape)Block.box((double)6.0, (double)0.0, (double)6.0, (double)10.0, (double)6.0, (double)10.0));
        }
        if (((Boolean)state.getValue(UP)).booleanValue()) {
            shape = Shapes.or((VoxelShape)shape, (VoxelShape)Block.box((double)6.0, (double)10.0, (double)6.0, (double)10.0, (double)16.0, (double)10.0));
        }
        return shape;
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    private static MapCodec<CableBlock> RecordCodecBuilderMapCodecHelper() {
        return CableBlock.simpleCodec(properties -> new CableBlock((BlockBehaviour.Properties)properties, 100, 500));
    }

    public CableBlock(BlockBehaviour.Properties properties, int maxTransfer, int maxCapacity) {
        super(properties);
        this.maxTransfer = maxTransfer;
        this.maxCapacity = maxCapacity;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(UP, false)).setValue(DOWN, false));
    }

    public int getMaxTransfer() {
        return this.maxTransfer;
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{NORTH, EAST, SOUTH, WEST, UP, DOWN});
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Zero-overhead: Cable polling ticks replaced completely by GlobalEnergyNetwork native/Union-Find solver
        return null;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide()) {
            net.exerion.energy.GlobalEnergyNetwork.onCableAdded(level, pos, this.maxTransfer, this.maxCapacity);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean isMoving) {
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
        net.exerion.energy.GlobalEnergyNetwork.onCableRemoved(level, pos);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.makeConnections(level, pos, this.defaultBlockState());
    }

    protected BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (levelReader instanceof Level) {
            Level level = (Level)levelReader;
            return this.makeConnections(level, pos, state);
        }
        return state;
    }

    private BlockState makeConnections(Level level, BlockPos pos, BlockState state) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, this.canConnectTo(level, pos.north()))).setValue(EAST, this.canConnectTo(level, pos.east()))).setValue(SOUTH, this.canConnectTo(level, pos.south()))).setValue(WEST, this.canConnectTo(level, pos.west()))).setValue(UP, this.canConnectTo(level, pos.above()))).setValue(DOWN, this.canConnectTo(level, pos.below()));
    }

    private boolean canConnectTo(Level level, BlockPos targetPos) {
        BlockEntity be = level.getBlockEntity(targetPos);
        return be instanceof IEnergyStorage;
    }
}

