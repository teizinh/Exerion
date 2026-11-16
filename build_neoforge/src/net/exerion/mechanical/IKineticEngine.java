package net.exerion.mechanical;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface IKineticEngine extends AutoCloseable {
    void addShaft(BlockPos pos, Direction.Axis axis);
    void removeShaft(BlockPos pos);
    void registerSource(BlockPos pos, int outputFm, boolean isGenerating);
    void updateSource(BlockPos pos, int outputFm, boolean isGenerating);
    void removeSource(BlockPos pos);
    void registerConsumer(BlockPos pos, int requiredFm);
    void removeConsumer(BlockPos pos);
    boolean isSpinning(BlockPos pos);
    int getAvailableFM(BlockPos pos);
    int getShaftCount();
    @Override
    void close();
}
