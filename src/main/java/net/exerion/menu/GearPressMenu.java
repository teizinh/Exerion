package net.exerion.menu;

import net.exerion.block.entity.GearPressBlockEntity;
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

public class GearPressMenu
extends AbstractContainerMenu {
    private final Container container;
    private final GearPressBlockEntity blockEntity;
    private final ContainerData containerData;

    public GearPressMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuTypes.GEAR_PRESS_MENU, containerId);
        this.container = container;
        this.blockEntity = container instanceof GearPressBlockEntity ? (GearPressBlockEntity)container : null;
        this.containerData = data;
        GearPressMenu.checkContainerSize((Container)container, (int)3);
        this.addDataSlots(data);
        this.addSlot(new Slot(container, 0, 56, 35));
        this.addSlot(new Slot(container, 1, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, 2, 152, 17) {
            @Override
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

    public GearPressMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, (Container)new SimpleContainer(3), (ContainerData)new SimpleContainerData(2));
    }

    public GearPressBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getPressTime() {
        return this.containerData.get(0);
    }

    public int getMaxPressTime() {
        int max = this.containerData.get(1);
        return max > 0 ? max : 100;
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

