package net.exerion.block;

import com.mojang.serialization.MapCodec;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.block.entity.WaterWheelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WaterWheelBlock
extends BaseEntityBlock {
    public static final MapCodec<WaterWheelBlock> CODEC = WaterWheelBlock.simpleCodec(WaterWheelBlock::new);
    public static final BooleanProperty SPINNING = BooleanProperty.create((String)"spinning");
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = (Direction)state.getValue(FACING);
        if (facing == Direction.NORTH || facing == Direction.SOUTH) {
            return Block.box((double)0.0, (double)0.0, (double)2.0, (double)16.0, (double)16.0, (double)14.0);
        }
        return Block.box((double)2.0, (double)0.0, (double)0.0, (double)14.0, (double)16.0, (double)16.0);
    }

    public WaterWheelBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SPINNING, false)).setValue(FACING, Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{SPINNING, FACING});
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WaterWheelBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return WaterWheelBlock.createTickerHelper(type, ModBlockEntityTypes.WATER_WHEEL_ENTITY, (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof WaterWheelBlockEntity wheelBE) {
            boolean hasWater = wheelBE.hasWater();
            int output = wheelBE.getOutputFM();
            if (hasWater) {
                player.displayClientMessage((Component)Component.literal("\u00a7e[Exerion] Roda de \u00c1gua: \u00a7aAtiva (" + output + " FM) \u00a77[\u00c1gua detetada]"), true);
                world.playSound(null, pos, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.5f, 1.0f);
            } else {
                player.displayClientMessage((Component)Component.literal("\u00a7e[Exerion] Roda de \u00c1gua: \u00a7cParada (0 FM) \u00a77[Coloca \u00e1gua adjacente]"), true);
                world.playSound(null, pos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.5f, 1.2f);
            }
        }
        return InteractionResult.SUCCESS;
    }
}

