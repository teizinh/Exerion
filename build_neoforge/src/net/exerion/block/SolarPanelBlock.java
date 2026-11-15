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
import net.exerion.block.entity.SolarPanelBlockEntity;
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

public class SolarPanelBlock
extends BaseEntityBlock {
    public static final MapCodec<SolarPanelBlock> CODEC = SolarPanelBlock.simpleCodec(properties -> new SolarPanelBlock((BlockBehaviour.Properties)properties, 100, 1000, 2));
    public static final BooleanProperty ACTIVE = BooleanProperty.create((String)"active");
    private final int maxTransfer;
    private final int maxCapacity;
    private final int generationRate;

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public SolarPanelBlock(BlockBehaviour.Properties properties, int maxTransfer, int maxCapacity, int generationRate) {
        super(properties);
        this.maxTransfer = maxTransfer;
        this.maxCapacity = maxCapacity;
        this.generationRate = generationRate;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)ACTIVE, (Comparable)Boolean.valueOf(false)));
    }

    public int getMaxTransfer() {
        return this.maxTransfer;
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    public int getGenerationRate() {
        return this.generationRate;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ACTIVE});
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarPanelBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return SolarPanelBlock.createTickerHelper(type, ModBlockEntityTypes.SOLAR_PANEL_ENTITY, (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }

    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        TechnologyEra playerEra = PlayerEraManager.getPlayerEra(player);
        if (!playerEra.isAtLeast(TechnologyEra.ADVANCED)) {
            player.displayClientMessage((Component)Component.translatable((String)"message.exerion.era_locked.block_use", (Object[])new Object[]{TechnologyEra.ADVANCED.getDisplayNameComponent()}), false);
            return InteractionResult.FAIL;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SolarPanelBlockEntity) {
            SolarPanelBlockEntity solarBE = (SolarPanelBlockEntity)be;
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.openMenu((MenuProvider)solarBE);
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        TechnologyEra playerEra = PlayerEraManager.getPlayerEra(player);
        if (!playerEra.isAtLeast(TechnologyEra.ADVANCED)) {
            player.displayClientMessage((Component)Component.translatable((String)"message.exerion.era_locked.block_use", (Object[])new Object[]{TechnologyEra.ADVANCED.getDisplayNameComponent()}), false);
            return InteractionResult.FAIL;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SolarPanelBlockEntity) {
            SolarPanelBlockEntity solarBE = (SolarPanelBlockEntity)be;
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.openMenu((MenuProvider)solarBE);
            }
        }
        return InteractionResult.SUCCESS;
    }
}

