/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.Level
 */
package net.exerion.item;

import net.exerion.item.ClientOnly;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ExerionGuideBookItem
extends Item {
    public ExerionGuideBookItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            ClientOnly.openBook();
        }
        return InteractionResult.SUCCESS;
    }
}

