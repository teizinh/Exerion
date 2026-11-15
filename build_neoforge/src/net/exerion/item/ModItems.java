package net.exerion.item;

import java.util.function.Function;
import net.exerion.Exerion;
import net.exerion.item.ElectricMiningDrillItem;
import net.exerion.item.ElectricPlateItem;
import net.exerion.item.EraScannerItem;
import net.exerion.item.ExerionGuideBookItem;
import net.exerion.item.LogicHelmetItem;
import net.exerion.item.ModArmorMaterials;
import net.exerion.item.PlasmaSwordItem;
import net.exerion.item.SteamBootsItem;
import net.exerion.item.WrenchItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModItems {
    public static Item EXERION_CORE;
    public static Item RAW_EXERION;
    public static Item EXERION_INGOT;
    public static Item EXERION_NUGGET;
    public static Item EXERION_GEAR;
    public static Item EXERION_PLATE;
    public static Item EXERION_ROD;
    public static Item COPPER_GEAR;
    public static Item IRON_GEAR;
    public static Item STEEL_COMPOUND;
    public static Item COAL_DUST;
    public static Item IRON_DUST;
    public static Item COPPER_DUST;
    public static Item GEAR_BLUEPRINT;
    public static Item STEEL_INGOT;
    public static Item WRENCH;
    public static Item STEAM_CRYSTAL;
    public static Item CIRCUIT_BOARD;
    public static Item ERA_SCANNER;
    public static Item ELECTRIC_MINING_DRILL;
    public static Item PLASMA_SWORD;
    public static Item SWISS_ARMY_KNIFE;
    public static Item LOGIC_CHIP;
    public static Item CARBON_PLATE;
    public static Item SPEED_UPGRADE;
    public static Item EFFICIENCY_UPGRADE;
    public static Item STEAM_BOOTS;
    public static Item ELECTRIC_PLATE;
    public static Item LOGIC_HELMET;
    public static Item EXERION_GUIDE_BOOK;

    private static Item registerItem(RegisterEvent.RegisterHelper<Item> helper, String name, Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("exerion", name));
        Item item = new Item(properties.setId(key));
        helper.register(Identifier.fromNamespaceAndPath("exerion", name), item);
        return item;
    }

    private static <T extends Item> T registerCustomItem(RegisterEvent.RegisterHelper<Item> helper, String name, Function<Item.Properties, T> factory, Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("exerion", name));
        T item = factory.apply(properties.setId(key));
        helper.register(Identifier.fromNamespaceAndPath("exerion", name), item);
        return item;
    }

    public static void registerItems(RegisterEvent event) {
        event.register(Registries.ITEM, helper -> {
            EXERION_CORE = registerItem(helper, "exerion_core", new Item.Properties());
            RAW_EXERION = registerItem(helper, "raw_exerion", new Item.Properties());
            EXERION_INGOT = registerItem(helper, "exerion_ingot", new Item.Properties());
            EXERION_NUGGET = registerItem(helper, "exerion_nugget", new Item.Properties());
            EXERION_GEAR = registerItem(helper, "exerion_gear", new Item.Properties());
            EXERION_PLATE = registerItem(helper, "exerion_plate", new Item.Properties());
            EXERION_ROD = registerItem(helper, "exerion_rod", new Item.Properties());
            COPPER_GEAR = registerItem(helper, "copper_gear", new Item.Properties());
            IRON_GEAR = registerItem(helper, "iron_gear", new Item.Properties());
            STEEL_COMPOUND = registerItem(helper, "steel_compound", new Item.Properties());
            COAL_DUST = registerItem(helper, "coal_dust", new Item.Properties());
            IRON_DUST = registerItem(helper, "iron_dust", new Item.Properties());
            COPPER_DUST = registerItem(helper, "copper_dust", new Item.Properties());
            GEAR_BLUEPRINT = registerItem(helper, "gear_blueprint", new Item.Properties());
            STEEL_INGOT = registerItem(helper, "steel_ingot", new Item.Properties());
            WRENCH = registerCustomItem(helper, "wrench", WrenchItem::new, new Item.Properties().stacksTo(1));
            STEAM_CRYSTAL = registerItem(helper, "steam_crystal", new Item.Properties());
            CIRCUIT_BOARD = registerItem(helper, "circuit_board", new Item.Properties());
            ERA_SCANNER = registerCustomItem(helper, "era_scanner", EraScannerItem::new, new Item.Properties().durability(100).stacksTo(1));
            ELECTRIC_MINING_DRILL = registerCustomItem(helper, "electric_mining_drill", ElectricMiningDrillItem::new, new Item.Properties().durability(500).stacksTo(1));
            PLASMA_SWORD = registerCustomItem(helper, "plasma_sword", PlasmaSwordItem::new, new Item.Properties().durability(1000).stacksTo(1));
            SWISS_ARMY_KNIFE = registerCustomItem(helper, "swiss_army_knife", SwissArmyKnifeItem::new, new Item.Properties().durability(2500).stacksTo(1));
            LOGIC_CHIP = registerItem(helper, "logic_chip", new Item.Properties());
            CARBON_PLATE = registerItem(helper, "carbon_plate", new Item.Properties());
            SPEED_UPGRADE = registerItem(helper, "speed_upgrade", new Item.Properties());
            EFFICIENCY_UPGRADE = registerItem(helper, "efficiency_upgrade", new Item.Properties());
            STEAM_BOOTS = registerCustomItem(helper, "steam_boots", properties -> new SteamBootsItem(properties.humanoidArmor(ModArmorMaterials.EXERION, ArmorType.BOOTS)), new Item.Properties().durability(450));
            ELECTRIC_PLATE = registerCustomItem(helper, "electric_plate", properties -> new ElectricPlateItem(properties.humanoidArmor(ModArmorMaterials.EXERION, ArmorType.CHESTPLATE)), new Item.Properties().durability(550));
            LOGIC_HELMET = registerCustomItem(helper, "logic_helmet", properties -> new LogicHelmetItem(properties.humanoidArmor(ModArmorMaterials.EXERION, ArmorType.HELMET)), new Item.Properties().durability(400));
            EXERION_GUIDE_BOOK = registerCustomItem(helper, "exerion_guide_book", ExerionGuideBookItem::new, new Item.Properties().stacksTo(1));
        });
    }

    public static void registerModItems() {
        Exerion.LOGGER.info("Registering Mod Items for exerion");
    }
}
