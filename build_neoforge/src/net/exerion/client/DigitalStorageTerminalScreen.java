/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package net.exerion.client;

import java.lang.reflect.Field;
import net.exerion.client.GuiHelper;
import net.exerion.menu.DigitalStorageTerminalMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DigitalStorageTerminalScreen
extends AbstractContainerScreen<DigitalStorageTerminalMenu> {
    private EditBox searchBox;

    public DigitalStorageTerminalScreen(DigitalStorageTerminalMenu menu, Inventory playerInventory, Component title) {
        super((AbstractContainerMenu)menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    protected void init() {
        super.init();
        this.titleLabelX = 8;
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.searchBox = new EditBox(this.font, x + 76, y + 6, 92, 10, (Component)Component.literal((String)"Search"));
        this.searchBox.setResponder(this::onSearchChanged);
        this.searchBox.setBordered(false);
        this.searchBox.setTextColor(-1);
        this.searchBox.setMaxLength(20);
        this.addRenderableWidget((GuiEventListener)this.searchBox);
    }

    private void setSlotPosition(Slot slot, int x, int y) {
        try {
            Field xField = Slot.class.getDeclaredField("x");
            xField.setAccessible(true);
            xField.setInt(slot, x);
            Field yField = Slot.class.getDeclaredField("y");
            yField.setAccessible(true);
            yField.setInt(slot, y);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSearchChanged(String query) {
        String filter = query.toLowerCase().trim();
        int matchCount = 0;
        for (int i = 0; i < 54; ++i) {
            boolean matches;
            Slot slot = (Slot)((DigitalStorageTerminalMenu)this.menu).slots.get(i);
            ItemStack stack = slot.getItem();
            boolean bl = matches = filter.isEmpty() || !stack.isEmpty() && stack.getHoverName().getString().toLowerCase().contains(filter);
            if (matches) {
                int r = matchCount / 9;
                int c = matchCount % 9;
                this.setSlotPosition(slot, 8 + c * 18, 18 + r * 18);
                ++matchCount;
                continue;
            }
            this.setSlotPosition(slot, -2000, -2000);
        }
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        GuiHelper.drawVanillaBackground(graphics, x, y, this.imageWidth, this.imageHeight);
        int sbX = x + 74;
        int sbY = y + 4;
        int sbW = 94;
        int sbH = 12;
        graphics.fill(sbX - 1, sbY - 1, sbX + sbW + 1, sbY, -13158601);
        graphics.fill(sbX - 1, sbY, sbX, sbY + sbH + 1, -13158601);
        graphics.fill(sbX, sbY, sbX + sbW, sbY + sbH, -11184811);
        graphics.fill(sbX, sbY + sbH, sbX + sbW + 1, sbY + sbH + 1, -1);
        graphics.fill(sbX + sbW, sbY, sbX + sbW + 1, sbY + sbH, -1);
        GuiHelper.drawPlayerInventorySlots(graphics, x, y, 140);
        for (int i = 0; i < 54; ++i) {
            Slot slot = (Slot)((DigitalStorageTerminalMenu)this.menu).slots.get(i);
            if (slot.x < 0) continue;
            GuiHelper.drawVanillaSlot(graphics, x + slot.x, y + slot.y);
        }
    }

    public boolean keyPressed(KeyEvent event) {
        if (this.searchBox.keyPressed(event)) {
            return true;
        }
        if (this.searchBox.canConsumeInput()) {
            return true;
        }
        return super.keyPressed(event);
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, (Component)Component.literal((String)"Storage Terminal"), this.titleLabelX, 6, -12566464, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, -12566464, false);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}

