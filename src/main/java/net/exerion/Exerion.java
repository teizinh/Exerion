package net.exerion;

import com.mojang.brigadier.CommandDispatcher;
import java.util.function.Predicate;
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
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.exerion.block.entity.FrequencyEnderChestBlockEntity;
import net.exerion.fluid.GlobalFluidNetwork;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Exerion implements ModInitializer {
    public static final String MOD_ID = "exerion";
    public static final Logger LOGGER = LoggerFactory.getLogger("exerion");

    private static final CompoundTag INIT_TAG = new CompoundTag();
    static {
        INIT_TAG.putBoolean("exerion_init", true);
    }

    private static boolean isExerionInitialized(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.matchedBy(INIT_TAG);
    }

    private static void initExerionItem(ItemStack stack) {
        stack.setDamageValue(stack.getMaxDamage());
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean("exerion_init", true));
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Exerion Mod...");
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModBlockEntityTypes.registerBlockEntityTypes();
        ModMenuTypes.registerMenuTypes();
        ModCreativeModeTabs.registerCreativeModeTabs();
        ModAttachments.registerAttachments();
        EraProgressionRegistry.init();
        ExerionNetwork.registerServer();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ExerionCommand.register(dispatcher));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            ExerionNetwork.syncEraToClient(handler.getPlayer(), PlayerEraManager.getPlayerEra(handler.getPlayer()))
        );
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            GlobalFluidNetwork.clear();
            FrequencyEnderChestBlockEntity.EnderStorageManager.clear();
            ExerionCrashGuard.clearAll();
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> ExerionCrashGuard.safeNetwork("ServerTickEvents.START_SERVER_TICK", () -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                if (!helmet.isEmpty() && helmet.is(ModItems.LOGIC_HELMET)) {
                    if (!isExerionInitialized(helmet)) {
                        initExerionItem(helmet);
                    }
                    if (helmet.getDamageValue() < helmet.getMaxDamage()) {
                        if (player.tickCount % 80 == 0) {
                            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
                            helmet.setDamageValue(Math.min(helmet.getMaxDamage(), helmet.getDamageValue() + 1));
                        }
                        if (player.tickCount % 4 == 0) {
                            HitResult hit = player.pick(6.0, 1.0f, false);
                            if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult blockHit) {
                                BlockPos targetPos = blockHit.getBlockPos();
                                BlockEntity be = player.level().getBlockEntity(targetPos);
                                if (be != null) {
                                    String msg = null;
                                    if (be instanceof IEnergyStorage energyStorage) {
                                        msg = "§b[HUD] " + be.getBlockState().getBlock().getName().getString() + " §7| §eEnergia: §f" + energyStorage.getEnergy() + " / " + energyStorage.getMaxEnergy() + " EU";
                                    } else if (be instanceof CoalBoilerBlockEntity boiler) {
                                        boolean isBurning = boiler.getBurnTime() > 0;
                                        msg = "§b[HUD] Caldeira a Carvão §7| §eCombustão: " + (isBurning ? "§aA Queimar" : "§cDesligada");
                                    }
                                    if (msg != null) {
                                        player.sendOverlayMessage(Component.literal(msg));
                                    }
                                }
                            }
                        }
                    }
                }

                ItemStack plate = player.getItemBySlot(EquipmentSlot.CHEST);
                if (!plate.isEmpty() && plate.is(ModItems.ELECTRIC_PLATE)) {
                    if (!isExerionInitialized(plate)) {
                        initExerionItem(plate);
                    }
                    if (plate.getDamageValue() < plate.getMaxDamage()) {
                        if (player.hurtTime == 10) {
                            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 80, 1, false, false, true));
                            plate.setDamageValue(Math.min(plate.getMaxDamage(), plate.getDamageValue() + 50));
                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 0.6f, 1.2f);
                        }
                        if (player.tickCount % 2 == 0) {
                            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                                ItemStack invStack = player.getInventory().getItem(i);
                                if (invStack.isEmpty() || invStack == plate || (!invStack.is(ModItems.ERA_SCANNER) && !invStack.is(ModItems.ELECTRIC_MINING_DRILL)) || invStack.getDamageValue() <= 0) continue;
                                invStack.setDamageValue(invStack.getDamageValue() - 1);
                                plate.setDamageValue(Math.min(plate.getMaxDamage(), plate.getDamageValue() + 1));
                                break;
                            }
                        }
                    }
                }

                ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
                if (!boots.isEmpty() && boots.is(ModItems.STEAM_BOOTS)) {
                    if (!isExerionInitialized(boots)) {
                        initExerionItem(boots);
                    }
                    if (boots.getDamageValue() < boots.getMaxDamage() && player.fallDistance > 0.0) {
                        int fallBlocks = (int) player.fallDistance;
                        if (fallBlocks > 0) {
                            player.fallDistance = 0.0f;
                            boots.setDamageValue(Math.min(boots.getMaxDamage(), boots.getDamageValue() + fallBlocks * 3));
                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 1.5f);
                        }
                    }
                }
            }
        }));

        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(MOD_ID, "exerion_ore"))
        );
        LOGGER.info("Exerion Mod initialized successfully!");
    }
}
