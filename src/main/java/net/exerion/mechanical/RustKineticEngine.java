package net.exerion.mechanical;

import java.lang.ref.Cleaner;
import net.exerion.energy.RustGridEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class RustKineticEngine implements IKineticEngine {
    private static final Cleaner CLEANER = Cleaner.create();

    private static class NetworkState implements Runnable {
        private long handle;

        NetworkState(long handle) {
            this.handle = handle;
        }

        @Override
        public void run() {
            if (this.handle != 0) {
                destroyKineticNetwork(this.handle);
                this.handle = 0;
            }
        }
    }

    private final NetworkState state;
    private final Cleaner.Cleanable cleanable;

    public RustKineticEngine() {
        if (!RustGridEngine.isAvailable()) {
            throw new IllegalStateException("Native library is not available for RustKineticEngine", RustGridEngine.getLoadError());
        }
        long h = createKineticNetwork();
        this.state = new NetworkState(h);
        this.cleanable = CLEANER.register(this, this.state);
    }

    private int axisToOrdinal(Direction.Axis axis) {
        if (axis == Direction.Axis.X) return 0;
        if (axis == Direction.Axis.Y) return 1;
        return 2;
    }

    @Override
    public void addShaft(BlockPos pos, Direction.Axis axis) {
        if (this.state.handle != 0) {
            addShaft(this.state.handle, pos.getX(), pos.getY(), pos.getZ(), axisToOrdinal(axis));
        }
    }

    @Override
    public void removeShaft(BlockPos pos) {
        if (this.state.handle != 0) {
            removeShaft(this.state.handle, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void registerSource(BlockPos pos, int outputFm, boolean isGenerating) {
        if (this.state.handle != 0) {
            registerSource(this.state.handle, pos.getX(), pos.getY(), pos.getZ(), outputFm, isGenerating);
        }
    }

    @Override
    public void updateSource(BlockPos pos, int outputFm, boolean isGenerating) {
        if (this.state.handle != 0) {
            updateSource(this.state.handle, pos.getX(), pos.getY(), pos.getZ(), outputFm, isGenerating);
        }
    }

    @Override
    public void removeSource(BlockPos pos) {
        if (this.state.handle != 0) {
            removeSource(this.state.handle, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void registerConsumer(BlockPos pos, int requiredFm) {
        if (this.state.handle != 0) {
            registerConsumer(this.state.handle, pos.getX(), pos.getY(), pos.getZ(), requiredFm);
        }
    }

    @Override
    public void removeConsumer(BlockPos pos) {
        if (this.state.handle != 0) {
            removeConsumer(this.state.handle, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public boolean isSpinning(BlockPos pos) {
        return this.state.handle != 0 && isSpinning(this.state.handle, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public int getAvailableFM(BlockPos pos) {
        return this.state.handle != 0 ? getAvailableFM(this.state.handle, pos.getX(), pos.getY(), pos.getZ()) : 0;
    }

    @Override
    public int getShaftCount() {
        return this.state.handle != 0 ? getShaftCount(this.state.handle) : 0;
    }

    @Override
    public synchronized void close() {
        this.cleanable.clean();
    }

    private static native long createKineticNetwork();
    private static native void destroyKineticNetwork(long handle);
    private static native void addShaft(long handle, int x, int y, int z, int axis);
    private static native void removeShaft(long handle, int x, int y, int z);
    private static native void registerSource(long handle, int x, int y, int z, int outputFm, boolean isGenerating);
    private static native void updateSource(long handle, int x, int y, int z, int outputFm, boolean isGenerating);
    private static native void removeSource(long handle, int x, int y, int z);
    private static native void registerConsumer(long handle, int x, int y, int z, int requiredFm);
    private static native void removeConsumer(long handle, int x, int y, int z);
    private static native boolean isSpinning(long handle, int x, int y, int z);
    private static native int getAvailableFM(long handle, int x, int y, int z);
    private static native int getShaftCount(long handle);
}
