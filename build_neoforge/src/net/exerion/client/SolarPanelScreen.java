/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package net.exerion.client;

import java.util.List;
import net.exerion.client.GuiHelper;
import net.exerion.menu.SolarPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class SolarPanelScreen
extends AbstractContainerScreen<SolarPanelMenu> {
    public SolarPanelScreen(SolarPanelMenu menu, Inventory playerInventory, Component title) {
        super((AbstractContainerMenu)menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 8;
        this.titleLabelY = 4;
    }

    protected void init() {
        super.init();
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        GuiHelper.drawVanillaBackground(graphics, x, y, this.imageWidth, this.imageHeight);
        GuiHelper.drawPlayerInventorySlots(graphics, x, y);
        int barX = x + 16;
        int barY = y + 20;
        int barW = 12;
        int barH = 50;
        graphics.fill(barX - 1, barY - 1, barX + barW + 1, barY, -13158601);
        graphics.fill(barX - 1, barY, barX, barY + barH + 1, -13158601);
        graphics.fill(barX, barY, barX + barW, barY + barH, -7631989);
        graphics.fill(barX, barY + barH, barX + barW + 1, barY + barH + 1, -1);
        graphics.fill(barX + barW, barY, barX + barW + 1, barY + barH, -1);
        int energy = ((SolarPanelMenu)this.menu).getEnergy();
        int maxEnergy = ((SolarPanelMenu)this.menu).getMaxEnergy();
        boolean generating = ((SolarPanelMenu)this.menu).isGenerating();
        if (maxEnergy > 0) {
            double ratio = (double)energy / (double)maxEnergy;
            int fillHeight = (int)((double)barH * ratio);
            graphics.fill(barX, barY + barH - fillHeight, barX + barW, barY + barH, -1726148);
        }
        int sunCenterX = x + 100;
        int sunCenterY = y + 42;
        if (generating) {
            graphics.fill(sunCenterX - 8, sunCenterY - 8, sunCenterX + 8, sunCenterY + 8, -10496);
            graphics.fill(sunCenterX - 6, sunCenterY - 6, sunCenterX + 6, sunCenterY + 6, -7883);
            double angleOffset = (double)System.currentTimeMillis() / 300.0 % (Math.PI * 2);
            for (int i = 0; i < 8; ++i) {
                double angle = angleOffset + (double)i * Math.PI / 4.0;
                int rayX = sunCenterX + (int)(14.0 * Math.cos(angle));
                int rayY = sunCenterY + (int)(14.0 * Math.sin(angle));
                graphics.fill(rayX - 1, rayY - 1, rayX + 1, rayY + 1, -1726148);
            }
        } else {
            graphics.fill(sunCenterX - 8, sunCenterY - 8, sunCenterX + 8, sunCenterY + 8, -8947849);
            graphics.fill(sunCenterX - 12, sunCenterY + 2, sunCenterX + 12, sunCenterY + 8, -11184811);
            graphics.fill(sunCenterX - 8, sunCenterY - 2, sunCenterX + 4, sunCenterY + 4, -11184811);
            graphics.fill(sunCenterX - 4, sunCenterY - 6, sunCenterX + 8, sunCenterY + 4, -11184811);
        }
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, 4, -12566464, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, -12566464, false);
        boolean generating = ((SolarPanelMenu)this.menu).isGenerating();
        int gen = ((SolarPanelMenu)this.menu).getGenerationRate();
        graphics.drawString(this.font, (Component)Component.literal((String)("Generation: " + (generating ? gen : 0) + " EU/t")), 38, 20, -16746650, false);
        graphics.drawString(this.font, (Component)Component.literal((String)("Status: " + (generating ? "\u00a72Generating" : "\u00a74Inactive"))), 38, 34, -12566464, false);
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
        int tooltipX = mouseX + 12;
        int tooltipY = mouseY - 12;
        int tooltipH = textLines.size() * lineHeight + padding * 2 - 2;
        graphics.fill(tooltipX - padding, tooltipY - padding, tooltipX + tooltipW + padding, tooltipY + tooltipH, -267776486);
        graphics.fill(tooltipX - padding - 1, tooltipY - padding - 1, tooltipX + tooltipW + padding + 1, tooltipY + tooltipH + 1, -12955541);
        for (int i = 0; i < textLines.size(); ++i) {
            graphics.drawString(this.font, (Component)Component.literal((String)textLines.get(i)), tooltipX, tooltipY + i * lineHeight, -1, false);
        }
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}

