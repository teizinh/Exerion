/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.Level
 */
package net.exerion.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class PlasmaSwordItem
extends Item {
    public PlasmaSwordItem(Item.Properties properties) {
        super(properties);
    }

    public static boolean isActive(ItemStack stack) {
        CustomData customData = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            return tag.getBooleanOr("Active", false);
        }
        return false;
    }

    public static void setActive(ItemStack stack, boolean active) {
        stack.update(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY, customData -> {
            CompoundTag tag = customData.copyTag();
            tag.putBoolean("Active", active);
            return CustomData.of((CompoundTag)tag);
        });
    }

    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide()) {
            boolean active = PlasmaSwordItem.isActive(stack);
            if (active && stack.getDamageValue() < stack.getMaxDamage()) {
                target.hurt(attacker.damageSources().mobAttack(attacker), 9.0f);
                stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 2));
                Level level = attacker.level();
                if (level instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)level;
                    serverLevel.sendParticles((ParticleOptions)ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY() + 1.0, target.getZ(), 8, 0.3, 0.3, 0.3, 0.1);
                    serverLevel.sendParticles((ParticleOptions)ParticleTypes.SCULK_SOUL, target.getX(), target.getY() + 1.0, target.getZ(), 3, 0.2, 0.2, 0.2, 0.05);
                }
                attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.8f, 1.3f);
            } else {
                target.hurt(attacker.damageSources().mobAttack(attacker), 2.0f);
            }
        }
    }

    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            if (stack.getDamageValue() >= stack.getMaxDamage()) {
                player.displayClientMessage((Component)Component.literal((String)"\u00a7c[Plasma Sword] Out of energy! Charge it in a BatBox or Peitoral."), false);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.5f, 1.0f);
                return InteractionResult.FAIL;
            }
            boolean active = !PlasmaSwordItem.isActive(stack);
            PlasmaSwordItem.setActive(stack, active);
            if (active) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 0.4f, 1.6f);
                player.displayClientMessage((Component)Component.literal((String)"\u00a7b[Exerion] Espada de Plasma: \u00a7aLIGADA"), false);
            } else {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.4f, 1.2f);
                player.displayClientMessage((Component)Component.literal((String)"\u00a7b[Exerion] Espada de Plasma: \u00a7cDESLIGADA"), false);
            }
        }
        return InteractionResult.SUCCESS;
    }

    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        boolean active = PlasmaSwordItem.isActive(stack);
        if (active) {
            boolean isSelected;
            if (stack.getDamageValue() >= stack.getMaxDamage()) {
                PlasmaSwordItem.setActive(stack, false);
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 0.8f);
                if (entity instanceof Player) {
                    Player player = (Player)entity;
                    player.displayClientMessage((Component)Component.literal((String)"\u00a7b[Exerion] Espada de Plasma: \u00a7cDESLIGADA (Sem Energia)"), false);
                }
                return;
            }
            if (entity.tickCount % 20 == 0) {
                stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 1));
            }
            boolean bl = isSelected = slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND;
            if (isSelected && entity.tickCount % 5 == 0) {
                level.sendParticles((ParticleOptions)ParticleTypes.ELECTRIC_SPARK, entity.getX(), entity.getY() + 1.0, entity.getZ(), 1, 0.1, 0.4, 0.1, 0.0);
            }
        }
    }

    public boolean isFoil(ItemStack stack) {
        return PlasmaSwordItem.isActive(stack);
    }
}

