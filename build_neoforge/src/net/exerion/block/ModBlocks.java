package net.exerion.block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.exerion.Exerion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModBlocks {
    public static Block EXERION_CASING;
    public static Block EXERION_BLOCK;
    public static Block EXERION_ORE;
    public static Block DEEPSLATE_EXERION_ORE;
    public static Block RAW_EXERION_BLOCK;
    public static Block EXERION_FRAME;
    public static Block GEAR_PRESS;
    public static Block COMPRESSOR;
    public static Block MINING_LASER;
    public static Block GEOTHERMAL_GENERATOR;
    public static Block ALLOY_SMELTER;
    public static Block ELECTRIC_FURNACE;
    public static Block FREQUENCY_ENDER_CHEST;
    public static Block FREQUENCY_ENDER_TANK;
    public static Block BATBOX;
    public static Block COAL_BOILER;
    public static Block STEAM_TURBINE;
    public static Block CRUSHER;
    public static Block PROGRESSION_TERMINAL;
    public static Block DIGITAL_STORAGE_TERMINAL;
    public static Block STEEL_BLOCK;
    public static Block STEEL_CASING;
    public static Block ELECTRIC_GENERATOR;
    public static Block COPPER_CABLE;
    public static Block STEEL_CABLE;
    public static Block EXERION_CABLE;
    public static Block ELECTRIC_CRUSHER;
    public static Block COPPER_SOLAR_PANEL;
    public static Block STEEL_SOLAR_PANEL;
    public static Block EXERION_SOLAR_PANEL;
    public static Block SHAFT;
    public static Block HAND_CRANK;
    public static Block WATER_WHEEL;
    public static Block MECHANICAL_CHUTE;
    public static Block PUMP;

    public static final List<BlockEntry> BLOCK_ENTRIES = new ArrayList<>();

    private static Block register(RegisterEvent.RegisterHelper<Block> helper, String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("exerion", name));
        Block block = factory.apply(properties.setId(blockKey));
        helper.register(Identifier.fromNamespaceAndPath("exerion", name), block);
        BLOCK_ENTRIES.add(new BlockEntry(name, block));
        return block;
    }

    public static void registerBlocks(RegisterEvent event) {
        event.register(Registries.BLOCK, helper -> {
            BLOCK_ENTRIES.clear();
            EXERION_CASING = register(helper, "exerion_casing", Block::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL));
            EXERION_BLOCK = register(helper, "exerion_block", Block::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL));
            EXERION_ORE = register(helper, "exerion_ore", Block::new, BlockBehaviour.Properties.of().strength(3.0f, 3.0f).requiresCorrectToolForDrops().sound(SoundType.STONE).mapColor(MapColor.STONE));
            DEEPSLATE_EXERION_ORE = register(helper, "deepslate_exerion_ore", Block::new, BlockBehaviour.Properties.of().strength(4.5f, 3.0f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).mapColor(MapColor.DEEPSLATE));
            RAW_EXERION_BLOCK = register(helper, "raw_exerion_block", Block::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.RAW_IRON));
            EXERION_FRAME = register(helper, "exerion_frame", Block::new, BlockBehaviour.Properties.of().strength(3.0f, 3.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            GEAR_PRESS = register(helper, "gear_press", GearPressBlock::new, BlockBehaviour.Properties.of().strength(4.0f, 5.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            COMPRESSOR = register(helper, "compressor", CompressorBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            MINING_LASER = register(helper, "mining_laser", MiningLaserBlock::new, BlockBehaviour.Properties.of().strength(6.0f, 8.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            GEOTHERMAL_GENERATOR = register(helper, "geothermal_generator", GeothermalGeneratorBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            ALLOY_SMELTER = register(helper, "alloy_smelter", AlloySmelterBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            ELECTRIC_FURNACE = register(helper, "electric_furnace", ElectricFurnaceBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            FREQUENCY_ENDER_CHEST = register(helper, "frequency_ender_chest", FrequencyEnderChestBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.STONE).mapColor(MapColor.COLOR_BLACK).noOcclusion());
            FREQUENCY_ENDER_TANK = register(helper, "frequency_ender_tank", FrequencyEnderTankBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.GLASS).mapColor(MapColor.COLOR_BLACK).noOcclusion());
            BATBOX = register(helper, "batbox", BatBoxBlock::new, BlockBehaviour.Properties.of().strength(4.0f, 5.0f).requiresCorrectToolForDrops().sound(SoundType.WOOD).mapColor(MapColor.WOOD).noOcclusion());
            COAL_BOILER = register(helper, "coal_boiler", CoalBoilerBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            STEAM_TURBINE = register(helper, "steam_turbine", SteamTurbineBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            CRUSHER = register(helper, "crusher", CrusherBlock::new, BlockBehaviour.Properties.of().strength(4.0f, 5.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            PROGRESSION_TERMINAL = register(helper, "progression_terminal", ProgressionTerminalBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.COLOR_PURPLE).noOcclusion());
            DIGITAL_STORAGE_TERMINAL = register(helper, "digital_storage_terminal", DigitalStorageTerminalBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.COLOR_PURPLE).noOcclusion());
            STEEL_BLOCK = register(helper, "steel_block", Block::new, BlockBehaviour.Properties.of().strength(6.0f, 8.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL));
            STEEL_CASING = register(helper, "steel_casing", Block::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL));
            ELECTRIC_GENERATOR = register(helper, "electric_generator", ElectricGeneratorBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            COPPER_CABLE = register(helper, "copper_cable", properties -> new CableBlock(properties, 100, 500), BlockBehaviour.Properties.of().strength(1.0f, 1.0f).sound(SoundType.WOOL).mapColor(MapColor.COLOR_ORANGE).noOcclusion());
            STEEL_CABLE = register(helper, "steel_cable", properties -> new CableBlock(properties, 500, 2500), BlockBehaviour.Properties.of().strength(1.5f, 1.5f).sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            EXERION_CABLE = register(helper, "exerion_cable", properties -> new CableBlock(properties, 2000, 10000), BlockBehaviour.Properties.of().strength(2.0f, 2.0f).sound(SoundType.METAL).mapColor(MapColor.COLOR_PURPLE).noOcclusion());
            ELECTRIC_CRUSHER = register(helper, "electric_crusher", ElectricCrusherBlock::new, BlockBehaviour.Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            COPPER_SOLAR_PANEL = register(helper, "copper_solar_panel", properties -> new SolarPanelBlock(properties, 100, 1000, 2), BlockBehaviour.Properties.of().strength(3.0f, 3.0f).sound(SoundType.METAL).mapColor(MapColor.COLOR_ORANGE).noOcclusion());
            STEEL_SOLAR_PANEL = register(helper, "steel_solar_panel", properties -> new SolarPanelBlock(properties, 500, 5000, 10), BlockBehaviour.Properties.of().strength(4.0f, 4.0f).sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            EXERION_SOLAR_PANEL = register(helper, "exerion_solar_panel", properties -> new SolarPanelBlock(properties, 2000, 20000, 40), BlockBehaviour.Properties.of().strength(5.0f, 5.0f).sound(SoundType.METAL).mapColor(MapColor.COLOR_PURPLE).noOcclusion());
            SHAFT = register(helper, "shaft", ShaftBlock::new, BlockBehaviour.Properties.of().strength(1.5f, 2.0f).sound(SoundType.WOOD).mapColor(MapColor.WOOD).noOcclusion());
            HAND_CRANK = register(helper, "hand_crank", HandCrankBlock::new, BlockBehaviour.Properties.of().strength(2.0f, 2.0f).sound(SoundType.WOOD).mapColor(MapColor.WOOD).noOcclusion());
            WATER_WHEEL = register(helper, "water_wheel", WaterWheelBlock::new, BlockBehaviour.Properties.of().strength(3.0f, 3.0f).sound(SoundType.WOOD).mapColor(MapColor.WOOD).noOcclusion());
            MECHANICAL_CHUTE = register(helper, "mechanical_chute", MechanicalChuteBlock::new, BlockBehaviour.Properties.of().strength(3.0f, 3.0f).sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
            PUMP = register(helper, "pump", PumpBlock::new, BlockBehaviour.Properties.of().strength(3.0f, 3.0f).sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());
        });
    }

    public static void registerBlockItems(RegisterEvent event) {
        event.register(Registries.ITEM, helper -> {
            for (BlockEntry entry : BLOCK_ENTRIES) {
                ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("exerion", entry.name()));
                helper.register(Identifier.fromNamespaceAndPath("exerion", entry.name()), new BlockItem(entry.block(), new Item.Properties().setId(itemKey)));
            }
        });
    }

    public static void registerModBlocks() {
        Exerion.LOGGER.info("Registering Mod Blocks for exerion");
    }

    public record BlockEntry(String name, Block block) {
    }
}
