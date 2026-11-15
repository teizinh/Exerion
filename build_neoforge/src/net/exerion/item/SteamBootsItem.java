/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 */
package net.exerion.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SteamBootsItem
extends Item {
    public SteamBootsItem(Item.Properties properties) {
        super(properties);
    }

    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (slot == EquipmentSlot.FEET) {
                double motionY;
                int fallBlocks;
                if (player.fallDistance > 0.0 && (fallBlocks = (int)player.fallDistance) > 0) {
                    player.fallDistance = 0.0;
                    stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + fallBlocks));
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 1.5f);
                }
                if ((motionY = player.getDeltaMovement().y) > 0.4 && motionY < 0.45 && !player.onGround() && player.tickCount % 5 == 0 && stack.getDamageValue() < stack.getMaxDamage()) {
                    player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.35, 0.0));
                    stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 2));
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.4f, 2.0f);
                }
            }
        }
    }
}

