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
    private final List<SlotRef> slots = new ArrayList<>();
    private final List<BlockPos> cachedContainerPositions = new ArrayList<>();
    private long lastFullScanTime = 0L;
    private static final long SCAN_INTERVAL_MS = 2000L;
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

        long now = System.currentTimeMillis();
        boolean useFastPath = !this.cachedContainerPositions.isEmpty() && (now - this.lastFullScanTime < SCAN_INTERVAL_MS);

        if (useFastPath) {
            boolean valid = true;
            for (BlockPos cPos : this.cachedContainerPositions) {
                if (!this.level.isLoaded(cPos)) {
                    valid = false;
                    break;
                }
                BlockEntity be = this.level.getBlockEntity(cPos);
                if (be instanceof Container container && !(be instanceof DigitalStorageTerminalBlockEntity)) {
                    int size = container.getContainerSize();
                    for (int s = 0; s < size; ++s) {
                        this.slots.add(new SlotRef(container, s));
                        if (this.slots.size() >= 54) {
                            return;
                        }
                    }
                } else {
                    valid = false;
                    break;
                }
            }
            if (valid && !this.slots.isEmpty()) {
                return;
            }
            this.slots.clear();
        }

        // Full scan
        this.cachedContainerPositions.clear();
        this.lastFullScanTime = now;
        int radius = 6;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (int y = -radius; y <= radius; ++y) {
            for (int x = -radius; x <= radius; ++x) {
                for (int z = -radius; z <= radius; ++z) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    mpos.set(this.terminalPos.getX() + x, this.terminalPos.getY() + y, this.terminalPos.getZ() + z);
                    if (!this.level.isLoaded(mpos)) continue;

                    BlockEntity be = this.level.getBlockEntity(mpos);
                    if (!(be instanceof Container container) || be instanceof DigitalStorageTerminalBlockEntity) continue;

                    this.cachedContainerPositions.add(mpos.immutable());
                    int size = container.getContainerSize();
                    for (int s = 0; s < size; ++s) {
                        this.slots.add(new SlotRef(container, s));
                        if (this.slots.size() >= 54) {
                            return;
                        }
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

