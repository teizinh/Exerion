/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ContainerData
 *  net.minecraft.world.inventory.SimpleContainerData
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package net.exerion.menu;

import net.exerion.block.entity.CompressorBlockEntity;
import net.exerion.menu.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CompressorMenu
extends AbstractContainerMenu {
    private final Container container;
    private final CompressorBlockEntity blockEntity;
    private final ContainerData containerData;

    public CompressorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuTypes.COMPRESSOR_MENU, containerId);
        this.container = container;
        this.blockEntity = container instanceof CompressorBlockEntity ? (CompressorBlockEntity)container : null;
        this.containerData = data;
        CompressorMenu.checkContainerSize((Container)container, (int)2);
        this.addDataSlots(data);
        this.addSlot(new Slot(container, 0, 56, 35));
        this.addSlot(new Slot(this, container, 1, 116, 35){

            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot((Container)playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot((Container)playerInventory, col, 8 + col * 18, 142));
        }
    }

    public CompressorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, (Container)new SimpleContainer(2), (ContainerData)new SimpleContainerData(4));
    }

    public CompressorBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getEnergy() {
        return this.containerData.get(0);
    }

    public int getMaxEnergy() {
        int max = this.containerData.get(1);
        return max > 0 ? max : 4000;
    }

    public int getProgress() {
        return this.containerData.get(2);
    }

    public int getMaxProgress() {
        int max = this.containerData.get(3);
        return max > 0 ? max : 160;
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < 2 ? !this.moveItemStackTo(slotStack, 2, this.slots.size(), true) : !this.moveItemStackTo(slotStack, 0, 1, false)) {
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

