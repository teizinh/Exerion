package net.exerion.item;

import net.exerion.block.ModBlocks;
import net.exerion.item.ModItems;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ModCreativeModeTabs {
    public static final ResourceKey<CreativeModeTab> EXERION_GROUP_KEY = ResourceKey.create((ResourceKey)Registries.CREATIVE_MODE_TAB, (Identifier)Identifier.fromNamespaceAndPath((String)"exerion", (String)"main"));
    public static final CreativeModeTab EXERION_GROUP = FabricCreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)ModItems.EXERION_CORE)).title((Component)Component.translatable((String)"itemGroup.exerion.main")).displayItems((params, output) -> {
        output.accept((ItemLike)ModItems.EXERION_GUIDE_BOOK);
        output.accept((ItemLike)ModItems.WRENCH);
        output.accept((ItemLike)ModItems.SWISS_ARMY_KNIFE);
        output.accept((ItemLike)ModItems.ERA_SCANNER);
        output.accept((ItemLike)ModItems.ELECTRIC_MINING_DRILL);
        output.accept((ItemLike)ModItems.PLASMA_SWORD);
        output.accept((ItemLike)ModItems.STEAM_BOOTS);
        output.accept((ItemLike)ModItems.LOGIC_HELMET);
        output.accept(ModBlocks.EXERION_ORE);
        output.accept(ModBlocks.DEEPSLATE_EXERION_ORE);
        output.accept((ItemLike)ModItems.RAW_EXERION);
        output.accept(ModBlocks.RAW_EXERION_BLOCK);
        output.accept((ItemLike)ModItems.EXERION_INGOT);
        output.accept(ModBlocks.EXERION_BLOCK);
        output.accept((ItemLike)ModItems.STEEL_INGOT);
        output.accept(ModBlocks.STEEL_BLOCK);
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
        output.accept(ModBlocks.EXERION_CASING);
        output.accept(ModBlocks.STEEL_CASING);
        output.accept(ModBlocks.EXERION_FRAME);
        output.accept(ModBlocks.COPPER_CABLE);
        output.accept(ModBlocks.STEEL_CABLE);
        output.accept(ModBlocks.EXERION_CABLE);
        output.accept(ModBlocks.SHAFT);
        output.accept(ModBlocks.HAND_CRANK);
        output.accept(ModBlocks.WATER_WHEEL);
        output.accept(ModBlocks.MECHANICAL_CHUTE);
        output.accept(ModBlocks.COAL_BOILER);
        output.accept(ModBlocks.GEAR_PRESS);
        output.accept(ModBlocks.CRUSHER);
        output.accept(ModBlocks.ELECTRIC_GENERATOR);
        output.accept(ModBlocks.STEAM_TURBINE);
        output.accept(ModBlocks.GEOTHERMAL_GENERATOR);
        output.accept(ModBlocks.BATBOX);
        output.accept(ModBlocks.ALLOY_SMELTER);
        output.accept(ModBlocks.PUMP);
        output.accept(ModBlocks.ELECTRIC_CRUSHER);
        output.accept(ModBlocks.ELECTRIC_FURNACE);
        output.accept(ModBlocks.COMPRESSOR);
        output.accept(ModBlocks.COPPER_SOLAR_PANEL);
        output.accept(ModBlocks.STEEL_SOLAR_PANEL);
        output.accept(ModBlocks.EXERION_SOLAR_PANEL);
        output.accept(ModBlocks.MINING_LASER);
        output.accept(ModBlocks.FREQUENCY_ENDER_TANK);
        output.accept(ModBlocks.FREQUENCY_ENDER_CHEST);
        output.accept(ModBlocks.PROGRESSION_TERMINAL);
        output.accept(ModBlocks.DIGITAL_STORAGE_TERMINAL);
    }).build();

    public static void registerCreativeModeTabs() {
        Registry.register((Registry)BuiltInRegistries.CREATIVE_MODE_TAB, EXERION_GROUP_KEY, EXERION_GROUP);
    }
}

