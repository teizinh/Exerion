package net.exerion.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class WrenchItem extends Item {
    public WrenchItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        String namespace = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace();
        if (!namespace.equals("exerion")) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            if (player.isSecondaryUseActive()) {
                ItemStack drop = new ItemStack(state.getBlock());
                BlockEntity be = level.getBlockEntity(pos);
                if (be != null) {
                    drop.applyComponents(be.collectComponents());
                }
                level.removeBlock(pos, false);
                Block.popResource(level, pos, drop);
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.6f, 1.4f);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8, 0.2, 0.2, 0.2, 0.05);
                }
                player.displayClientMessage(Component.literal("§a[Exerion] Máquina desmontada preservando inventário!"), true);
            } else if (state.hasProperty(BlockStateProperties.FACING)) {
                Direction current = state.getValue(BlockStateProperties.FACING);
                Direction next = getNextDirection(current);
                level.setBlock(pos, state.setValue(BlockStateProperties.FACING, next), 3);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8f, 1.2f);
                player.displayClientMessage(Component.literal("§b[Exerion] Orientação: " + next.getName()), true);
            } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                Direction current = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                Direction next = getNextHorizontalDirection(current);
                level.setBlock(pos, state.setValue(BlockStateProperties.HORIZONTAL_FACING, next), 3);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8f, 1.2f);
                player.displayClientMessage(Component.literal("§b[Exerion] Orientação: " + next.getName()), true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private Direction getNextDirection(Direction dir) {
        return Direction.values()[(dir.ordinal() + 1) % Direction.values().length];
    }

    private Direction getNextHorizontalDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
            default -> Direction.NORTH;
        };
    }
}
