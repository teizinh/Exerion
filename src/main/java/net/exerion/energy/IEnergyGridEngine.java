package net.exerion.energy;

import net.minecraft.core.BlockPos;

public interface IEnergyGridEngine extends AutoCloseable {
    void addCable(BlockPos pos, int maxTransfer, int capacity);
    void removeCable(BlockPos pos);
    void registerDevice(BlockPos pos, boolean isSource, int amount, int rateLimit);
    void updateDevice(BlockPos pos, int amount, int rateLimit);
    void removeDevice(BlockPos pos);
    int getCableCount();
    int getGridCount();
    long[] getConnectedDevices(BlockPos cablePos);
    long[] tick();
    @Override
    void close();
}
