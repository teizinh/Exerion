/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.registries.RegisterEvent
 */
package net.exerion.item;

import net.exerion.block.ModBlocks;
import net.exerion.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModCreativeModeTabs {
    public static final ResourceKey<CreativeModeTab> EXERION_GROUP_KEY = ResourceKey.create((ResourceKey)Registries.CREATIVE_MODE_TAB, (Identifier)Identifier.fromNamespaceAndPath((String)"exerion", (String)"main"));
    public static CreativeModeTab EXERION_GROUP;

    public static void registerCreativeModeTabs(RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            EXERION_GROUP = CreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)ModItems.EXERION_CORE)).title((Component)Component.translatable((String)"itemGroup.exerion.main")).displayItems((params, output) -> {
                output.accept((ItemLike)ModItems.EXERION_GUIDE_BOOK);
                output.accept((ItemLike)ModItems.WRENCH);
                output.accept((ItemLike)ModItems.ERA_SCANNER);
                output.accept((ItemLike)ModItems.ELECTRIC_MINING_DRILL);
                output.accept((ItemLike)ModItems.PLASMA_SWORD);
                output.accept((ItemLike)ModItems.STEAM_BOOTS);
                output.accept((ItemLike)ModItems.LOGIC_HELMET);
                output.accept((ItemLike)ModBlocks.EXERION_ORE);
                output.accept((ItemLike)ModBlocks.DEEPSLATE_EXERION_ORE);
                output.accept((ItemLike)ModItems.RAW_EXERION);
                output.accept((ItemLike)ModBlocks.RAW_EXERION_BLOCK);
                output.accept((ItemLike)ModItems.EXERION_INGOT);
                output.accept((ItemLike)ModBlocks.EXERION_BLOCK);
                output.accept((ItemLike)ModItems.STEEL_INGOT);
                output.accept((ItemLike)ModBlocks.STEEL_BLOCK);
                output.accept((ItemLike)ModItems.STEAM_CRYSTAL);
                output.accept((ItemLike)ModItems.EXERION_NUGGET);
                output.accept((ItemLike)ModItems.COAL_DUST);
                output.accept((ItemLike)ModItems.IRON_DUST);
                output.accept((ItemLike)ModItems.COPPER_DUST);
                output.accept((ItemLike)ModItems.STEEL_COMPOUND);
                output.accept((ItemLike)ModItems.EXERION_PLATE);
                output.accept((ItemLike)ModItems.CARBON_PLATE);
                output.accept((ItemLike)ModItems.ELECTRIC_PLATE);
                output.accept((ItemLike)ModItems.EXERION_ROD);
                output.accept((ItemLike)ModItems.COPPER_GEAR);
                output.accept((ItemLike)ModItems.IRON_GEAR);
                output.accept((ItemLike)ModItems.EXERION_GEAR);
                output.accept((ItemLike)ModItems.CIRCUIT_BOARD);
                output.accept((ItemLike)ModItems.LOGIC_CHIP);
                output.accept((ItemLike)ModItems.EXERION_CORE);
                output.accept((ItemLike)ModItems.GEAR_BLUEPRINT);
                output.accept((ItemLike)ModItems.SPEED_UPGRADE);
                output.accept((ItemLike)ModItems.EFFICIENCY_UPGRADE);
                output.accept((ItemLike)ModBlocks.EXERION_CASING);
                output.accept((ItemLike)ModBlocks.STEEL_CASING);
                output.accept((ItemLike)ModBlocks.EXERION_FRAME);
                output.accept((ItemLike)ModBlocks.COPPER_CABLE);
                output.accept((ItemLike)ModBlocks.STEEL_CABLE);
                output.accept((ItemLike)ModBlocks.EXERION_CABLE);
                output.accept((ItemLike)ModBlocks.SHAFT);
                output.accept((ItemLike)ModBlocks.HAND_CRANK);
                output.accept((ItemLike)ModBlocks.WATER_WHEEL);
                output.accept((ItemLike)ModBlocks.MECHANICAL_CHUTE);
                output.accept((ItemLike)ModBlocks.COAL_BOILER);
                output.accept((ItemLike)ModBlocks.GEAR_PRESS);
                output.accept((ItemLike)ModBlocks.CRUSHER);
                output.accept((ItemLike)ModBlocks.ELECTRIC_GENERATOR);
                output.accept((ItemLike)ModBlocks.STEAM_TURBINE);
                output.accept((ItemLike)ModBlocks.GEOTHERMAL_GENERATOR);
                output.accept((ItemLike)ModBlocks.BATBOX);
                output.accept((ItemLike)ModBlocks.ALLOY_SMELTER);
                output.accept((ItemLike)ModBlocks.PUMP);
                output.accept((ItemLike)ModBlocks.ELECTRIC_CRUSHER);
                output.accept((ItemLike)ModBlocks.ELECTRIC_FURNACE);
                output.accept((ItemLike)ModBlocks.COMPRESSOR);
                output.accept((ItemLike)ModBlocks.COPPER_SOLAR_PANEL);
                output.accept((ItemLike)ModBlocks.STEEL_SOLAR_PANEL);
                output.accept((ItemLike)ModBlocks.EXERION_SOLAR_PANEL);
                output.accept((ItemLike)ModBlocks.MINING_LASER);
                output.accept((ItemLike)ModBlocks.FREQUENCY_ENDER_TANK);
                output.accept((ItemLike)ModBlocks.FREQUENCY_ENDER_CHEST);
                output.accept((ItemLike)ModBlocks.PROGRESSION_TERMINAL);
                output.accept((ItemLike)ModBlocks.DIGITAL_STORAGE_TERMINAL);
            }).build();
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"main"), (Object)EXERION_GROUP);
        });
    }
}

