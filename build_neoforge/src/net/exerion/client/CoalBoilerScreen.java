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
import net.exerion.menu.CoalBoilerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CoalBoilerScreen
extends AbstractContainerScreen<CoalBoilerMenu> {
    public CoalBoilerScreen(CoalBoilerMenu menu, Inventory playerInventory, Component title) {
        super((AbstractContainerMenu)menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width((FormattedText)this.title)) / 2;
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int steamTimer;
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        GuiHelper.drawVanillaBackground(graphics, x, y, this.imageWidth, this.imageHeight);
        GuiHelper.drawPlayerInventorySlots(graphics, x, y);
        GuiHelper.drawVanillaSlot(graphics, x + 56, y + 17);
        GuiHelper.drawVanillaSlot(graphics, x + 56, y + 53);
        GuiHelper.drawVanillaSlot(graphics, x + 116, y + 35);
        int flameX = x + 80;
        int flameY = y + 30;
        graphics.fill(flameX + 3, flameY, flameX + 5, flameY + 2, -22016);
        graphics.fill(flameX + 2, flameY + 2, flameX + 6, flameY + 4, -35072);
        graphics.fill(flameX + 1, flameY + 4, flameX + 7, flameY + 7, -43776);
        graphics.fill(flameX + 2, flameY + 5, flameX + 6, flameY + 7, -56832);
        graphics.fill(x + 79, y + 35, x + 113, y + 37, -7631989);
        int burnTime = ((CoalBoilerMenu)this.menu).getBurnTime();
        if (burnTime > 0) {
            graphics.fill(flameX + 3, flameY - 2, flameX + 5, flameY, -256);
        }
        if ((steamTimer = ((CoalBoilerMenu)this.menu).getSteamTimer()) > 0) {
            int progressW = steamTimer * 34 / 40;
            graphics.fill(x + 79, y + 35, x + 79 + progressW, y + 37, -16733628);
        }
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, 4, -12566464, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, -12566464, false);
        graphics.drawString(this.font, (Component)Component.literal((String)"Fuel"), 56, 8, -8762880, false);
        graphics.drawString(this.font, (Component)Component.literal((String)"Water"), 56, 44, -16755303, false);
        graphics.drawString(this.font, (Component)Component.literal((String)"Out"), 116, 26, -16742332, false);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}

