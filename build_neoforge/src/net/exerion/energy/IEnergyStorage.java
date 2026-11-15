/*
 * Decompiled with CFR 0.152.
 */
package net.exerion.energy;

public interface IEnergyStorage {
    public int getEnergy();

    public int getMaxEnergy();

    public void setEnergy(int var1);

    default public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.getMaxEnergy() - this.getEnergy(), maxReceive);
        if (!simulate) {
            this.setEnergy(this.getEnergy() + energyReceived);
        }
        return energyReceived;
    }

    default public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.getEnergy(), maxExtract);
        if (!simulate) {
            this.setEnergy(this.getEnergy() - energyExtracted);
        }
        return energyExtracted;
    }
}

