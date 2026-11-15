package net.exerion.block.entity;

import net.exerion.mechanical.IFMConsumer;
import net.exerion.mechanical.MechanicalNetwork;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalChuteBlockEntity
extends BlockEntity
implements IFMConsumer {
    private int transferCooldown = 0;

    public MechanicalChuteBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.MECHANICAL_CHUTE_ENTITY, pos, state);
    }

    @Override
    public int getRequiredFM() {
        return 4;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            if (level.isClientSide()) {
                return;
            }
            if (this.transferCooldown > 0) {
                --this.transferCooldown;
                return;
            }
            if (MechanicalNetwork.getAvailableFM(level, pos) < this.getRequiredFM()) {
                return;
            }
            BlockEntity aboveEntity = level.getBlockEntity(pos.above());
            BlockEntity belowEntity = level.getBlockEntity(pos.below());
            if (aboveEntity instanceof Container aboveContainer && belowEntity instanceof Container belowContainer) {
                if (this.transferItem(aboveContainer, belowContainer, Direction.DOWN)) {
                    this.transferCooldown = 10;
                }
            }
        });
    }

    private boolean transferItem(Container source, Container destination, Direction direction) {
        for (int i = 0; i < source.getContainerSize(); ++i) {
            ItemStack stackInSlot = source.getItem(i);
            if (stackInSlot.isEmpty()) continue;
            if (source instanceof WorldlyContainer worldlySource && !worldlySource.canTakeItemThroughFace(i, stackInSlot, Direction.DOWN)) {
                continue;
            }
            ItemStack stackToTransfer = stackInSlot.copy();
            stackToTransfer.setCount(1);
            ItemStack remaining = this.insertItem(destination, stackToTransfer, direction);
            if (!remaining.isEmpty()) continue;
            source.removeItem(i, 1);
            source.setChanged();
            destination.setChanged();
            return true;
        }
        return false;
    }

    private ItemStack insertItem(Container destination, ItemStack stack, Direction direction) {
        for (int i = 0; i < destination.getContainerSize(); ++i) {
            if (destination instanceof WorldlyContainer worldlyDest && !worldlyDest.canPlaceItemThroughFace(i, stack, direction.getOpposite())) {
                continue;
            }
            ItemStack stackInSlot = destination.getItem(i);
            if (stackInSlot.isEmpty()) {
                destination.setItem(i, stack);
                return ItemStack.EMPTY;
            }
            if (!ItemStack.isSameItemSameComponents(stackInSlot, stack)) continue;
            int space = stackInSlot.getMaxStackSize() - stackInSlot.getCount();
            if (space < stack.getCount()) continue;
            stackInSlot.grow(stack.getCount());
            return ItemStack.EMPTY;
        }
        return stack;
    }
}
