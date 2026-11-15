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

import net.exerion.block.entity.ProgressionTerminalBlockEntity;
import net.exerion.menu.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ProgressionTerminalMenu
extends AbstractContainerMenu {
    private final Container container;
    private final ProgressionTerminalBlockEntity blockEntity;

    public ProgressionTerminalMenu(int containerId, Inventory playerInventory, Container container, ProgressionTerminalBlockEntity blockEntity) {
        super(ModMenuTypes.PROGRESSION_TERMINAL_MENU, containerId);
        this.container = container;
        this.blockEntity = blockEntity;
        ProgressionTerminalMenu.checkContainerSize((Container)container, (int)5);
        for (int i = 0; i < 5; ++i) {
            this.addSlot(new Slot(container, i, 8 + i * 18, 20));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot((Container)playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot((Container)playerInventory, col, 8 + col * 18, 198));
        }
    }

    public ProgressionTerminalMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, (Container)new SimpleContainer(5), null);
    }

    public ProgressionTerminalBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < 5 ? !this.moveItemStackTo(slotStack, 5, this.slots.size(), true) : !this.moveItemStackTo(slotStack, 0, 5, false)) {
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

