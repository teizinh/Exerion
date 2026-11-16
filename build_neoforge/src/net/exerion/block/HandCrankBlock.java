package net.exerion.block;

import com.mojang.serialization.MapCodec;
import net.exerion.block.entity.HandCrankBlockEntity;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class HandCrankBlock
extends BaseEntityBlock {
    public static final MapCodec<HandCrankBlock> CODEC = HandCrankBlock.simpleCodec(HandCrankBlock::new);
    public static final BooleanProperty CRANKING = BooleanProperty.create((String)"cranking");
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = (Direction)state.getValue(FACING);
        return switch (facing) {
            case UP -> Block.box((double)2.0, (double)0.0, (double)2.0, (double)14.0, (double)16.0, (double)14.0);
            case DOWN -> Block.box((double)2.0, (double)0.0, (double)2.0, (double)14.0, (double)16.0, (double)14.0);
            case NORTH -> Block.box((double)2.0, (double)2.0, (double)0.0, (double)14.0, (double)14.0, (double)16.0);
            case SOUTH -> Block.box((double)2.0, (double)2.0, (double)0.0, (double)14.0, (double)14.0, (double)16.0);
            case WEST -> Block.box((double)0.0, (double)2.0, (double)2.0, (double)16.0, (double)14.0, (double)14.0);
            case EAST -> Block.box((double)0.0, (double)2.0, (double)2.0, (double)16.0, (double)14.0, (double)14.0);
        };
    }

    public HandCrankBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(CRANKING, false)).setValue(FACING, Direction.UP));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{CRANKING, FACING});
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandCrankBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return HandCrankBlock.createTickerHelper(type, ModBlockEntityTypes.HAND_CRANK_ENTITY, (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }

    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return this.performCrank(world, pos, player);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        return this.performCrank(world, pos, player);
    }

    private InteractionResult performCrank(Level world, BlockPos pos, Player player) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof HandCrankBlockEntity) {
            HandCrankBlockEntity crankBE = (HandCrankBlockEntity)be;
            if (player.isShiftKeyDown()) {
                boolean isBurst = crankBE.toggleMode();
                world.playSound(null, pos, SoundEvents.COPPER_GRATE_BREAK, SoundSource.BLOCKS, 0.8f, 1.2f);
                player.displayClientMessage((Component)Component.literal("\u00a7e[Exerion] Manivela: " + (isBurst ? "\u00a7aModo Rajada (Buffer)" : "\u00a7bModo Cont\u00ednuo (Segurar)")), false);
            } else {
                crankBE.crank(world);
                world.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.6f, 0.9f);
                if (world instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)world;
                    BlockState bState = world.getBlockState(pos);
                    Direction facing = bState.hasProperty(FACING) ? (Direction)bState.getValue(FACING) : Direction.UP;
                    double px = (double)pos.getX() + 0.5 + (double)facing.getStepX() * 0.4;
                    double py = (double)pos.getY() + 0.5 + (double)facing.getStepY() * 0.4;
                    double pz = (double)pos.getZ() + 0.5 + (double)facing.getStepZ() * 0.4;
                    serverLevel.sendParticles((ParticleOptions)ParticleTypes.CRIT, px, py, pz, 3, 0.1, 0.1, 0.1, 0.0);
                }
                int rate = crankBE.getOutputFM();
                if (crankBE.isBurstMode()) {
                    player.displayClientMessage((Component)Component.literal("\u00a7e[Exerion] Manivela: \u00a7a" + rate + " FM gerados \u00a77(Buffer: " + crankBE.getFmBuffer() + "/" + crankBE.getMaxBuffer() + ")"), true);
                } else {
                    player.displayClientMessage((Component)Component.literal("\u00a7e[Exerion] Manivela: \u00a7b" + rate + " FM gerados \u00a77(Modo Cont\u00ednuo)"), true);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}

