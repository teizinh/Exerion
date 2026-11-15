/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.exerion.item;

import net.exerion.progression.EraProgressionRegistry;
import net.exerion.progression.PlayerEraManager;
import net.exerion.progression.TechnologyEra;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EraScannerItem
extends Item {
    public EraScannerItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            if (!world.isClientSide()) {
                player.displayClientMessage((Component)Component.translatable((String)"message.exerion.scanner_no_charge").withStyle(ChatFormatting.RED), false);
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 1.0f);
            return InteractionResult.FAIL;
        }
        if (!world.isClientSide()) {
            BlockState state = world.getBlockState(pos);
            TechnologyEra required = EraProgressionRegistry.getRequiredEra(state.getBlock());
            String blockName = Component.translatable((String)state.getBlock().getDescriptionId()).getString();
            player.displayClientMessage((Component)Component.translatable((String)"message.exerion.scanner.block_info", (Object[])new Object[]{blockName}).withStyle(ChatFormatting.AQUA), false);
            player.displayClientMessage((Component)Component.translatable((String)"message.exerion.scanner.era_info", (Object[])new Object[]{required.getDisplayNameComponent()}).withStyle(ChatFormatting.LIGHT_PURPLE), false);
            stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 10));
            world.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.8f, 1.5f);
        }
        return InteractionResult.SUCCESS;
    }

    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            if (!world.isClientSide()) {
                player.displayClientMessage((Component)Component.translatable((String)"message.exerion.scanner_no_charge").withStyle(ChatFormatting.RED), false);
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 1.0f);
            return InteractionResult.FAIL;
        }
        if (!world.isClientSide()) {
            TechnologyEra current = PlayerEraManager.getPlayerEra(player);
            player.displayClientMessage((Component)Component.translatable((String)"message.exerion.scanner.current_era", (Object[])new Object[]{current.getDisplayNameComponent()}).withStyle(ChatFormatting.YELLOW), false);
            stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 10));
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.8f, 1.2f);
        }
        return InteractionResult.SUCCESS;
    }
}

