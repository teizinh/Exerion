/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.flag.FeatureFlags
 *  net.minecraft.world.inventory.MenuType
 *  net.neoforged.neoforge.registries.RegisterEvent
 */
package net.exerion.menu;

import net.exerion.menu.AlloySmelterMenu;
import net.exerion.menu.BatBoxMenu;
import net.exerion.menu.CoalBoilerMenu;
import net.exerion.menu.CompressorMenu;
import net.exerion.menu.CrusherMenu;
import net.exerion.menu.DigitalStorageTerminalMenu;
import net.exerion.menu.ElectricCrusherMenu;
import net.exerion.menu.ElectricFurnaceMenu;
import net.exerion.menu.ElectricGeneratorMenu;
import net.exerion.menu.FrequencyEnderChestMenu;
import net.exerion.menu.GearPressMenu;
import net.exerion.menu.GeothermalGeneratorMenu;
import net.exerion.menu.MiningLaserMenu;
import net.exerion.menu.ProgressionTerminalMenu;
import net.exerion.menu.SolarPanelMenu;
import net.exerion.menu.SteamTurbineMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModMenuTypes {
    public static MenuType<CoalBoilerMenu> COAL_BOILER_MENU;
    public static MenuType<ProgressionTerminalMenu> PROGRESSION_TERMINAL_MENU;
    public static MenuType<SteamTurbineMenu> STEAM_TURBINE_MENU;
    public static MenuType<CrusherMenu> CRUSHER_MENU;
    public static MenuType<GearPressMenu> GEAR_PRESS_MENU;
    public static MenuType<ElectricGeneratorMenu> ELECTRIC_GENERATOR_MENU;
    public static MenuType<ElectricCrusherMenu> ELECTRIC_CRUSHER_MENU;
    public static MenuType<SolarPanelMenu> SOLAR_PANEL_MENU;
    public static MenuType<DigitalStorageTerminalMenu> DIGITAL_STORAGE_TERMINAL_MENU;
    public static MenuType<BatBoxMenu> BATBOX_MENU;
    public static MenuType<CompressorMenu> COMPRESSOR_MENU;
    public static MenuType<AlloySmelterMenu> ALLOY_SMELTER_MENU;
    public static MenuType<FrequencyEnderChestMenu> FREQUENCY_ENDER_CHEST_MENU;
    public static MenuType<MiningLaserMenu> MINING_LASER_MENU;
    public static MenuType<GeothermalGeneratorMenu> GEOTHERMAL_GENERATOR_MENU;
    public static MenuType<ElectricFurnaceMenu> ELECTRIC_FURNACE_MENU;

    public static void registerMenuTypes(RegisterEvent event) {
        event.register(Registries.MENU, helper -> {
            COAL_BOILER_MENU = new MenuType(CoalBoilerMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"coal_boiler"), COAL_BOILER_MENU);
            PROGRESSION_TERMINAL_MENU = new MenuType(ProgressionTerminalMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"progression_terminal"), PROGRESSION_TERMINAL_MENU);
            STEAM_TURBINE_MENU = new MenuType(SteamTurbineMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"steam_turbine"), STEAM_TURBINE_MENU);
            CRUSHER_MENU = new MenuType(CrusherMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"crusher"), CRUSHER_MENU);
            GEAR_PRESS_MENU = new MenuType(GearPressMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"gear_press"), GEAR_PRESS_MENU);
            ELECTRIC_GENERATOR_MENU = new MenuType(ElectricGeneratorMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"electric_generator"), ELECTRIC_GENERATOR_MENU);
            ELECTRIC_CRUSHER_MENU = new MenuType(ElectricCrusherMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"electric_crusher"), ELECTRIC_CRUSHER_MENU);
            SOLAR_PANEL_MENU = new MenuType(SolarPanelMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"solar_panel"), SOLAR_PANEL_MENU);
            DIGITAL_STORAGE_TERMINAL_MENU = new MenuType(DigitalStorageTerminalMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"digital_storage_terminal"), DIGITAL_STORAGE_TERMINAL_MENU);
            BATBOX_MENU = new MenuType(BatBoxMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"batbox"), BATBOX_MENU);
            COMPRESSOR_MENU = new MenuType(CompressorMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"compressor"), COMPRESSOR_MENU);
            ALLOY_SMELTER_MENU = new MenuType(AlloySmelterMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"alloy_smelter"), ALLOY_SMELTER_MENU);
            FREQUENCY_ENDER_CHEST_MENU = new MenuType(FrequencyEnderChestMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"frequency_ender_chest"), FREQUENCY_ENDER_CHEST_MENU);
            MINING_LASER_MENU = new MenuType(MiningLaserMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"mining_laser"), MINING_LASER_MENU);
            GEOTHERMAL_GENERATOR_MENU = new MenuType(GeothermalGeneratorMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"geothermal_generator"), GEOTHERMAL_GENERATOR_MENU);
            ELECTRIC_FURNACE_MENU = new MenuType(ElectricFurnaceMenu::new, FeatureFlags.DEFAULT_FLAGS);
            helper.register(Identifier.fromNamespaceAndPath((String)"exerion", (String)"electric_furnace"), ELECTRIC_FURNACE_MENU);
        });
    }
}

