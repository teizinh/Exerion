/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.neoforge.registries.RegisterEvent
 */
package net.exerion.block.entity;

import net.exerion.block.ModBlocks;
import net.exerion.block.entity.AlloySmelterBlockEntity;
import net.exerion.block.entity.BatBoxBlockEntity;
import net.exerion.block.entity.CableBlockEntity;
import net.exerion.block.entity.CoalBoilerBlockEntity;
import net.exerion.block.entity.CompressorBlockEntity;
import net.exerion.block.entity.CrusherBlockEntity;
import net.exerion.block.entity.DigitalStorageTerminalBlockEntity;
import net.exerion.block.entity.ElectricCrusherBlockEntity;
import net.exerion.block.entity.ElectricFurnaceBlockEntity;
import net.exerion.block.entity.ElectricGeneratorBlockEntity;
import net.exerion.block.entity.FrequencyEnderChestBlockEntity;
import net.exerion.block.entity.FrequencyEnderTankBlockEntity;
import net.exerion.block.entity.GearPressBlockEntity;
import net.exerion.block.entity.GeothermalGeneratorBlockEntity;
import net.exerion.block.entity.HandCrankBlockEntity;
import net.exerion.block.entity.MechanicalChuteBlockEntity;
import net.exerion.block.entity.MiningLaserBlockEntity;
import net.exerion.block.entity.ProgressionTerminalBlockEntity;
import net.exerion.block.entity.PumpBlockEntity;
import net.exerion.block.entity.ShaftBlockEntity;
import net.exerion.block.entity.SolarPanelBlockEntity;
import net.exerion.block.entity.SteamTurbineBlockEntity;
import net.exerion.block.entity.WaterWheelBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModBlockEntityTypes {
    public static BlockEntityType<CoalBoilerBlockEntity> COAL_BOILER_ENTITY;
    public static BlockEntityType<ProgressionTerminalBlockEntity> PROGRESSION_TERMINAL_ENTITY;
    public static BlockEntityType<SteamTurbineBlockEntity> STEAM_TURBINE_ENTITY;
    public static BlockEntityType<CrusherBlockEntity> CRUSHER_ENTITY;
    public static BlockEntityType<GearPressBlockEntity> GEAR_PRESS_ENTITY;
    public static BlockEntityType<ElectricGeneratorBlockEntity> ELECTRIC_GENERATOR_ENTITY;
    public static BlockEntityType<CableBlockEntity> CABLE_ENTITY;
    public static BlockEntityType<ElectricCrusherBlockEntity> ELECTRIC_CRUSHER_ENTITY;
    public static BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY;
    public static BlockEntityType<DigitalStorageTerminalBlockEntity> DIGITAL_STORAGE_TERMINAL_ENTITY;
    public static BlockEntityType<BatBoxBlockEntity> BATBOX_ENTITY;
    public static BlockEntityType<CompressorBlockEntity> COMPRESSOR_ENTITY;
    public static BlockEntityType<AlloySmelterBlockEntity> ALLOY_SMELTER_ENTITY;
    public static BlockEntityType<FrequencyEnderChestBlockEntity> FREQUENCY_ENDER_CHEST_ENTITY;
    public static BlockEntityType<FrequencyEnderTankBlockEntity> FREQUENCY_ENDER_TANK_ENTITY;
    public static BlockEntityType<MiningLaserBlockEntity> MINING_LASER_ENTITY;
    public static BlockEntityType<GeothermalGeneratorBlockEntity> GEOTHERMAL_GENERATOR_ENTITY;
    public static BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE_ENTITY;
    public static BlockEntityType<ShaftBlockEntity> SHAFT_ENTITY;
    public static BlockEntityType<HandCrankBlockEntity> HAND_CRANK_ENTITY;
    public static BlockEntityType<WaterWheelBlockEntity> WATER_WHEEL_ENTITY;
    public static BlockEntityType<MechanicalChuteBlockEntity> MECHANICAL_CHUTE_ENTITY;
    public static BlockEntityType<PumpBlockEntity> PUMP_ENTITY;

    public static void registerBlockEntityTypes(RegisterEvent event) {
        event.register(Registries.BLOCK_ENTITY_TYPE, helper -> {
            COAL_BOILER_ENTITY = new BlockEntityType(CoalBoilerBlockEntity::new, new Block[]{ModBlocks.COAL_BOILER});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"coal_boiler"), COAL_BOILER_ENTITY);
            PROGRESSION_TERMINAL_ENTITY = new BlockEntityType(ProgressionTerminalBlockEntity::new, new Block[]{ModBlocks.PROGRESSION_TERMINAL});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"progression_terminal"), PROGRESSION_TERMINAL_ENTITY);
            STEAM_TURBINE_ENTITY = new BlockEntityType(SteamTurbineBlockEntity::new, new Block[]{ModBlocks.STEAM_TURBINE});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"steam_turbine"), STEAM_TURBINE_ENTITY);
            CRUSHER_ENTITY = new BlockEntityType(CrusherBlockEntity::new, new Block[]{ModBlocks.CRUSHER});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"crusher"), CRUSHER_ENTITY);
            GEAR_PRESS_ENTITY = new BlockEntityType(GearPressBlockEntity::new, new Block[]{ModBlocks.GEAR_PRESS});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"gear_press"), GEAR_PRESS_ENTITY);
            ELECTRIC_GENERATOR_ENTITY = new BlockEntityType(ElectricGeneratorBlockEntity::new, new Block[]{ModBlocks.ELECTRIC_GENERATOR});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"electric_generator"), ELECTRIC_GENERATOR_ENTITY);
            CABLE_ENTITY = new BlockEntityType(CableBlockEntity::new, new Block[]{ModBlocks.COPPER_CABLE, ModBlocks.STEEL_CABLE, ModBlocks.EXERION_CABLE});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"cable"), CABLE_ENTITY);
            ELECTRIC_CRUSHER_ENTITY = new BlockEntityType(ElectricCrusherBlockEntity::new, new Block[]{ModBlocks.ELECTRIC_CRUSHER});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"electric_crusher"), ELECTRIC_CRUSHER_ENTITY);
            SOLAR_PANEL_ENTITY = new BlockEntityType(SolarPanelBlockEntity::new, new Block[]{ModBlocks.COPPER_SOLAR_PANEL, ModBlocks.STEEL_SOLAR_PANEL, ModBlocks.EXERION_SOLAR_PANEL});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"solar_panel"), SOLAR_PANEL_ENTITY);
            DIGITAL_STORAGE_TERMINAL_ENTITY = new BlockEntityType(DigitalStorageTerminalBlockEntity::new, new Block[]{ModBlocks.DIGITAL_STORAGE_TERMINAL});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"digital_storage_terminal"), DIGITAL_STORAGE_TERMINAL_ENTITY);
            BATBOX_ENTITY = new BlockEntityType(BatBoxBlockEntity::new, new Block[]{ModBlocks.BATBOX});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"batbox"), BATBOX_ENTITY);
            COMPRESSOR_ENTITY = new BlockEntityType(CompressorBlockEntity::new, new Block[]{ModBlocks.COMPRESSOR});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"compressor"), COMPRESSOR_ENTITY);
            ALLOY_SMELTER_ENTITY = new BlockEntityType(AlloySmelterBlockEntity::new, new Block[]{ModBlocks.ALLOY_SMELTER});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"alloy_smelter"), ALLOY_SMELTER_ENTITY);
            FREQUENCY_ENDER_CHEST_ENTITY = new BlockEntityType(FrequencyEnderChestBlockEntity::new, new Block[]{ModBlocks.FREQUENCY_ENDER_CHEST});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"frequency_ender_chest"), FREQUENCY_ENDER_CHEST_ENTITY);
            FREQUENCY_ENDER_TANK_ENTITY = new BlockEntityType(FrequencyEnderTankBlockEntity::new, new Block[]{ModBlocks.FREQUENCY_ENDER_TANK});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"frequency_ender_tank"), FREQUENCY_ENDER_TANK_ENTITY);
            MINING_LASER_ENTITY = new BlockEntityType(MiningLaserBlockEntity::new, new Block[]{ModBlocks.MINING_LASER});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"mining_laser"), MINING_LASER_ENTITY);
            GEOTHERMAL_GENERATOR_ENTITY = new BlockEntityType(GeothermalGeneratorBlockEntity::new, new Block[]{ModBlocks.GEOTHERMAL_GENERATOR});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"geothermal_generator"), GEOTHERMAL_GENERATOR_ENTITY);
            ELECTRIC_FURNACE_ENTITY = new BlockEntityType(ElectricFurnaceBlockEntity::new, new Block[]{ModBlocks.ELECTRIC_FURNACE});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"electric_furnace"), ELECTRIC_FURNACE_ENTITY);
            SHAFT_ENTITY = new BlockEntityType(ShaftBlockEntity::new, new Block[]{ModBlocks.SHAFT});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"shaft"), SHAFT_ENTITY);
            HAND_CRANK_ENTITY = new BlockEntityType(HandCrankBlockEntity::new, new Block[]{ModBlocks.HAND_CRANK});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"hand_crank"), HAND_CRANK_ENTITY);
            WATER_WHEEL_ENTITY = new BlockEntityType(WaterWheelBlockEntity::new, new Block[]{ModBlocks.WATER_WHEEL});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"water_wheel"), WATER_WHEEL_ENTITY);
            MECHANICAL_CHUTE_ENTITY = new BlockEntityType(MechanicalChuteBlockEntity::new, new Block[]{ModBlocks.MECHANICAL_CHUTE});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"mechanical_chute"), MECHANICAL_CHUTE_ENTITY);
            PUMP_ENTITY = new BlockEntityType(PumpBlockEntity::new, new Block[]{ModBlocks.PUMP});
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"pump"), PUMP_ENTITY);
        });
    }
}

