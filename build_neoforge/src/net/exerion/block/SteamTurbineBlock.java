/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
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
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.Nullable
 */
package net.exerion.block;

import com.mojang.serialization.MapCodec;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.block.entity.SteamTurbineBlockEntity;
import net.exerion.progression.PlayerEraManager;
import net.exerion.progression.TechnologyEra;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SteamTurbineBlock
extends BaseEntityBlock {
    public static final MapCodec<SteamTurbineBlock> CODEC = SteamTurbineBlock.simpleCodec(SteamTurbineBlock::new);
    public static final BooleanProperty TURNING = BooleanProperty.create((String)"turning");

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public SteamTurbineBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)TURNING, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{TURNING});
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SteamTurbineBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return SteamTurbineBlock.createTickerHelper(type, ModBlockEntityTypes.STEAM_TURBINE_ENTITY, (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }

    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        TechnologyEra playerEra = PlayerEraManager.getPlayerEra(player);
        if (!playerEra.isAtLeast(TechnologyEra.STEEL)) {
            player.displayClientMessage((Component)Component.translatable((String)"message.exerion.era_locked.block_use", (Object[])new Object[]{TechnologyEra.STEEL.getDisplayNameComponent()}), false);
            return InteractionResult.FAIL;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SteamTurbineBlockEntity) {
            SteamTurbineBlockEntity turbineBE = (SteamTurbineBlockEntity)be;
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.openMenu((MenuProvider)turbineBE);
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        TechnologyEra playerEra = PlayerEraManager.getPlayerEra(player);
        if (!playerEra.isAtLeast(TechnologyEra.STEEL)) {
            player.displayClientMessage((Component)Component.translatable((String)"message.exerion.era_locked.block_use", (Object[])new Object[]{TechnologyEra.STEEL.getDisplayNameComponent()}), false);
            return InteractionResult.FAIL;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SteamTurbineBlockEntity) {
            SteamTurbineBlockEntity turbineBE = (SteamTurbineBlockEntity)be;
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.openMenu((MenuProvider)turbineBE);
            }
        }
        return InteractionResult.SUCCESS;
    }
}

