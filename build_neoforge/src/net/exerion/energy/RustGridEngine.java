package net.exerion.energy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RustGridEngine implements IEnergyGridEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger("Exerion-RustGridEngine");
    private static boolean AVAILABLE = false;
    private static Throwable LOAD_ERROR = null;

    static {
        tryLoadNatives();
    }

    private static synchronized void tryLoadNatives() {
        if (AVAILABLE) return;
        try {
            System.loadLibrary("exerion_grid");
            AVAILABLE = true;
            LOGGER.info("🦀 Exerion native Rust engine loaded successfully via System.loadLibrary.");
            return;
        } catch (UnsatisfiedLinkError e) {
            // Fall through to unpack embedded resource
        }

        String os = System.getProperty("os.name", "").toLowerCase();
        String libName;
        if (os.contains("win")) {
            libName = "exerion_grid.dll";
        } else if (os.contains("mac") || os.contains("darwin")) {
            libName = "libexerion_grid.dylib";
        } else {
            libName = "libexerion_grid.so";
        }

        String resourcePath = "/natives/" + libName;
        try (InputStream in = RustGridEngine.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                LOAD_ERROR = new UnsatisfiedLinkError("Native library resource not found in classpath: " + resourcePath);
                LOGGER.warn("⚠️ Native library '{}' not found in jar classpath. Using pure-Java fallback.", resourcePath);
                return;
            }

            File tempDir = new File(System.getProperty("java.io.tmpdir"), "exerion_natives");
            if (!tempDir.exists() && !tempDir.mkdirs()) {
                LOGGER.warn("⚠️ Could not create temp directory for natives: {}", tempDir);
            }
            File tempLib = new File(tempDir, libName);

            try (OutputStream out = new FileOutputStream(tempLib)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            tempLib.deleteOnExit();

            System.load(tempLib.getAbsolutePath());
            AVAILABLE = true;
            LOGGER.info("🦀 Exerion native Rust engine loaded successfully from {}.", tempLib.getAbsolutePath());
        } catch (Throwable t) {
            LOAD_ERROR = t;
            LOGGER.warn("⚠️ Failed to load native Rust engine: {}. Using pure-Java fallback.", t.getMessage());
        }
    }

    public static boolean isAvailable() {
        return AVAILABLE;
    }

    public static Throwable getLoadError() {
        return LOAD_ERROR;
    }

    private static final java.lang.ref.Cleaner CLEANER = java.lang.ref.Cleaner.create();

    private static class NetworkState implements Runnable {
        private long handle;

        NetworkState(long handle) {
            this.handle = handle;
        }

        @Override
        public void run() {
            if (this.handle != 0) {
                destroyNetwork(this.handle);
                this.handle = 0;
            }
        }
    }

    private final NetworkState state;
    private final java.lang.ref.Cleaner.Cleanable cleanable;

    public RustGridEngine() {
        if (!AVAILABLE) {
            throw new IllegalStateException("RustGridEngine is not available on this platform", LOAD_ERROR);
        }
        long h = createNetwork();
        this.state = new NetworkState(h);
        this.cleanable = CLEANER.register(this, this.state);
    }

    @Override
    public void addCable(BlockPos pos, int maxTransfer, int capacity) {
        if (this.state.handle != 0) {
            addCable(this.state.handle, pos.getX(), pos.getY(), pos.getZ(), maxTransfer, capacity);
        }
    }

    @Override
    public void removeCable(BlockPos pos) {
        if (this.state.handle != 0) {
            removeCable(this.state.handle, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void registerDevice(BlockPos pos, boolean isSource, int amount, int rateLimit) {
        if (this.state.handle != 0) {
            registerDevice(this.state.handle, pos.getX(), pos.getY(), pos.getZ(), isSource, amount, rateLimit);
        }
    }

    @Override
    public void updateDevice(BlockPos pos, int amount, int rateLimit) {
        if (this.state.handle != 0) {
            updateDevice(this.state.handle, pos.getX(), pos.getY(), pos.getZ(), amount, rateLimit);
        }
    }

    @Override
    public void removeDevice(BlockPos pos) {
        if (this.state.handle != 0) {
            removeDevice(this.state.handle, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public int getCableCount() {
        return this.state.handle != 0 ? getCableCount(this.state.handle) : 0;
    }

    @Override
    public int getGridCount() {
        return this.state.handle != 0 ? getGridCount(this.state.handle) : 0;
    }

    @Override
    public long[] getConnectedDevices(BlockPos cablePos) {
        if (this.state.handle != 0) {
            long[] res = getConnectedDevices(this.state.handle, cablePos.getX(), cablePos.getY(), cablePos.getZ());
            return res != null ? res : new long[0];
        }
        return new long[0];
    }

    @Override
    public long[] tick() {
        if (this.state.handle != 0) {
            long[] res = solveTick(this.state.handle);
            return res != null ? res : new long[0];
        }
        return new long[0];
    }

    @Override
    public synchronized void close() {
        this.cleanable.clean();
    }

    // Native JNI functions
    private static native long createNetwork();
    private static native void destroyNetwork(long handle);
    private static native void addCable(long handle, int x, int y, int z, int maxTransfer, int capacity);
    private static native void removeCable(long handle, int x, int y, int z);
    private static native void registerDevice(long handle, int x, int y, int z, boolean isSource, int amount, int rateLimit);
    private static native void updateDevice(long handle, int x, int y, int z, int amount, int rateLimit);
    private static native void removeDevice(long handle, int x, int y, int z);
    private static native int getCableCount(long handle);
    private static native int getGridCount(long handle);
    private static native long[] getConnectedDevices(long handle, int x, int y, int z);
    private static native long[] solveTick(long handle);
}
