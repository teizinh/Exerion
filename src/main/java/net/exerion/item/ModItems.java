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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

public class ModItems {
    public static final Item EXERION_CORE = ModItems.registerItem("exerion_core", new Item.Properties());
    public static final Item RAW_EXERION = ModItems.registerItem("raw_exerion", new Item.Properties());
    public static final Item EXERION_INGOT = ModItems.registerItem("exerion_ingot", new Item.Properties());
    public static final Item EXERION_NUGGET = ModItems.registerItem("exerion_nugget", new Item.Properties());
    public static final Item EXERION_GEAR = ModItems.registerItem("exerion_gear", new Item.Properties());
    public static final Item EXERION_PLATE = ModItems.registerItem("exerion_plate", new Item.Properties());
    public static final Item EXERION_ROD = ModItems.registerItem("exerion_rod", new Item.Properties());
    public static final Item COPPER_GEAR = ModItems.registerItem("copper_gear", new Item.Properties());
    public static final Item IRON_GEAR = ModItems.registerItem("iron_gear", new Item.Properties());
    public static final Item STEEL_COMPOUND = ModItems.registerItem("steel_compound", new Item.Properties());
    public static final Item COAL_DUST = ModItems.registerItem("coal_dust", new Item.Properties());
    public static final Item IRON_DUST = ModItems.registerItem("iron_dust", new Item.Properties());
    public static final Item COPPER_DUST = ModItems.registerItem("copper_dust", new Item.Properties());
    public static final Item GEAR_BLUEPRINT = ModItems.registerItem("gear_blueprint", new Item.Properties());
    public static final Item STEEL_INGOT = ModItems.registerItem("steel_ingot", new Item.Properties());
    public static final Item WRENCH = ModItems.registerCustomItem("wrench", WrenchItem::new, new Item.Properties().stacksTo(1));
    public static final Item STEAM_CRYSTAL = ModItems.registerItem("steam_crystal", new Item.Properties());
    public static final Item CIRCUIT_BOARD = ModItems.registerItem("circuit_board", new Item.Properties());
    public static final Item ERA_SCANNER = ModItems.registerCustomItem("era_scanner", EraScannerItem::new, new Item.Properties().durability(100).stacksTo(1));
    public static final Item ELECTRIC_MINING_DRILL = ModItems.registerCustomItem("electric_mining_drill", ElectricMiningDrillItem::new, new Item.Properties().durability(500).stacksTo(1));
    public static final Item PLASMA_SWORD = ModItems.registerCustomItem("plasma_sword", PlasmaSwordItem::new, new Item.Properties().durability(1000).stacksTo(1));
    public static final Item SWISS_ARMY_KNIFE = ModItems.registerCustomItem("swiss_army_knife", SwissArmyKnifeItem::new, new Item.Properties().durability(2500).stacksTo(1));
    public static final Item LOGIC_CHIP = ModItems.registerItem("logic_chip", new Item.Properties());
    public static final Item CARBON_PLATE = ModItems.registerItem("carbon_plate", new Item.Properties());
    public static final Item SPEED_UPGRADE = ModItems.registerItem("speed_upgrade", new Item.Properties());
    public static final Item EFFICIENCY_UPGRADE = ModItems.registerItem("efficiency_upgrade", new Item.Properties());
    public static final Item STEAM_BOOTS = ModItems.registerCustomItem("steam_boots", properties -> new SteamBootsItem(properties.humanoidArmor(ModArmorMaterials.EXERION, ArmorType.BOOTS)), new Item.Properties().durability(450));
    public static final Item ELECTRIC_PLATE = ModItems.registerCustomItem("electric_plate", properties -> new ElectricPlateItem(properties.humanoidArmor(ModArmorMaterials.EXERION, ArmorType.CHESTPLATE)), new Item.Properties().durability(550));
    public static final Item LOGIC_HELMET = ModItems.registerCustomItem("logic_helmet", properties -> new LogicHelmetItem(properties.humanoidArmor(ModArmorMaterials.EXERION, ArmorType.HELMET)), new Item.Properties().durability(400));
    public static final Item EXERION_GUIDE_BOOK = ModItems.registerCustomItem("exerion_guide_book", ExerionGuideBookItem::new, new Item.Properties().stacksTo(1));

    private static Item registerItem(String name, Item.Properties properties) {
        ResourceKey key = ResourceKey.create((ResourceKey)Registries.ITEM, (Identifier)Identifier.fromNamespaceAndPath((String)"exerion", (String)name));
        return (Item)Registry.register((Registry)BuiltInRegistries.ITEM, (ResourceKey)key, new Item(properties.setId(key)));
    }

    private static <T extends Item> T registerCustomItem(String name, Function<Item.Properties, T> factory, Item.Properties properties) {
        ResourceKey key = ResourceKey.create((ResourceKey)Registries.ITEM, (Identifier)Identifier.fromNamespaceAndPath((String)"exerion", (String)name));
        return (T)((Item)Registry.register((Registry)BuiltInRegistries.ITEM, (ResourceKey)key, ((Item)factory.apply(properties.setId(key)))));
    }

    public static void registerModItems() {
        Exerion.LOGGER.info("Registering Mod Items for exerion");
    }
}

