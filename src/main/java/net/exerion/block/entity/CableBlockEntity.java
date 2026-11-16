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

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level != null && !level.isClientSide()) {
            net.exerion.energy.GlobalEnergyNetwork.onCableAdded(level, this.worldPosition, this.getTransferRate(), this.getCapacity());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level != null && !this.level.isClientSide()) {
            net.exerion.energy.GlobalEnergyNetwork.onCableRemoved(this.level, this.worldPosition);
        }
    }

    private int receiveEnergyLocal(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.getMaxEnergy() - this.getEnergy(), maxReceive);
        if (!simulate) {
            this.setEnergy(this.getEnergy() + energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (this.level == null || this.level.isClientSide() || maxReceive <= 0) {
            return receiveEnergyLocal(maxReceive, simulate);
        }

        int routed = net.exerion.energy.GlobalEnergyNetwork.routeEnergy(this.level, this.worldPosition, maxReceive, simulate);
        if (routed >= maxReceive || simulate) {
            return routed;
        }

        int remaining = maxReceive - routed;
        int stored = receiveEnergyLocal(remaining, false);
        return routed + stored;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        // No-op: Cable polling is completely replaced by GlobalEnergyNetwork (0 tick overhead)
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

