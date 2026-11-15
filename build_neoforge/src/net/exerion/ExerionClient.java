/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Pre
 *  net.neoforged.neoforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
 *  net.neoforged.neoforge.common.NeoForge
 */
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
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ExerionClient {
    public static void init(IEventBus modEventBus) {
        Exerion.LOGGER.info("Initializing Exerion Client for NeoForge...");
        modEventBus.addListener(RegisterMenuScreensEvent.class, ExerionClient::onRegisterMenuScreens);
        modEventBus.addListener(EntityRenderersEvent.RegisterRenderers.class, ExerionClient::onRegisterRenderers);
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, ExerionClient::onClientTick);
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.COAL_BOILER_MENU, CoalBoilerScreen::new);
        event.register(ModMenuTypes.PROGRESSION_TERMINAL_MENU, ProgressionTerminalScreen::new);
        event.register(ModMenuTypes.STEAM_TURBINE_MENU, SteamTurbineScreen::new);
        event.register(ModMenuTypes.CRUSHER_MENU, CrusherScreen::new);
        event.register(ModMenuTypes.GEAR_PRESS_MENU, GearPressScreen::new);
        event.register(ModMenuTypes.ELECTRIC_GENERATOR_MENU, ElectricGeneratorScreen::new);
        event.register(ModMenuTypes.ELECTRIC_CRUSHER_MENU, ElectricCrusherScreen::new);
        event.register(ModMenuTypes.SOLAR_PANEL_MENU, SolarPanelScreen::new);
        event.register(ModMenuTypes.DIGITAL_STORAGE_TERMINAL_MENU, DigitalStorageTerminalScreen::new);
        event.register(ModMenuTypes.BATBOX_MENU, BatBoxScreen::new);
        event.register(ModMenuTypes.COMPRESSOR_MENU, CompressorScreen::new);
        event.register(ModMenuTypes.ALLOY_SMELTER_MENU, AlloySmelterScreen::new);
        event.register(ModMenuTypes.FREQUENCY_ENDER_CHEST_MENU, FrequencyEnderChestScreen::new);
        event.register(ModMenuTypes.MINING_LASER_MENU, MiningLaserScreen::new);
        event.register(ModMenuTypes.GEOTHERMAL_GENERATOR_MENU, GeothermalGeneratorScreen::new);
        event.register(ModMenuTypes.ELECTRIC_FURNACE_MENU, ElectricFurnaceScreen::new);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.WATER_WHEEL_ENTITY, WaterWheelRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.HAND_CRANK_ENTITY, HandCrankRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.SHAFT_ENTITY, ShaftRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.PUMP_ENTITY, PumpRenderer::new);
    }

    private static void onClientTick(ClientTickEvent.Pre event) {
        double motionY;
        ItemStack boots;
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && !(boots = client.player.getItemBySlot(EquipmentSlot.FEET)).isEmpty() && boots.is(ModItems.STEAM_BOOTS) && boots.getDamageValue() < boots.getMaxDamage() && (motionY = client.player.getDeltaMovement().y) > 0.4 && motionY < 0.45 && !client.player.onGround() && client.player.tickCount % 5 == 0) {
            client.player.setDeltaMovement(client.player.getDeltaMovement().add(0.0, 0.35, 0.0));
            client.player.level().playSound((Entity)client.player, client.player.getX(), client.player.getY(), client.player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.4f, 2.0f);
            boots.setDamageValue(Math.min(boots.getMaxDamage(), boots.getDamageValue() + 2));
        }
    }
}

