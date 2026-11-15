/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 */
package net.exerion.client;

import net.minecraft.client.gui.GuiGraphics;

public class GuiHelper {
    public static void drawVanillaBackground(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, -3750202);
        graphics.fill(x, y, x + width, y + 1, -1);
        graphics.fill(x, y, x + 1, y + height, -1);
        graphics.fill(x, y + height - 1, x + width, y + height, -11184811);
        graphics.fill(x + width - 1, y, x + width, y + height, -11184811);
    }

    public static void drawVanillaSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 17, y, -13158601);
        graphics.fill(x - 1, y, x, y + 17, -13158601);
        graphics.fill(x, y, x + 16, y + 16, -7631989);
        graphics.fill(x, y + 16, x + 17, y + 17, -1);
        graphics.fill(x + 16, y, x + 17, y + 16, -1);
    }

    public static void drawPlayerInventorySlots(GuiGraphics graphics, int x, int y) {
        GuiHelper.drawPlayerInventorySlots(graphics, x, y, 84);
    }

    public static void drawPlayerInventorySlots(GuiGraphics graphics, int x, int y, int startY) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                GuiHelper.drawVanillaSlot(graphics, x + 8 + col * 18, y + startY + row * 18);
            }
        }
        for (int col = 0; col < 9; ++col) {
            GuiHelper.drawVanillaSlot(graphics, x + 8 + col * 18, y + startY + 58);
        }
    }
}

