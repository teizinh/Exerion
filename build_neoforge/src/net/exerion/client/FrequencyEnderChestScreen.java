/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package net.exerion.client;

import java.util.List;
import net.exerion.client.GuiHelper;
import net.exerion.menu.FrequencyEnderChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class FrequencyEnderChestScreen
extends AbstractContainerScreen<FrequencyEnderChestMenu> {
    private static final int[] COLOR_MAP = new int[]{-986896, -425955, -3715395, -12806187, -75715, -8337633, -816214, -12103854, -6447721, -15295332, -7785837, -12827478, -8170446, -10585066, -5231066, -14869215};
    private static final String[] COLOR_NAMES = new String[]{"White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};

    public FrequencyEnderChestScreen(FrequencyEnderChestMenu menu, Inventory playerInventory, Component title) {
        super((AbstractContainerMenu)menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    protected void init() {
        super.init();
        this.titleLabelX = 8;
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"L1"), btn -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(((FrequencyEnderChestMenu)this.menu).containerId, 0);
            }
        }).bounds(x + 110, y + 4, 18, 12).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"L2"), btn -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(((FrequencyEnderChestMenu)this.menu).containerId, 1);
            }
        }).bounds(x + 130, y + 4, 18, 12).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.literal((String)"L3"), btn -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(((FrequencyEnderChestMenu)this.menu).containerId, 2);
            }
        }).bounds(x + 150, y + 4, 18, 12).build());
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        GuiHelper.drawVanillaBackground(graphics, x, y, this.imageWidth, this.imageHeight);
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                GuiHelper.drawVanillaSlot(graphics, x + 8 + col * 18, y + 18 + row * 18);
            }
        }
        GuiHelper.drawPlayerInventorySlots(graphics, x, y);
        int c1 = ((FrequencyEnderChestMenu)this.menu).getColor1();
        int c2 = ((FrequencyEnderChestMenu)this.menu).getColor2();
        int c3 = ((FrequencyEnderChestMenu)this.menu).getColor3();
        int hex1 = COLOR_MAP[c1 % 16];
        int hex2 = COLOR_MAP[c2 % 16];
        int hex3 = COLOR_MAP[c3 % 16];
        graphics.fill(x + 110, y + 1, x + 128, y + 3, hex1);
        graphics.fill(x + 130, y + 1, x + 148, y + 3, hex2);
        graphics.fill(x + 150, y + 1, x + 168, y + 3, hex3);
        int rx = mouseX - x;
        int ry = mouseY - y;
        if (rx >= 110 && rx <= 168 && ry >= 1 && ry <= 16) {
            this.drawCustomTooltip(graphics, mouseX, mouseY, List.of("\u00a7dChest Frequency", "\u00a7fL1: \u00a7e" + COLOR_NAMES[c1 % 16], "\u00a7fL2: \u00a7e" + COLOR_NAMES[c2 % 16], "\u00a7fL3: \u00a7e" + COLOR_NAMES[c3 % 16], "\u00a77Click L1/L2/L3 to change color!"));
        }
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, 6, -12566464, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, -12566464, false);
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
        graphics.fill(tx - 1, ty - 1, tx + tooltipW + 1, ty + tooltipH + 1, -2080517);
        graphics.fill(tx, ty, tx + tooltipW, ty + tooltipH, -99285729);
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

