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

import java.util.List;
import net.exerion.client.GuiHelper;
import net.exerion.menu.ElectricCrusherMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ElectricCrusherScreen
extends AbstractContainerScreen<ElectricCrusherMenu> {
    public ElectricCrusherScreen(ElectricCrusherMenu menu, Inventory playerInventory, Component title) {
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
        int barX = x + 16;
        int barY = y + 20;
        int barW = 12;
        int barH = 50;
        graphics.fill(barX - 1, barY - 1, barX + barW + 1, barY, -13158601);
        graphics.fill(barX - 1, barY, barX, barY + barH + 1, -13158601);
        graphics.fill(barX, barY, barX + barW, barY + barH, -7631989);
        graphics.fill(barX, barY + barH, barX + barW + 1, barY + barH + 1, -1);
        graphics.fill(barX + barW, barY, barX + barW + 1, barY + barH, -1);
        int energy = ((ElectricCrusherMenu)this.menu).getEnergy();
        int maxEnergy = ((ElectricCrusherMenu)this.menu).getMaxEnergy();
        int crushTime = ((ElectricCrusherMenu)this.menu).getCrushTime();
        int maxCrushTime = ((ElectricCrusherMenu)this.menu).getMaxCrushTime();
        if (energy > 0) {
            double ratio = (double)energy / (double)maxEnergy;
            int fillH = (int)((double)barH * ratio);
            for (int i = 0; i < fillH; ++i) {
                int currentY = barY + barH - 1 - i;
                float pct = (float)i / (float)barH;
                int color = this.interpolateColor(-16731905, -16755102, pct);
                graphics.fill(barX, currentY, barX + barW, currentY + 1, color);
            }
        }
        int arrowX = x + 79;
        int arrowY = y + 37;
        int arrowW = 24;
        int arrowH = 12;
        graphics.fill(arrowX, arrowY + 4, arrowX + arrowW, arrowY + 8, -7631989);
        graphics.fill(arrowX + arrowW - 6, arrowY, arrowX + arrowW, arrowY + 12, -7631989);
        if (crushTime > 0) {
            double ratio = (double)crushTime / (double)maxCrushTime;
            int fillW = (int)((double)arrowW * ratio);
            int fillCol = System.currentTimeMillis() / 100L % 2L == 0L ? -16733441 : -16742196;
            graphics.fill(arrowX, arrowY + 4, arrowX + fillW, arrowY + 8, fillCol);
            if (fillW >= arrowW - 6) {
                graphics.fill(arrowX + arrowW - 6, arrowY, arrowX + arrowW, arrowY + 12, fillCol);
            }
        }
        int rx = mouseX - x;
        int ry = mouseY - y;
        if (rx >= 15 && rx <= 29 && ry >= 19 && ry <= 71) {
            this.drawCustomTooltip(graphics, mouseX, mouseY, List.of("\u00a7bElectric Crusher", "\u00a73Energy: \u00a7f" + energy + " / " + maxEnergy + " EU", "\u00a77Consumption: \u00a7c20 EU/t", "\u00a77Speed: \u00a7a5.0x"));
        }
    }

    private void drawSlotBackground(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 2, y - 2, x + 18, y + 18, -13418155);
        graphics.fill(x - 1, y - 1, x + 17, y + 17, -16644585);
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, 4, -12566464, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, -12566464, false);
        graphics.drawString(this.font, (Component)Component.literal((String)"Input"), 46, 23, -11184811, false);
        graphics.drawString(this.font, (Component)Component.literal((String)"Output"), 114, 23, -16746582, false);
        int usage = ((ElectricCrusherMenu)this.menu).getCrushTime() > 0 ? 20 : 0;
        graphics.drawString(this.font, (Component)Component.literal((String)("Use: " + usage + " EU/t")), this.imageWidth - 76, 4, -16746582, false);
    }

    private int interpolateColor(int color1, int color2, float fraction) {
        int r1 = color1 >> 16 & 0xFF;
        int g1 = color1 >> 8 & 0xFF;
        int b1 = color1 & 0xFF;
        int r2 = color2 >> 16 & 0xFF;
        int g2 = color2 >> 8 & 0xFF;
        int b2 = color2 & 0xFF;
        int r = (int)((float)r1 + (float)(r2 - r1) * fraction);
        int g = (int)((float)g1 + (float)(g2 - g1) * fraction);
        int b = (int)((float)b1 + (float)(b2 - b1) * fraction);
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    private void drawCustomTooltip(GuiGraphics graphics, int mouseX, int mouseY, List<String> textLines) {
        int padding = 6;
        int lineHeight = 10;
        int tooltipW = 0;
        for (String line : textLines) {
            int w = this.font.width(line);
            if (w <= tooltipW) continue;
            tooltipW = w;
        }
        int tooltipH = textLines.size() * lineHeight + padding * 2 - 2;
        int tx = mouseX + 12;
        int ty = mouseY - 12;
        if (tx + (tooltipW += padding * 2) > this.width) {
            tx = mouseX - tooltipW - 8;
        }
        if (ty + tooltipH > this.height) {
            ty = this.height - tooltipH - 8;
        }
        graphics.fill(tx - 1, ty - 1, tx + tooltipW + 1, ty + tooltipH + 1, -16718337);
        graphics.fill(tx, ty, tx + tooltipW, ty + tooltipH, -100530665);
        int currentY = ty + padding;
        for (String line : textLines) {
            graphics.drawString(this.font, line, tx + padding, currentY, -1, true);
            currentY += lineHeight;
        }
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}

