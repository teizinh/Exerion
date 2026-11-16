package net.exerion.compat.jei;

import java.util.ArrayList;
import java.util.List;
import net.exerion.block.ModBlocks;
import net.exerion.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ExerionRecipeMaker {
    public static List<CrusherJeiRecipe> getCrusherRecipes() {
        ArrayList<CrusherJeiRecipe> list = new ArrayList<>();
        list.add(new CrusherJeiRecipe(new ItemStack(Items.RAW_IRON), new ItemStack(ModItems.IRON_DUST, 2)));
        list.add(new CrusherJeiRecipe(new ItemStack(Items.RAW_COPPER), new ItemStack(ModItems.COPPER_DUST, 2)));
        list.add(new CrusherJeiRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.IRON_DUST)));
        list.add(new CrusherJeiRecipe(new ItemStack(Items.COPPER_INGOT), new ItemStack(ModItems.COPPER_DUST)));
        list.add(new CrusherJeiRecipe(new ItemStack(Items.COAL), new ItemStack(ModItems.COAL_DUST)));
        list.add(new CrusherJeiRecipe(new ItemStack(Items.IRON_ORE), new ItemStack(Items.RAW_IRON, 2)));
        list.add(new CrusherJeiRecipe(new ItemStack(Items.DEEPSLATE_IRON_ORE), new ItemStack(Items.RAW_IRON, 2)));
        list.add(new CrusherJeiRecipe(new ItemStack(Items.COPPER_ORE), new ItemStack(Items.RAW_COPPER, 2)));
        list.add(new CrusherJeiRecipe(new ItemStack(Items.DEEPSLATE_COPPER_ORE), new ItemStack(Items.RAW_COPPER, 2)));
        list.add(new CrusherJeiRecipe(new ItemStack(ModBlocks.EXERION_ORE), new ItemStack(ModItems.RAW_EXERION, 2)));
        list.add(new CrusherJeiRecipe(new ItemStack(ModBlocks.DEEPSLATE_EXERION_ORE), new ItemStack(ModItems.RAW_EXERION, 2)));
        return list;
    }

    public static List<AlloySmelterJeiRecipe> getAlloySmelterRecipes() {
        ArrayList<AlloySmelterJeiRecipe> list = new ArrayList<>();
        list.add(new AlloySmelterJeiRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.COAL_DUST), new ItemStack(ModItems.STEEL_INGOT, 1)));
        list.add(new AlloySmelterJeiRecipe(new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.STEEL_INGOT, 2)));
        list.add(new AlloySmelterJeiRecipe(new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(ModItems.CIRCUIT_BOARD, 2)));
        list.add(new AlloySmelterJeiRecipe(new ItemStack(ModItems.EXERION_INGOT), new ItemStack(Items.DIAMOND), new ItemStack(ModItems.EXERION_CORE, 1)));
        return list;
    }

    public static List<CompressorJeiRecipe> getCompressorRecipes() {
        ArrayList<CompressorJeiRecipe> list = new ArrayList<>();
        list.add(new CompressorJeiRecipe(new ItemStack(ModItems.COAL_DUST), new ItemStack(ModItems.CARBON_PLATE, 1)));
        list.add(new CompressorJeiRecipe(new ItemStack(Items.IRON_INGOT, 9), new ItemStack(Items.IRON_BLOCK, 1)));
        list.add(new CompressorJeiRecipe(new ItemStack(Items.GOLD_INGOT, 9), new ItemStack(Items.GOLD_BLOCK, 1)));
        list.add(new CompressorJeiRecipe(new ItemStack(Items.COPPER_INGOT, 9), new ItemStack(Items.COPPER_BLOCK, 1)));
        list.add(new CompressorJeiRecipe(new ItemStack(Items.DIAMOND, 9), new ItemStack(Items.DIAMOND_BLOCK, 1)));
        list.add(new CompressorJeiRecipe(new ItemStack(Items.REDSTONE, 9), new ItemStack(Items.REDSTONE_BLOCK, 1)));
        list.add(new CompressorJeiRecipe(new ItemStack(ModItems.EXERION_INGOT, 9), new ItemStack(ModBlocks.EXERION_BLOCK, 1)));
        list.add(new CompressorJeiRecipe(new ItemStack(ModItems.STEEL_INGOT, 9), new ItemStack(ModBlocks.STEEL_BLOCK, 1)));
        return list;
    }

    public static List<GearPressJeiRecipe> getGearPressRecipes() {
        ArrayList<GearPressJeiRecipe> list = new ArrayList<>();
        list.add(new GearPressJeiRecipe(new ItemStack(ModItems.EXERION_INGOT), new ItemStack(ModItems.EXERION_PLATE)));
        list.add(new GearPressJeiRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.IRON_GEAR)));
        list.add(new GearPressJeiRecipe(new ItemStack(Items.COPPER_INGOT), new ItemStack(ModItems.COPPER_GEAR)));
        list.add(new GearPressJeiRecipe(new ItemStack(ModItems.EXERION_PLATE), new ItemStack(ModItems.EXERION_GEAR)));
        return list;
    }
}
