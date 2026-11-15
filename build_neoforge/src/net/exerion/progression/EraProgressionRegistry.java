/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 */
package net.exerion.progression;

import java.util.HashMap;
import java.util.Map;
import net.exerion.block.ModBlocks;
import net.exerion.item.ModItems;
import net.exerion.progression.PlayerEraManager;
import net.exerion.progression.TechnologyEra;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class EraProgressionRegistry {
    private static final Map<Identifier, TechnologyEra> ITEM_ERAS = new HashMap<Identifier, TechnologyEra>();
    private static final Map<Identifier, TechnologyEra> BLOCK_ERAS = new HashMap<Identifier, TechnologyEra>();

    public static void registerItemEra(Item item, TechnologyEra era) {
        ITEM_ERAS.put(BuiltInRegistries.ITEM.getKey((Object)item), era);
    }

    public static void registerBlockEra(Block block, TechnologyEra era) {
        BLOCK_ERAS.put(BuiltInRegistries.BLOCK.getKey((Object)block), era);
    }

    public static TechnologyEra getRequiredEra(Item item) {
        Identifier id = BuiltInRegistries.ITEM.getKey((Object)item);
        if (ITEM_ERAS.containsKey(id)) {
            return ITEM_ERAS.get(id);
        }
        return EraProgressionRegistry.getDynamicEra(id);
    }

    public static TechnologyEra getRequiredEra(Block block) {
        Identifier id = BuiltInRegistries.BLOCK.getKey((Object)block);
        if (BLOCK_ERAS.containsKey(id)) {
            return BLOCK_ERAS.get(id);
        }
        return EraProgressionRegistry.getDynamicEra(id);
    }

    private static TechnologyEra getDynamicEra(Identifier id) {
        String namespace = id.getNamespace();
        String path = id.getPath();
        if (namespace.contains("create")) {
            return TechnologyEra.MECHANICAL;
        }
        if (namespace.equalsIgnoreCase("minecraft")) {
            if (path.contains("netherite") || path.contains("elytra")) {
                return TechnologyEra.COSMIC;
            }
            if (path.contains("diamond") || path.contains("emerald") || path.contains("obsidian")) {
                return TechnologyEra.ADVANCED;
            }
            if (path.contains("redstone") || path.contains("quartz")) {
                return TechnologyEra.INDUSTRIAL;
            }
            if (path.contains("iron") || path.contains("gold") || path.contains("lapis")) {
                return TechnologyEra.IRON;
            }
            if (path.contains("copper")) {
                return TechnologyEra.COPPER;
            }
            if (path.contains("stone") || path.contains("wood") || path.contains("leather") || path.contains("flint") || path.contains("dirt")) {
                return TechnologyEra.STONE;
            }
        }
        return TechnologyEra.STONE;
    }

    public static boolean hasUnlockedRequiredEra(Player player, Item item) {
        TechnologyEra required = EraProgressionRegistry.getRequiredEra(item);
        TechnologyEra playerEra = PlayerEraManager.getPlayerEra(player);
        return playerEra.isAtLeast(required);
    }

    public static boolean hasUnlockedRequiredEra(Player player, Block block) {
        TechnologyEra required = EraProgressionRegistry.getRequiredEra(block);
        TechnologyEra playerEra = PlayerEraManager.getPlayerEra(player);
        return playerEra.isAtLeast(required);
    }

    public static void init() {
        EraProgressionRegistry.registerBlockEra(ModBlocks.GEAR_PRESS, TechnologyEra.IRON);
        EraProgressionRegistry.registerBlockEra(ModBlocks.CRUSHER, TechnologyEra.IRON);
        EraProgressionRegistry.registerItemEra(ModItems.COAL_DUST, TechnologyEra.IRON);
        EraProgressionRegistry.registerItemEra(ModItems.IRON_DUST, TechnologyEra.IRON);
        EraProgressionRegistry.registerItemEra(ModItems.COPPER_DUST, TechnologyEra.IRON);
        EraProgressionRegistry.registerItemEra(ModItems.GEAR_BLUEPRINT, TechnologyEra.IRON);
        EraProgressionRegistry.registerItemEra(ModItems.COPPER_GEAR, TechnologyEra.IRON);
        EraProgressionRegistry.registerItemEra(ModItems.IRON_GEAR, TechnologyEra.IRON);
        EraProgressionRegistry.registerItemEra(ModItems.EXERION_CORE, TechnologyEra.STEEL);
        EraProgressionRegistry.registerItemEra(ModItems.STEEL_COMPOUND, TechnologyEra.STEEL);
        EraProgressionRegistry.registerItemEra(ModItems.STEAM_CRYSTAL, TechnologyEra.STEEL);
        EraProgressionRegistry.registerItemEra(ModItems.STEEL_INGOT, TechnologyEra.STEEL);
        EraProgressionRegistry.registerItemEra(ModItems.WRENCH, TechnologyEra.STEEL);
        EraProgressionRegistry.registerBlockEra(ModBlocks.EXERION_CASING, TechnologyEra.STEEL);
        EraProgressionRegistry.registerBlockEra(ModBlocks.EXERION_FRAME, TechnologyEra.STEEL);
        EraProgressionRegistry.registerBlockEra(ModBlocks.COAL_BOILER, TechnologyEra.STEEL);
        EraProgressionRegistry.registerBlockEra(ModBlocks.STEAM_TURBINE, TechnologyEra.STEEL);
        EraProgressionRegistry.registerItemEra(ModItems.EXERION_PLATE, TechnologyEra.INDUSTRIAL);
        EraProgressionRegistry.registerItemEra(ModItems.EXERION_ROD, TechnologyEra.INDUSTRIAL);
        EraProgressionRegistry.registerItemEra(ModItems.CIRCUIT_BOARD, TechnologyEra.INDUSTRIAL);
        EraProgressionRegistry.registerItemEra(ModItems.ERA_SCANNER, TechnologyEra.INDUSTRIAL);
        EraProgressionRegistry.registerBlockEra(ModBlocks.EXERION_BLOCK, TechnologyEra.INDUSTRIAL);
        EraProgressionRegistry.registerBlockEra(ModBlocks.ELECTRIC_GENERATOR, TechnologyEra.INDUSTRIAL);
        EraProgressionRegistry.registerBlockEra(ModBlocks.COPPER_CABLE, TechnologyEra.INDUSTRIAL);
        EraProgressionRegistry.registerBlockEra(ModBlocks.STEEL_CABLE, TechnologyEra.INDUSTRIAL);
        EraProgressionRegistry.registerBlockEra(ModBlocks.ELECTRIC_CRUSHER, TechnologyEra.INDUSTRIAL);
        EraProgressionRegistry.registerItemEra(ModItems.EXERION_GEAR, TechnologyEra.DIGITAL);
        EraProgressionRegistry.registerItemEra(ModItems.LOGIC_CHIP, TechnologyEra.DIGITAL);
        EraProgressionRegistry.registerBlockEra(ModBlocks.EXERION_CABLE, TechnologyEra.DIGITAL);
    }
}

