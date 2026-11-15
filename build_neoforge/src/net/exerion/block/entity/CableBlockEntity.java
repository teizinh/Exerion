package net.exerion.block.entity;

import net.exerion.block.CableBlock;
import net.exerion.block.entity.ModBlockEntityTypes;
import net.exerion.energy.IEnergyStorage;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CableBlockEntity
extends BlockEntity
implements IEnergyStorage {
    private static final Direction[] DIRECTIONS = Direction.values();
    private int energy = 0;

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CABLE_ENTITY, pos, state);
    }

    public int getCapacity() {
        BlockState state;
        Block block;
        if (this.level != null && (block = (state = this.getBlockState()).getBlock()) instanceof CableBlock) {
            CableBlock cableBlock = (CableBlock)block;
            return cableBlock.getMaxCapacity();
        }
        return 500;
    }

    public int getTransferRate() {
        BlockState state;
        Block block;
        if (this.level != null && (block = (state = this.getBlockState()).getBlock()) instanceof CableBlock) {
            CableBlock cableBlock = (CableBlock)block;
            return cableBlock.getMaxTransfer();
        }
        return 100;
    }

    @Override
    public int getEnergy() {
        return this.energy;
    }

    @Override
    public int getMaxEnergy() {
        return this.getCapacity();
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(this.getMaxEnergy(), energy));
        this.setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        ExerionCrashGuard.safeTick(level, pos, state, this, () -> {
            if (level.isClientSide()) {
                return;
            }
            if (this.energy <= 0) {
                return;
            }
            int transferRate = this.getTransferRate();
            for (Direction dir : DIRECTIONS) {
                if (this.energy <= 0) break;
                BlockEntity be = level.getBlockEntity(pos.relative(dir));
                if (!(be instanceof IEnergyStorage)) continue;
                IEnergyStorage targetStorage = (IEnergyStorage)be;
                if (be instanceof CableBlockEntity) {
                    int difference;
                    int toTransfer;
                    CableBlockEntity neighborCable = (CableBlockEntity)be;
                    if (this.energy <= neighborCable.energy || (toTransfer = Math.min(transferRate, (difference = this.energy - neighborCable.energy) / 2)) <= 0) continue;
                    int received = neighborCable.receiveEnergy(toTransfer, false);
                    this.energy -= received;
                    this.setChanged();
                    continue;
                }
                int toTransfer = Math.min(transferRate, this.energy);
                int received = targetStorage.receiveEnergy(toTransfer, false);
                if (received <= 0) continue;
                this.energy -= received;
                this.setChanged();
            }
        });
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", this.energy);
    }

    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("Energy", 0);
    }
}

