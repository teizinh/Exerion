/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 */
package net.exerion.client;

import java.util.ArrayList;
import java.util.List;
import net.exerion.block.ModBlocks;
import net.exerion.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class ExerionGuideBookScreen
extends Screen {
    private int currentTab = 0;
    private final int imageWidth = 320;
    private final int imageHeight = 240;
    private int scrollOffset = 0;
    private final List<GuideItem> guideItems = new ArrayList<GuideItem>();
    private final List<Button> tabButtons = new ArrayList<Button>();

    public ExerionGuideBookScreen() {
        super((Component)Component.translatable((String)"gui.exerion.guidebook.title"));
    }

    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int tabW = 60;
        int tabH = 16;
        int startX = x - 64;
        int startY = y + 10;
        String[] tabNames = new String[]{"Intro", "Stone", "Copper", "Iron", "Mechanical", "Steel", "Industrial", "Digital", "Advanced", "Quantum", "Cosmic"};
        this.tabButtons.clear();
        for (int i = 0; i < tabNames.length; ++i) {
            int tabIndex = i;
            Button tabBtn = Button.builder((Component)Component.literal((String)tabNames[i]), btn -> {
                this.currentTab = tabIndex;
                this.scrollOffset = 0;
                this.updateTabButtons();
                this.rebuildGuideItems();
            }).bounds(startX, startY + i * 18, tabW, tabH).build();
            this.tabButtons.add(tabBtn);
            this.addRenderableWidget((GuiEventListener)tabBtn);
        }
        this.updateTabButtons();
        this.rebuildGuideItems();
    }

    private void updateTabButtons() {
        for (int i = 0; i < this.tabButtons.size(); ++i) {
            this.tabButtons.get((int)i).active = this.currentTab != i;
        }
    }

    private void rebuildGuideItems() {
        this.guideItems.clear();
        int currentY = 0;
        switch (this.currentTab) {
            case 1: {
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModItems.RAW_EXERION), "gui.exerion.guide.stone.raw", "gui.exerion.guide.stone.raw.desc", null));
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModItems.EXERION_CORE), "gui.exerion.guide.stone.core", "gui.exerion.guide.stone.core.desc", null));
                break;
            }
            case 2: {
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModItems.COPPER_GEAR), "gui.exerion.guide.copper.gear", "gui.exerion.guide.copper.gear.desc", null));
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.WATER_WHEEL), "gui.exerion.guide.copper.water_wheel", "gui.exerion.guide.copper.water_wheel.desc", null));
                break;
            }
            case 3: {
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.GEAR_PRESS), "gui.exerion.guide.iron.gear_press", "gui.exerion.guide.iron.gear_press.desc", new ItemStack[]{new ItemStack((ItemLike)Items.IRON_BLOCK), new ItemStack((ItemLike)Items.PISTON), new ItemStack((ItemLike)Items.IRON_BLOCK), new ItemStack((ItemLike)Items.IRON_INGOT), ItemStack.EMPTY, new ItemStack((ItemLike)Items.IRON_INGOT), new ItemStack((ItemLike)Blocks.SMOOTH_STONE), new ItemStack((ItemLike)Blocks.SMOOTH_STONE), new ItemStack((ItemLike)Blocks.SMOOTH_STONE)}));
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.CRUSHER), "gui.exerion.guide.iron.crusher", "gui.exerion.guide.iron.crusher.desc", new ItemStack[]{new ItemStack((ItemLike)Items.FLINT), new ItemStack((ItemLike)Items.IRON_INGOT), new ItemStack((ItemLike)Items.FLINT), new ItemStack((ItemLike)Items.IRON_INGOT), ItemStack.EMPTY, new ItemStack((ItemLike)Items.IRON_INGOT), new ItemStack((ItemLike)Blocks.COBBLESTONE), new ItemStack((ItemLike)Blocks.COBBLESTONE), new ItemStack((ItemLike)Blocks.COBBLESTONE)}));
                break;
            }
            case 4: {
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModItems.WRENCH), "gui.exerion.guide.mech.wrench", "gui.exerion.guide.mech.wrench.desc", new ItemStack[]{ItemStack.EMPTY, new ItemStack((ItemLike)Items.GOLD_INGOT), new ItemStack((ItemLike)Items.GOLD_INGOT), new ItemStack((ItemLike)Items.GOLD_INGOT), new ItemStack((ItemLike)ModItems.COPPER_GEAR), new ItemStack((ItemLike)Items.GOLD_INGOT), ItemStack.EMPTY, new ItemStack((ItemLike)Items.STICK), ItemStack.EMPTY}));
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.SHAFT), "gui.exerion.guide.mech.shaft", "gui.exerion.guide.mech.shaft.desc", null));
                break;
            }
            case 5: {
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.COAL_BOILER), "gui.exerion.guide.steel.boiler", "gui.exerion.guide.steel.boiler.desc", new ItemStack[]{new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)Items.BUCKET), new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)Blocks.FURNACE), new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)ModItems.STEEL_INGOT)}));
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.STEAM_TURBINE), "gui.exerion.guide.steel.turbine", "gui.exerion.guide.steel.turbine.desc", new ItemStack[]{new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)ModItems.EXERION_GEAR), new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)ModItems.EXERION_GEAR), new ItemStack((ItemLike)ModBlocks.SHAFT), new ItemStack((ItemLike)ModItems.EXERION_GEAR), new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)ModItems.EXERION_GEAR), new ItemStack((ItemLike)ModItems.STEEL_INGOT)}));
                break;
            }
            case 6: {
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.ELECTRIC_GENERATOR), "gui.exerion.guide.ind.generator", "gui.exerion.guide.ind.generator.desc", new ItemStack[]{new ItemStack((ItemLike)ModItems.CIRCUIT_BOARD), new ItemStack((ItemLike)ModItems.EXERION_ROD), new ItemStack((ItemLike)ModItems.CIRCUIT_BOARD), new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)Blocks.FURNACE), new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)ModItems.EXERION_PLATE), new ItemStack((ItemLike)ModBlocks.BATBOX), new ItemStack((ItemLike)ModItems.EXERION_PLATE)}));
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.ELECTRIC_CRUSHER), "gui.exerion.guide.ind.elec_crusher", "gui.exerion.guide.ind.elec_crusher.desc", new ItemStack[]{new ItemStack((ItemLike)ModItems.CIRCUIT_BOARD), new ItemStack((ItemLike)ModItems.LOGIC_CHIP), new ItemStack((ItemLike)ModItems.CIRCUIT_BOARD), new ItemStack((ItemLike)ModItems.EXERION_PLATE), new ItemStack((ItemLike)ModBlocks.CRUSHER), new ItemStack((ItemLike)ModItems.EXERION_PLATE), new ItemStack((ItemLike)ModItems.STEEL_INGOT), new ItemStack((ItemLike)ModBlocks.BATBOX), new ItemStack((ItemLike)ModItems.STEEL_INGOT)}));
                break;
            }
            case 7: {
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModItems.LOGIC_CHIP), "gui.exerion.guide.dig.logic", "gui.exerion.guide.dig.logic.desc", null));
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.PROGRESSION_TERMINAL), "gui.exerion.guide.dig.terminal", "gui.exerion.guide.dig.terminal.desc", null));
                break;
            }
            case 8: {
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.STEEL_SOLAR_PANEL), "gui.exerion.guide.adv.solar", "gui.exerion.guide.adv.solar.desc", new ItemStack[]{new ItemStack((ItemLike)Blocks.GLASS), new ItemStack((ItemLike)Blocks.GLASS), new ItemStack((ItemLike)Blocks.GLASS), new ItemStack((ItemLike)ModItems.CARBON_PLATE), new ItemStack((ItemLike)ModItems.CARBON_PLATE), new ItemStack((ItemLike)ModItems.CARBON_PLATE), new ItemStack((ItemLike)ModItems.LOGIC_CHIP), new ItemStack((ItemLike)ModItems.CIRCUIT_BOARD), new ItemStack((ItemLike)ModItems.LOGIC_CHIP)}));
                break;
            }
            case 9: {
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.FREQUENCY_ENDER_CHEST), "gui.exerion.guide.quant.chest", "gui.exerion.guide.quant.chest.desc", null));
                break;
            }
            case 10: {
                this.guideItems.add(new GuideItem(new ItemStack((ItemLike)ModBlocks.MINING_LASER), "gui.exerion.guide.cosm.laser", "gui.exerion.guide.cosm.laser.desc", null));
            }
        }
        for (GuideItem item : this.guideItems) {
            item.yOffset = currentY;
            String rawText = I18n.get((String)item.descKey, (Object[])new Object[0]);
            int maxTextWidth = item.recipe != null && item.recipe.length == 9 ? 135 : 280;
            String[] words = rawText.split(" ");
            StringBuilder currentLine = new StringBuilder();
            int textLines = 0;
            for (String word : words) {
                if (this.font.width(currentLine.toString() + word + " ") < maxTextWidth) {
                    currentLine.append(word).append(" ");
                    continue;
                }
                currentLine = new StringBuilder(word + " ");
                ++textLines;
            }
            if (!currentLine.isEmpty()) {
                ++textLines;
            }
            int boxHeight = Math.max(item.recipe != null ? 70 : 30, 20 + textLines * 10);
            currentY += boxHeight + 15;
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.currentTab != 0) {
            this.scrollOffset -= (int)(verticalAmount * 20.0);
            if (this.scrollOffset < 0) {
                this.scrollOffset = 0;
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.fill(x, y, x + 320, y + 240, -15066588);
        graphics.fill(x, y, x + 320, y + 1, -16733441);
        graphics.fill(x, y + 240 - 1, x + 320, y + 240, -16733441);
        graphics.fill(x, y, x + 1, y + 240, -16733441);
        graphics.fill(x + 320 - 1, y, x + 320, y + 240, -16733441);
        graphics.fill(x + 1, y + 1, x + 320 - 1, y + 24, -14013899);
        String titleText = switch (this.currentTab) {
            case 0 -> "gui.exerion.guide.tab.intro";
            case 1 -> "era.exerion.stone";
            case 2 -> "era.exerion.copper";
            case 3 -> "era.exerion.iron";
            case 4 -> "era.exerion.mechanical";
            case 5 -> "era.exerion.steel";
            case 6 -> "era.exerion.industrial";
            case 7 -> "era.exerion.digital";
            case 8 -> "era.exerion.advanced";
            case 9 -> "era.exerion.quantum";
            case 10 -> "era.exerion.cosmic";
            default -> "gui.exerion.guidebook.title";
        };
        graphics.drawString(this.font, (Component)Component.translatable((String)titleText).withStyle(ChatFormatting.BOLD), x + 8, y + 8, -1, true);
        int contentX = x + 12;
        int contentY = y + 36;
        if (this.currentTab == 0) {
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.exerion.guide.intro.welcome").withStyle(ChatFormatting.GOLD), contentX, contentY, -1626, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.exerion.guide.intro.desc1"), contentX, contentY + 13, -1446673, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.exerion.guide.intro.how_to_progress").withStyle(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.BOLD}), contentX, contentY + 32, -1, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.exerion.guide.intro.step1"), contentX, contentY + 45, -2434081, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.exerion.guide.intro.step2"), contentX, contentY + 56, -2434081, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.exerion.guide.intro.step3"), contentX, contentY + 67, -2434081, false);
            graphics.drawString(this.font, (Component)Component.translatable((String)"gui.exerion.guide.intro.sandbox").withStyle(ChatFormatting.GREEN), contentX, contentY + 90, -10485856, false);
        } else {
            graphics.enableScissor(x + 1, y + 25, x + 320 - 1, y + 240 - 1);
            for (GuideItem item : this.guideItems) {
                int slotY = contentY + item.yOffset - this.scrollOffset;
                if (slotY > y + 240 || slotY + 70 < y + 25) continue;
                graphics.renderFakeItem(item.stack, contentX, slotY);
                graphics.drawString(this.font, (Component)Component.translatable((String)item.titleKey).withStyle(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.BOLD}), contentX + 22, slotY + 4, -1446673, false);
                String rawText = I18n.get((String)item.descKey, (Object[])new Object[0]);
                int maxTextWidth = item.recipe != null && item.recipe.length == 9 ? 135 : 280;
                String[] words = rawText.split(" ");
                StringBuilder currentLine = new StringBuilder();
                int textY = slotY + 20;
                for (String word : words) {
                    if (this.font.width(currentLine.toString() + word + " ") < maxTextWidth) {
                        currentLine.append(word).append(" ");
                        continue;
                    }
                    graphics.drawString(this.font, (Component)Component.literal((String)currentLine.toString().trim()), contentX, textY, -3355444, false);
                    currentLine = new StringBuilder(word + " ");
                    textY += 10;
                }
                if (!currentLine.isEmpty()) {
                    graphics.drawString(this.font, (Component)Component.literal((String)currentLine.toString().trim()), contentX, textY, -3355444, false);
                }
                if (item.recipe == null || item.recipe.length != 9) continue;
                int gridX = contentX + 160;
                int gridY = slotY + 5;
                for (int r = 0; r < 3; ++r) {
                    for (int c = 0; c < 3; ++c) {
                        int sx = gridX + c * 18;
                        int sy = gridY + r * 18;
                        graphics.fill(sx, sy, sx + 18, sy + 18, -13158601);
                        graphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, -11907500);
                        ItemStack stack = item.recipe[r * 3 + c];
                        if (stack == null || stack.isEmpty()) continue;
                        graphics.renderFakeItem(stack, sx + 1, sy + 1);
                        if (mouseX < sx + 1 || mouseX >= sx + 17 || mouseY < sy + 1 || mouseY >= sy + 17) continue;
                        graphics.setTooltipForNextFrame(this.font, stack, mouseX, mouseY);
                    }
                }
                graphics.drawString(this.font, (Component)Component.literal((String)"\u2794"), gridX + 58, gridY + 18, -1, false);
                int resX = gridX + 72;
                int resY = gridY + 18 - 8;
                graphics.fill(resX, resY, resX + 24, resY + 24, -13158601);
                graphics.fill(resX + 1, resY + 1, resX + 23, resY + 23, -11907500);
                graphics.renderFakeItem(item.stack, resX + 4, resY + 4);
                if (mouseX < resX + 4 || mouseX >= resX + 20 || mouseY < resY + 4 || mouseY >= resY + 20) continue;
                graphics.setTooltipForNextFrame(this.font, item.stack, mouseX, mouseY);
            }
            graphics.disableScissor();
        }
    }

    public boolean isPauseScreen() {
        return false;
    }

    private static class GuideItem {
        final ItemStack stack;
        int yOffset;
        final String titleKey;
        final String descKey;
        final ItemStack[] recipe;

        GuideItem(ItemStack stack, String titleKey, String descKey, ItemStack[] recipe) {
            this.stack = stack;
            this.titleKey = titleKey;
            this.descKey = descKey;
            this.recipe = recipe;
        }
    }
}

