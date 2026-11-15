/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.event.RegisterCommandsEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.neoforged.neoforge.event.tick.ServerTickEvent$Pre
 *  net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
 *  net.neoforged.neoforge.registries.RegisterEvent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.exerion;

import com.mojang.brigadier.CommandDispatcher;
import net.exerion.ExerionClient;
import net.exerion.block.ModBlocks;
import net.exerion.block.entity.CoalBoilerBlockEntity;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.command.ExerionCommand;
import net.exerion.energy.IEnergyStorage;
import net.exerion.item.ModCreativeModeTabs;
import net.exerion.item.ModItems;
import net.exerion.menu.ModMenuTypes;
import net.exerion.network.ExerionNetwork;
import net.exerion.progression.EraProgressionRegistry;
import net.exerion.progression.ModAttachments;
import net.exerion.progression.PlayerEraManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.exerion.util.ExerionCrashGuard;
import net.exerion.fluid.GlobalFluidNetwork;
import net.exerion.block.entity.FrequencyEnderChestBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value="exerion")
public class Exerion {
    public static final String MOD_ID = "exerion";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"exerion");

    public Exerion(IEventBus modEventBus) {
        LOGGER.info("Initializing Exerion Mod for NeoForge (v1.0.5 safe registry)...");
        modEventBus.addListener(RegisterEvent.class, this::onRegister);
        modEventBus.addListener(RegisterPayloadHandlersEvent.class, ExerionNetwork::registerPayloads);
        ModAttachments.registerAttachments(modEventBus);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);
        NeoForge.EVENT_BUS.addListener(this::onServerTick);
        NeoForge.EVENT_BUS.addListener(this::onServerStopping);
        try {
            ExerionClient.init(modEventBus);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        LOGGER.info("Exerion Mod initialized successfully!");
    }

    private void onRegister(RegisterEvent event) {
        ModBlocks.registerBlocks(event);
        ModBlocks.registerBlockItems(event);
        ModItems.registerItems(event);
        ModBlockEntityTypes.registerBlockEntityTypes(event);
        ModMenuTypes.registerMenuTypes(event);
        ModCreativeModeTabs.registerCreativeModeTabs(event);
        if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
            EraProgressionRegistry.init();
        }
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ExerionCommand.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
    }

    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            ExerionNetwork.syncEraToClient(serverPlayer, PlayerEraManager.getPlayerEra((Player)serverPlayer));
        }
    }

    private void onServerStopping(ServerStoppingEvent event) {
        GlobalFluidNetwork.clear();
        FrequencyEnderChestBlockEntity.EnderStorageManager.clear();
        ExerionCrashGuard.clearAll();
    }

    private void onServerTick(ServerTickEvent.Pre event) {
        MinecraftServer server = event.getServer();
        if (server == null) {
            return;
        }
        ExerionCrashGuard.safeNetwork("ServerTickEvent.Pre", () -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            int fallBlocks;
            ItemStack boots;
            ItemStack plate;
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!helmet.isEmpty() && helmet.is(ModItems.LOGIC_HELMET)) {
                if (!helmet.has(DataComponents.CUSTOM_DATA) || !((CustomData)helmet.get(DataComponents.CUSTOM_DATA)).copyTag().contains("exerion_init")) {
                    helmet.setDamageValue(helmet.getMaxDamage());
                    CompoundTag tag = helmet.has(DataComponents.CUSTOM_DATA) ? ((CustomData)helmet.get(DataComponents.CUSTOM_DATA)).copyTag() : new CompoundTag();
                    tag.putBoolean("exerion_init", true);
                    helmet.set(DataComponents.CUSTOM_DATA, CustomData.of((CompoundTag)tag));
                }
                if (helmet.getDamageValue() < helmet.getMaxDamage()) {
                    BlockHitResult hit;
                    if (player.tickCount % 80 == 0) {
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
                        helmet.setDamageValue(Math.min(helmet.getMaxDamage(), helmet.getDamageValue() + 1));
                    }
                    if (player.tickCount % 4 == 0 && (hit = (BlockHitResult)player.pick(6.0, 1.0f, false)).getType() == HitResult.Type.BLOCK) {
                        BlockPos targetPos = hit.getBlockPos();
                        BlockEntity be = player.level().getBlockEntity(targetPos);
                        if (be != null) {
                            String msg = null;
                            if (be instanceof IEnergyStorage) {
                                IEnergyStorage energyStorage = (IEnergyStorage)be;
                                msg = "\u00a7b[HUD] " + be.getBlockState().getBlock().getName().getString() + " \u00a77| \u00a7eEnergia: \u00a7f" + energyStorage.getEnergy() + " / " + energyStorage.getMaxEnergy() + " EU";
                            } else if (be instanceof CoalBoilerBlockEntity) {
                                CoalBoilerBlockEntity boiler = (CoalBoilerBlockEntity)be;
                                boolean isBurning = boiler.getBurnTime() > 0;
                                msg = "\u00a7b[HUD] Caldeira a Carv\u00e3o \u00a77| \u00a7eCombust\u00e3o: " + (isBurning ? "\u00a7aA Queimar" : "\u00a7cDesligada");
                            }
                            if (msg != null) {
                                player.sendSystemMessage((Component)Component.literal(msg), true);
                            }
                        }
                    }
                }
            }
            if (!(plate = player.getItemBySlot(EquipmentSlot.CHEST)).isEmpty() && plate.is(ModItems.ELECTRIC_PLATE)) {
                if (!plate.has(DataComponents.CUSTOM_DATA) || !((CustomData)plate.get(DataComponents.CUSTOM_DATA)).copyTag().contains("exerion_init")) {
                    plate.setDamageValue(plate.getMaxDamage());
                    CompoundTag tag = plate.has(DataComponents.CUSTOM_DATA) ? ((CustomData)plate.get(DataComponents.CUSTOM_DATA)).copyTag() : new CompoundTag();
                    tag.putBoolean("exerion_init", true);
                    plate.set(DataComponents.CUSTOM_DATA, CustomData.of((CompoundTag)tag));
                }
                if (plate.getDamageValue() < plate.getMaxDamage()) {
                    if (player.hurtTime == 10) {
                        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 80, 1, false, false, true));
                        plate.setDamageValue(Math.min(plate.getMaxDamage(), plate.getDamageValue() + 50));
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), (SoundEvent)SoundEvents.SHIELD_BLOCK.value(), SoundSource.PLAYERS, 0.6f, 1.2f);
                    }
                    if (player.tickCount % 2 == 0) {
                        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                            ItemStack invStack = player.getInventory().getItem(i);
                            if (invStack.isEmpty() || invStack == plate || !invStack.is(ModItems.ERA_SCANNER) && !invStack.is(ModItems.ELECTRIC_MINING_DRILL) || invStack.getDamageValue() <= 0) continue;
                            invStack.setDamageValue(invStack.getDamageValue() - 1);
                            plate.setDamageValue(Math.min(plate.getMaxDamage(), plate.getDamageValue() + 1));
                            break;
                        }
                    }
                }
            }
            if ((boots = player.getItemBySlot(EquipmentSlot.FEET)).isEmpty() || !boots.is(ModItems.STEAM_BOOTS)) continue;
            if (!boots.has(DataComponents.CUSTOM_DATA) || !((CustomData)boots.get(DataComponents.CUSTOM_DATA)).copyTag().contains("exerion_init")) {
                boots.setDamageValue(boots.getMaxDamage());
                CompoundTag tag = boots.has(DataComponents.CUSTOM_DATA) ? ((CustomData)boots.get(DataComponents.CUSTOM_DATA)).copyTag() : new CompoundTag();
                tag.putBoolean("exerion_init", true);
                boots.set(DataComponents.CUSTOM_DATA, CustomData.of((CompoundTag)tag));
            }
            if (boots.getDamageValue() >= boots.getMaxDamage() || !(player.fallDistance > 0.0) || (fallBlocks = (int)player.fallDistance) <= 0) continue;
            player.fallDistance = 0.0;
            boots.setDamageValue(Math.min(boots.getMaxDamage(), boots.getDamageValue() + fallBlocks * 3));
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 1.5f);
        }
        });
    }
}

