/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package net.exerion.block.entity;

import java.util.ArrayList;
import java.util.List;
import net.exerion.block.entity.DigitalStorageTerminalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DigitalStorageContainer
implements Container {
    private final List<SlotRef> slots = new ArrayList<SlotRef>();
    private final BlockPos terminalPos;
    private final Level level;

    public DigitalStorageContainer(Level level, BlockPos terminalPos) {
        this.level = level;
        this.terminalPos = terminalPos;
        this.rebuildMappings();
    }

    public void rebuildMappings() {
        this.slots.clear();
        if (this.level == null) {
            return;
        }
        int radius = 6;
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    BlockEntity be;
                    BlockPos pos = this.terminalPos.offset(x, y, z);
                    if (pos.equals((Object)this.terminalPos) || !((be = this.level.getBlockEntity(pos)) instanceof Container)) continue;
                    Container container = (Container)be;
                    if (be instanceof DigitalStorageTerminalBlockEntity) continue;
                    int size = container.getContainerSize();
                    for (int s = 0; s < size; ++s) {
                        this.slots.add(new SlotRef(container, s));
                        if (this.slots.size() < 54) continue;
                        return;
                    }
                }
            }
        }
    }

    public int getContainerSize() {
        return 54;
    }

    public boolean isEmpty() {
        for (int i = 0; i < this.getContainerSize(); ++i) {
            if (this.getItem(i).isEmpty()) continue;
            return false;
        }
        return true;
    }

    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < this.slots.size()) {
            SlotRef ref = this.slots.get(slot);
            return ref.container.getItem(ref.slot);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeItem(int slot, int amount) {
        if (slot >= 0 && slot < this.slots.size()) {
            SlotRef ref = this.slots.get(slot);
            ItemStack stack = ref.container.removeItem(ref.slot, amount);
            if (!stack.isEmpty()) {
                ref.container.setChanged();
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeItemNoUpdate(int slot) {
        if (slot >= 0 && slot < this.slots.size()) {
            SlotRef ref = this.slots.get(slot);
            ItemStack stack = ref.container.removeItemNoUpdate(ref.slot);
            ref.container.setChanged();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.slots.size()) {
            SlotRef ref = this.slots.get(slot);
            ref.container.setItem(ref.slot, stack);
            ref.container.setChanged();
        }
    }

    public void setChanged() {
        for (SlotRef ref : this.slots) {
            ref.container.setChanged();
        }
    }

    public boolean stillValid(Player player) {
        return player.distanceToSqr((double)this.terminalPos.getX() + 0.5, (double)this.terminalPos.getY() + 0.5, (double)this.terminalPos.getZ() + 0.5) <= 64.0;
    }

    public void clearContent() {
        for (SlotRef ref : this.slots) {
            ref.container.clearContent();
        }
    }

    public static class SlotRef {
        public final Container container;
        public final int slot;

        public SlotRef(Container container, int slot) {
            this.container = container;
            this.slot = slot;
        }
    }
}

