package net.exerion.energy;

public interface IEnergyStorage {
    int getEnergy();
    int getMaxEnergy();
    void setEnergy(int energy);

    default boolean canReceive() {
        return true;
    }

    default boolean canExtract() {
        return true;
    }

    default int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive() || maxReceive <= 0) return 0;
        int energyReceived = Math.min(this.getMaxEnergy() - this.getEnergy(), maxReceive);
        if (!simulate) {
            this.setEnergy(this.getEnergy() + energyReceived);
        }
        return energyReceived;
    }

    default int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract() || maxExtract <= 0) return 0;
        int energyExtracted = Math.min(this.getEnergy(), maxExtract);
        if (!simulate) {
            this.setEnergy(this.getEnergy() - energyExtracted);
        }
        return energyExtracted;
    }
}
