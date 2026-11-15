/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package net.exerion.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class WrenchItem
extends Item {
    public WrenchItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        String blockId = BuiltInRegistries.BLOCK.getKey((Object)state.getBlock()).getNamespace();
        if (!blockId.equals("exerion")) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            if (player.isSecondaryUseActive()) {
                level.destroyBlock(pos, true);
            } else if (state.hasProperty((Property)BlockStateProperties.FACING)) {
                Direction current = (Direction)state.getValue((Property)BlockStateProperties.FACING);
                Direction next = this.getNextDirection(current);
                level.setBlock(pos, (BlockState)state.setValue((Property)BlockStateProperties.FACING, (Comparable)next), 3);
            } else if (state.hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING)) {
                Direction current = (Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
                Direction next = this.getNextHorizontalDirection(current);
                level.setBlock(pos, (BlockState)state.setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)next), 3);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private Direction getNextDirection(Direction dir) {
        return Direction.values()[(dir.ordinal() + 1) % Direction.values().length];
    }

    private Direction getNextHorizontalDirection(Direction dir) {
        return switch (dir) {
            case Direction.NORTH -> Direction.EAST;
            case Direction.EAST -> Direction.SOUTH;
            case Direction.SOUTH -> Direction.WEST;
            case Direction.WEST -> Direction.NORTH;
            default -> Direction.NORTH;
        };
    }
}

