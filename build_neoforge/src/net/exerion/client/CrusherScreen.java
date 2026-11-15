/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package net.exerion.client;

import net.exerion.client.GuiHelper;
import net.exerion.menu.CrusherMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CrusherScreen
extends AbstractContainerScreen<CrusherMenu> {
    public CrusherScreen(CrusherMenu menu, Inventory playerInventory, Component title) {
        super((AbstractContainerMenu)menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width((FormattedText)this.title)) / 2;
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        GuiHelper.drawVanillaBackground(graphics, x, y, this.imageWidth, this.imageHeight);
        GuiHelper.drawPlayerInventorySlots(graphics, x, y);
        GuiHelper.drawVanillaSlot(graphics, x + 56, y + 35);
        GuiHelper.drawVanillaSlot(graphics, x + 116, y + 35);
        GuiHelper.drawVanillaSlot(graphics, x + 152, y + 17);
        int arrowX = x + 79;
        int arrowY = y + 37;
        int arrowW = 24;
        int arrowH = 12;
        graphics.fill(arrowX, arrowY + 4, arrowX + arrowW, arrowY + 8, -7631989);
        graphics.fill(arrowX + arrowW - 6, arrowY, arrowX + arrowW, arrowY + 12, -7631989);
        int crushTime = ((CrusherMenu)this.menu).getCrushTime();
        int maxCrushTime = ((CrusherMenu)this.menu).getMaxCrushTime();
        if (crushTime > 0) {
            double ratio = (double)crushTime / (double)maxCrushTime;
            int fillW = (int)((double)arrowW * ratio);
            graphics.fill(arrowX, arrowY + 4, arrowX + fillW, arrowY + 8, -2592768);
            if (fillW >= arrowW - 6) {
                graphics.fill(arrowX + arrowW - 6, arrowY, arrowX + arrowW, arrowY + 12, -2592768);
            }
        }
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, 4, -12566464, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, -12566464, false);
        graphics.drawString(this.font, (Component)Component.literal((String)"Ore"), 46, 23, -11184811, false);
        graphics.drawString(this.font, (Component)Component.literal((String)"Dust"), 118, 23, -6719744, false);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}

