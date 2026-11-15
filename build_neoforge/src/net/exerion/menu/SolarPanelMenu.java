/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ContainerData
 *  net.minecraft.world.inventory.SimpleContainerData
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package net.exerion.menu;

import net.exerion.block.entity.SolarPanelBlockEntity;
import net.exerion.menu.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SolarPanelMenu
extends AbstractContainerMenu {
    private final SolarPanelBlockEntity blockEntity;
    private final ContainerData containerData;

    public SolarPanelMenu(int containerId, Inventory playerInventory, SolarPanelBlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.SOLAR_PANEL_MENU, containerId);
        this.blockEntity = blockEntity;
        this.containerData = data;
        this.addDataSlots(data);
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot((Container)playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot((Container)playerInventory, col, 8 + col * 18, 142));
        }
    }

    public SolarPanelMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, (ContainerData)new SimpleContainerData(4));
    }

    public SolarPanelBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getEnergy() {
        return this.containerData.get(0);
    }

    public int getMaxEnergy() {
        int max = this.containerData.get(1);
        return max > 0 ? max : 1000;
    }

    public boolean isGenerating() {
        return this.containerData.get(2) == 1;
    }

    public int getGenerationRate() {
        return this.containerData.get(3);
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < 27 ? !this.moveItemStackTo(slotStack, 27, 36, false) : !this.moveItemStackTo(slotStack, 0, 27, false)) {
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
        return true;
    }
}

