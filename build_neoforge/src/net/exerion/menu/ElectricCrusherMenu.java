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

import net.exerion.block.entity.ElectricCrusherBlockEntity;
import net.exerion.item.ModItems;
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

public class ElectricCrusherMenu
extends AbstractContainerMenu {
    private final Container container;
    private final ElectricCrusherBlockEntity blockEntity;
    private final ContainerData containerData;

    public ElectricCrusherMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuTypes.ELECTRIC_CRUSHER_MENU, containerId);
        this.container = container;
        this.blockEntity = container instanceof ElectricCrusherBlockEntity ? (ElectricCrusherBlockEntity)container : null;
        this.containerData = data;
        ElectricCrusherMenu.checkContainerSize((Container)container, (int)3);
        this.addDataSlots(data);
        this.addSlot(new Slot(container, 0, 56, 35));
        this.addSlot(new Slot(this, container, 1, 116, 35){

            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(this, container, 2, 152, 17){

            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == ModItems.SPEED_UPGRADE || stack.getItem() == ModItems.EFFICIENCY_UPGRADE;
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

    public ElectricCrusherMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, (Container)new SimpleContainer(3), (ContainerData)new SimpleContainerData(4));
    }

    public ElectricCrusherBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getCrushTime() {
        return this.containerData.get(0);
    }

    public int getMaxCrushTime() {
        int max = this.containerData.get(1);
        return max > 0 ? max : 20;
    }

    public int getEnergy() {
        return this.containerData.get(2);
    }

    public int getMaxEnergy() {
        int max = this.containerData.get(3);
        return max > 0 ? max : 5000;
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < 3 ? !this.moveItemStackTo(slotStack, 3, this.slots.size(), true) : (slotStack.getItem() == ModItems.SPEED_UPGRADE || slotStack.getItem() == ModItems.EFFICIENCY_UPGRADE ? !this.moveItemStackTo(slotStack, 2, 3, false) && !this.moveItemStackTo(slotStack, 0, 1, false) : !this.moveItemStackTo(slotStack, 0, 1, false))) {
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

