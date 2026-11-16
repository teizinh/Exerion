package net.exerion.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.exerion.block.ModBlocks;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class ExerionJeiPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath("exerion", "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
            new CrusherRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new AlloySmelterRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new CompressorRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new GearPressRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CrusherRecipeCategory.TYPE, ExerionRecipeMaker.getCrusherRecipes());
        registration.addRecipes(AlloySmelterRecipeCategory.TYPE, ExerionRecipeMaker.getAlloySmelterRecipes());
        registration.addRecipes(CompressorRecipeCategory.TYPE, ExerionRecipeMaker.getCompressorRecipes());
        registration.addRecipes(GearPressRecipeCategory.TYPE, ExerionRecipeMaker.getGearPressRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CRUSHER), CrusherRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ELECTRIC_CRUSHER), CrusherRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ALLOY_SMELTER), AlloySmelterRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COMPRESSOR), CompressorRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GEAR_PRESS), GearPressRecipeCategory.TYPE);
    }
}
