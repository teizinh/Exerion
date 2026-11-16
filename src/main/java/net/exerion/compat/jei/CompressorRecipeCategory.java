package net.exerion.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.exerion.block.ModBlocks;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class CompressorRecipeCategory implements IRecipeCategory<CompressorJeiRecipe> {
    public static final Identifier UID = Identifier.fromNamespaceAndPath("exerion", "compressor");
    public static final RecipeType<CompressorJeiRecipe> TYPE = new RecipeType<>(UID, CompressorJeiRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;

    public CompressorRecipeCategory(IGuiHelper helper) {
        Identifier texture = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/furnace.png");
        this.background = helper.createDrawable(texture, 55, 16, 82, 54);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.COMPRESSOR));
    }

    @Override
    public RecipeType<CompressorJeiRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.exerion.compressor");
    }

    @Override
    public int getWidth() {
        return 82;
    }

    @Override
    public int getHeight() {
        return 54;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(CompressorJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CompressorJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 19).add(recipe.output());
    }
}
