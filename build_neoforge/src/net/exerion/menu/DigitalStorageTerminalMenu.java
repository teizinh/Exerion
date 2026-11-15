/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package net.exerion.menu;

import net.exerion.menu.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DigitalStorageTerminalMenu
extends AbstractContainerMenu {
    private final Container container;

    public DigitalStorageTerminalMenu(int containerId, Inventory playerInventory, Container container) {
        super(ModMenuTypes.DIGITAL_STORAGE_TERMINAL_MENU, containerId);
        int c;
        int r;
        this.container = container;
        DigitalStorageTerminalMenu.checkContainerSize((Container)container, (int)54);
        for (r = 0; r < 6; ++r) {
            for (c = 0; c < 9; ++c) {
                this.addSlot(new Slot(container, c + r * 9, 8 + c * 18, 18 + r * 18));
            }
        }
        for (r = 0; r < 3; ++r) {
            for (c = 0; c < 9; ++c) {
                this.addSlot(new Slot((Container)playerInventory, c + r * 9 + 9, 8 + c * 18, 140 + r * 18));
            }
        }
        for (int c2 = 0; c2 < 9; ++c2) {
            this.addSlot(new Slot((Container)playerInventory, c2, 8 + c2 * 18, 198));
        }
    }

    public DigitalStorageTerminalMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, (Container)new SimpleContainer(54));
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < 54 ? !this.moveItemStackTo(slotStack, 54, this.slots.size(), true) : !this.moveItemStackTo(slotStack, 0, 54, false)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}

