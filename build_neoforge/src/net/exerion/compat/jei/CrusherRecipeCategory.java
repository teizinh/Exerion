package net.exerion.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.exerion.block.ModBlocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class CrusherRecipeCategory implements IRecipeCategory<CrusherJeiRecipe> {
    public static final IRecipeType<CrusherJeiRecipe> TYPE = IRecipeType.create("exerion", "crusher", CrusherJeiRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;

    public CrusherRecipeCategory(IGuiHelper helper) {
        Identifier texture = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/furnace.png");
        this.background = helper.createDrawable(texture, 55, 16, 82, 54);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModBlocks.CRUSHER));
    }

    @Override
    public IRecipeType<CrusherJeiRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.exerion.crusher");
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
    public IDrawable getIcon() {
        return this.icon;
    }

    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void draw(CrusherJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrusherJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 19).add(recipe.output());
    }
}
