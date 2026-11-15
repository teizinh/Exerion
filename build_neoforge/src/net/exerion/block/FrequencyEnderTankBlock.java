/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
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
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.Nullable
 */
package net.exerion.block;

import com.mojang.serialization.MapCodec;
import net.exerion.block.entity.FrequencyEnderTankBlockEntity;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.fluid.GlobalFluidNetwork;
import net.exerion.progression.PlayerEraManager;
import net.exerion.progression.TechnologyEra;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class FrequencyEnderTankBlock
extends BaseEntityBlock {
    public static final MapCodec<FrequencyEnderTankBlock> CODEC = FrequencyEnderTankBlock.simpleCodec(FrequencyEnderTankBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public FrequencyEnderTankBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, (Comparable)Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)this.defaultBlockState().setValue(FACING, (Comparable)context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FrequencyEnderTankBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return FrequencyEnderTankBlock.createTickerHelper(type, ModBlockEntityTypes.FREQUENCY_ENDER_TANK_ENTITY, (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }

    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        TechnologyEra playerEra = PlayerEraManager.getPlayerEra(player);
        if (!playerEra.isAtLeast(TechnologyEra.STEEL)) {
            player.displayClientMessage((Component)Component.literal((String)"\u00a7c[Exerion] You need the Steam Era to use the Frequency Ender Tank Ender de Frequ\u00eancia!"), false);
            return InteractionResult.FAIL;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof FrequencyEnderTankBlockEntity) {
            FrequencyEnderTankBlockEntity tankBE = (FrequencyEnderTankBlockEntity)be;
            int currentLava = GlobalFluidNetwork.getLavaAmount(tankBE.getFrequency());
            player.displayClientMessage((Component)Component.literal((String)("\u00a76Lava na Rede (" + tankBE.getFrequency() + "): \u00a7e" + currentLava + " mB")), false);
        }
        return InteractionResult.SUCCESS;
    }
}

