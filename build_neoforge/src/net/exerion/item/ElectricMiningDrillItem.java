/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.exerion.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricMiningDrillItem
extends Item {
    public ElectricMiningDrillItem(Item.Properties properties) {
        super(properties);
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            return 1.0f;
        }
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return 15.0f;
        }
        return 1.0f;
    }

    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            return false;
        }
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide() && state.getDestroySpeed((BlockGetter)level, pos) != 0.0f) {
            stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 1));
        }
        return true;
    }
}

