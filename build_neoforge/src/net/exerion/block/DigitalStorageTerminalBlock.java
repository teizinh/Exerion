/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.Nullable
 */
package net.exerion.block;

import com.mojang.serialization.MapCodec;
import net.exerion.block.entity.DigitalStorageTerminalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class DigitalStorageTerminalBlock
extends BaseEntityBlock {
    public static final MapCodec<DigitalStorageTerminalBlock> CODEC = DigitalStorageTerminalBlock.simpleCodec(DigitalStorageTerminalBlock::new);

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public DigitalStorageTerminalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DigitalStorageTerminalBlockEntity(pos, state);
    }

    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof DigitalStorageTerminalBlockEntity) {
            DigitalStorageTerminalBlockEntity terminalBE = (DigitalStorageTerminalBlockEntity)be;
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                terminalBE.rebuildContainer();
                serverPlayer.openMenu((MenuProvider)terminalBE);
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof DigitalStorageTerminalBlockEntity) {
            DigitalStorageTerminalBlockEntity terminalBE = (DigitalStorageTerminalBlockEntity)be;
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                terminalBE.rebuildContainer();
                serverPlayer.openMenu((MenuProvider)terminalBE);
            }
        }
        return InteractionResult.SUCCESS;
    }
}

