/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.advancements.AdvancementHolder
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.Identifier
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 */
package net.exerion.progression;

import net.exerion.progression.ModAttachments;
import net.exerion.progression.TechnologyEra;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerEraManager {
    public static TechnologyEra getPlayerEra(Player player) {
        if (player.isCreative() || player.isSpectator()) {
            return TechnologyEra.COSMIC;
        }
        if (player.hasData(ModAttachments.PLAYER_ERA)) {
            return (TechnologyEra)((Object)player.getData(ModAttachments.PLAYER_ERA));
        }
        return TechnologyEra.STONE;
    }

    public static void setPlayerEra(ServerPlayer player, TechnologyEra era) {
        player.setData(ModAttachments.PLAYER_ERA, (Object)era);
        MutableComponent message = Component.translatable((String)"message.exerion.era_unlocked", (Object[])new Object[]{era.getDisplayNameComponent()}).withStyle(ChatFormatting.GREEN);
        player.displayClientMessage((Component)message, false);
        String eraName = era.name().toLowerCase();
        Identifier advId = Identifier.fromNamespaceAndPath((String)"exerion", (String)("era/" + eraName));
        AdvancementHolder adv = player.level().getServer().getAdvancements().get(advId);
        if (adv != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
            for (String criterion : progress.getRemainingCriteria()) {
                player.getAdvancements().award(adv, criterion);
            }
        }
    }
}

