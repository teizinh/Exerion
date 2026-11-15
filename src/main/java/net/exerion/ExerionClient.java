package net.exerion;

import net.exerion.Exerion;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.client.AlloySmelterScreen;
import net.exerion.client.BatBoxScreen;
import net.exerion.client.CoalBoilerScreen;
import net.exerion.client.CompressorScreen;
import net.exerion.client.CrusherScreen;
import net.exerion.client.DigitalStorageTerminalScreen;
import net.exerion.client.ElectricCrusherScreen;
import net.exerion.client.ElectricFurnaceScreen;
import net.exerion.client.ElectricGeneratorScreen;
import net.exerion.client.FrequencyEnderChestScreen;
import net.exerion.client.GearPressScreen;
import net.exerion.client.GeothermalGeneratorScreen;
import net.exerion.client.MiningLaserScreen;
import net.exerion.client.ProgressionTerminalScreen;
import net.exerion.client.SolarPanelScreen;
import net.exerion.client.SteamTurbineScreen;
import net.exerion.client.render.HandCrankRenderer;
import net.exerion.client.render.PumpRenderer;
import net.exerion.client.render.ShaftRenderer;
import net.exerion.client.render.WaterWheelRenderer;
import net.exerion.item.ModItems;
import net.exerion.menu.ModMenuTypes;
import net.exerion.network.ExerionNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ExerionClient
implements ClientModInitializer {
    public void onInitializeClient() {
        Exerion.LOGGER.info("Initializing Exerion Client...");
        MenuScreens.register(ModMenuTypes.COAL_BOILER_MENU, CoalBoilerScreen::new);
        MenuScreens.register(ModMenuTypes.PROGRESSION_TERMINAL_MENU, ProgressionTerminalScreen::new);
        MenuScreens.register(ModMenuTypes.STEAM_TURBINE_MENU, SteamTurbineScreen::new);
        MenuScreens.register(ModMenuTypes.CRUSHER_MENU, CrusherScreen::new);
        MenuScreens.register(ModMenuTypes.GEAR_PRESS_MENU, GearPressScreen::new);
        MenuScreens.register(ModMenuTypes.ELECTRIC_GENERATOR_MENU, ElectricGeneratorScreen::new);
        MenuScreens.register(ModMenuTypes.ELECTRIC_CRUSHER_MENU, ElectricCrusherScreen::new);
        MenuScreens.register(ModMenuTypes.SOLAR_PANEL_MENU, SolarPanelScreen::new);
        MenuScreens.register(ModMenuTypes.DIGITAL_STORAGE_TERMINAL_MENU, DigitalStorageTerminalScreen::new);
        MenuScreens.register(ModMenuTypes.BATBOX_MENU, BatBoxScreen::new);
        MenuScreens.register(ModMenuTypes.COMPRESSOR_MENU, CompressorScreen::new);
        MenuScreens.register(ModMenuTypes.ALLOY_SMELTER_MENU, AlloySmelterScreen::new);
        MenuScreens.register(ModMenuTypes.FREQUENCY_ENDER_CHEST_MENU, FrequencyEnderChestScreen::new);
        MenuScreens.register(ModMenuTypes.MINING_LASER_MENU, MiningLaserScreen::new);
        MenuScreens.register(ModMenuTypes.GEOTHERMAL_GENERATOR_MENU, GeothermalGeneratorScreen::new);
        MenuScreens.register(ModMenuTypes.ELECTRIC_FURNACE_MENU, ElectricFurnaceScreen::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.WATER_WHEEL_ENTITY, WaterWheelRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.HAND_CRANK_ENTITY, HandCrankRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.SHAFT_ENTITY, ShaftRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.PUMP_ENTITY, PumpRenderer::new);
        ExerionNetwork.registerClient();
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            double motionY;
            ItemStack boots;
            if (client.player != null && !(boots = client.player.getItemBySlot(EquipmentSlot.FEET)).isEmpty() && boots.getItem() == ModItems.STEAM_BOOTS && boots.getDamageValue() < boots.getMaxDamage() && (motionY = client.player.getDeltaMovement().y) > 0.4 && motionY < 0.45 && !client.player.onGround() && client.player.tickCount % 5 == 0) {
                client.player.setDeltaMovement(client.player.getDeltaMovement().add(0.0, 0.35, 0.0));
                client.player.level().playSound((Entity)client.player, client.player.getX(), client.player.getY(), client.player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.4f, 2.0f);
                boots.setDamageValue(Math.min(boots.getMaxDamage(), boots.getDamageValue() + 2));
            }
        });
        Exerion.LOGGER.info("Exerion Client initialized!");
    }
}

