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
import net.exerion.compat.jei.CrusherJeiRecipe;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class CrusherRecipeCategory
implements IRecipeCategory<CrusherJeiRecipe> {
    public static final Identifier UID = Identifier.fromNamespaceAndPath((String)"exerion", (String)"crusher");
    public static final RecipeType<CrusherJeiRecipe> TYPE = new RecipeType(UID, CrusherJeiRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;

    public CrusherRecipeCategory(IGuiHelper helper) {
        Identifier texture = Identifier.fromNamespaceAndPath((String)"minecraft", (String)"textures/gui/container/furnace.png");
        this.background = helper.createDrawable(texture, 55, 16, 82, 54);
        this.icon = helper.createDrawableIngredient((IIngredientType)VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CRUSHER));
    }

    public RecipeType<CrusherJeiRecipe> getRecipeType() {
        return TYPE;
    }

    public Component getTitle() {
        return Component.translatable((String)"container.exerion.crusher");
    }

    public int getWidth() {
        return 82;
    }

    public int getHeight() {
        return 54;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    public void draw(CrusherJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics, 0, 0);
    }

    public void setRecipe(IRecipeLayoutBuilder builder, CrusherJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 19).add(recipe.output());
    }
}

