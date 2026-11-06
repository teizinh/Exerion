package net.exerion.item;

import net.exerion.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ElectricPlateItem
extends Item {
    public ElectricPlateItem(Item.Properties properties) {
        super(properties);
    }

    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (slot == EquipmentSlot.CHEST) {
                if (player.hurtTime == 10 && stack.getDamageValue() < stack.getMaxDamage()) {
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 80, 1, false, false, true));
                    stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 50));
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), (SoundEvent)SoundEvents.SHIELD_BLOCK.value(), SoundSource.PLAYERS, 0.6f, 1.2f);
                }
                if (player.tickCount % 2 == 0 && stack.getDamageValue() < stack.getMaxDamage()) {
                    for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                        ItemStack invStack = player.getInventory().getItem(i);
                        if (invStack.isEmpty() || invStack == stack || !invStack.is(ModItems.ERA_SCANNER) && !invStack.is(ModItems.SWISS_ARMY_KNIFE) && !invStack.is(ModItems.ELECTRIC_MINING_DRILL) && !invStack.is(ModItems.PLASMA_SWORD) || invStack.getDamageValue() <= 0) continue;
                        invStack.setDamageValue(invStack.getDamageValue() - 1);
                        stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 1));
                        break;
                    }
                }
            }
        }
    }
}

