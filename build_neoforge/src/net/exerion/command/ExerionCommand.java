/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 */
package net.exerion.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.exerion.network.ExerionNetwork;
import net.exerion.progression.PlayerEraManager;
import net.exerion.progression.TechnologyEra;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ExerionCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)Commands.literal((String)"exerion").then(Commands.literal((String)"era").then(Commands.literal((String)"set").then(Commands.argument((String)"era_name", (ArgumentType)StringArgumentType.word()).executes(context -> {
            String eraName = StringArgumentType.getString((CommandContext)context, (String)"era_name");
            TechnologyEra era = TechnologyEra.fromId(eraName);
            ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
            if (era != null) {
                PlayerEraManager.setPlayerEra(player, era);
                ExerionNetwork.syncEraToClient(player, era);
                ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Era set to " + era.name())), false);
                return 1;
            }
            return 0;
        })))));
    }
}

