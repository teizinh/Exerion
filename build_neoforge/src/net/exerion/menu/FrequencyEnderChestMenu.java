/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
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

import net.exerion.block.entity.FrequencyEnderChestBlockEntity;
import net.exerion.menu.ModMenuTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FrequencyEnderChestMenu
extends AbstractContainerMenu {
    private final Container container;
    private final FrequencyEnderChestBlockEntity blockEntity;
    private final ContainerData containerData;

    public FrequencyEnderChestMenu(int containerId, Inventory playerInventory, Container container, FrequencyEnderChestBlockEntity be, ContainerData data) {
        super(ModMenuTypes.FREQUENCY_ENDER_CHEST_MENU, containerId);
        int col;
        int row;
        this.container = container;
        this.blockEntity = be;
        this.containerData = data;
        FrequencyEnderChestMenu.checkContainerSize((Container)container, (int)27);
        this.addDataSlots(data);
        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlot(new Slot(container, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlot(new Slot((Container)playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col2 = 0; col2 < 9; ++col2) {
            this.addSlot(new Slot((Container)playerInventory, col2, 8 + col2 * 18, 142));
        }
    }

    public FrequencyEnderChestMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, (Container)new SimpleContainer(27), null, (ContainerData)new SimpleContainerData(3));
    }

    public FrequencyEnderChestBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getColor1() {
        return this.containerData.get(0);
    }

    public int getColor2() {
        return this.containerData.get(1);
    }

    public int getColor3() {
        return this.containerData.get(2);
    }

    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId >= 0 && buttonId < 3) {
            int current = this.containerData.get(buttonId);
            this.containerData.set(buttonId, (current + 1) % 16);
            if (this.blockEntity != null) {
                this.blockEntity.setChanged();
            }
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.closeContainer();
            }
            return true;
        }
        return false;
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < 27 ? !this.moveItemStackTo(slotStack, 27, this.slots.size(), true) : !this.moveItemStackTo(slotStack, 0, 27, false)) {
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

