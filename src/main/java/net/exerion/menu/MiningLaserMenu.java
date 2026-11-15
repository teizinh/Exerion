package net.exerion.menu;

import net.exerion.block.entity.MiningLaserBlockEntity;
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

public class MiningLaserMenu
extends AbstractContainerMenu {
    private final Container container;
    private final MiningLaserBlockEntity blockEntity;
    private final ContainerData containerData;

    public MiningLaserMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuTypes.MINING_LASER_MENU, containerId);
        this.container = container;
        this.blockEntity = container instanceof MiningLaserBlockEntity ? (MiningLaserBlockEntity)container : null;
        this.containerData = data;
        MiningLaserMenu.checkContainerSize((Container)container, (int)1);
        this.addDataSlots(data);
        this.addSlot(new Slot(container, 0, 80, 45) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == ModItems.SPEED_UPGRADE;
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

    public MiningLaserMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, (Container)new SimpleContainer(1), (ContainerData)new SimpleContainerData(3));
    }

    public MiningLaserBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getEnergy() {
        return this.containerData.get(0);
    }

    public int getMaxEnergy() {
        int max = this.containerData.get(1);
        return max > 0 ? max : 20000;
    }

    public int getOffsetY() {
        return this.containerData.get(2);
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < 1) {
                if (!this.moveItemStackTo(slotStack, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotStack.getItem() == ModItems.SPEED_UPGRADE) {
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
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

