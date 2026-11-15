/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 */
package net.exerion.item;

import net.exerion.block.entity.CoalBoilerBlockEntity;
import net.exerion.energy.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class LogicHelmetItem
extends Item {
    public LogicHelmetItem(Item.Properties properties) {
        super(properties);
    }

    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (slot == EquipmentSlot.HEAD) {
                BlockPos targetPos;
                BlockEntity be;
                BlockHitResult hit;
                if (player.tickCount % 80 == 0 && stack.getDamageValue() < stack.getMaxDamage()) {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
                    stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 1));
                }
                if (player.tickCount % 4 == 0 && stack.getDamageValue() < stack.getMaxDamage() && (hit = (BlockHitResult)player.pick(6.0, 1.0f, false)).getType() == HitResult.Type.BLOCK && (be = level.getBlockEntity(targetPos = hit.getBlockPos())) != null) {
                    String msg = null;
                    if (be instanceof IEnergyStorage) {
                        IEnergyStorage energyStorage = (IEnergyStorage)be;
                        msg = "\u00a7b[HUD] " + be.getBlockState().getBlock().getName().getString() + " \u00a77| \u00a7eEnergia: \u00a7f" + energyStorage.getEnergy() + " / " + energyStorage.getMaxEnergy() + " EU";
                    } else if (be instanceof CoalBoilerBlockEntity) {
                        CoalBoilerBlockEntity boiler = (CoalBoilerBlockEntity)be;
                        boolean isBurning = boiler.getBurnTime() > 0;
                        msg = "\u00a7b[HUD] Caldeira a Carv\u00e3o \u00a77| \u00a7eCombust\u00e3o: " + (isBurning ? "\u00a7aA Queimar" : "\u00a7cDesligada");
                    }
                    if (msg != null) {
                        player.displayClientMessage((Component)Component.literal(msg), true);
                    }
                }
            }
        }
    }
}

