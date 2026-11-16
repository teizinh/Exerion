package net.exerion.energy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.exerion.block.entity.CableBlockEntity;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GlobalEnergyNetwork {
    private static final Map<String, IEnergyGridEngine> ENGINES = new ConcurrentHashMap<>();

    public static IEnergyGridEngine getEngineForDimension(String dimensionId) {
        if (dimensionId == null) return null;
        return ENGINES.computeIfAbsent(dimensionId, k -> EnergyNetworkFactory.createEngine());
    }

    public static IEnergyGridEngine getEngine(Level level) {
        if (level == null || level.isClientSide()) {
            return null;
        }
        return getEngineForDimension(level.dimension().identifier().toString());
    }

    public static void onCableAdded(String dimId, BlockPos pos, int maxTransfer, int capacity) {
        IEnergyGridEngine engine = getEngineForDimension(dimId);
        if (engine != null) {
            engine.addCable(pos, maxTransfer, capacity);
        }
    }

    public static void onCableAdded(Level level, BlockPos pos, int maxTransfer, int capacity) {
        if (level != null && !level.isClientSide()) {
            onCableAdded(level.dimension().identifier().toString(), pos, maxTransfer, capacity);
        }
    }

    public static void onCableRemoved(String dimId, BlockPos pos) {
        IEnergyGridEngine engine = getEngineForDimension(dimId);
        if (engine != null) {
            engine.removeCable(pos);
        }
    }

    public static void onCableRemoved(Level level, BlockPos pos) {
        if (level != null && !level.isClientSide()) {
            onCableRemoved(level.dimension().identifier().toString(), pos);
        }
    }

    public static void registerDevice(Level level, BlockPos pos, boolean isSource, int amount, int rateLimit) {
        IEnergyGridEngine engine = getEngine(level);
        if (engine != null) {
            engine.registerDevice(pos, isSource, amount, rateLimit);
        }
    }

    public static void updateDevice(Level level, BlockPos pos, int amount, int rateLimit) {
        IEnergyGridEngine engine = getEngine(level);
        if (engine != null) {
            engine.updateDevice(pos, amount, rateLimit);
        }
    }

    public static void removeDevice(Level level, BlockPos pos) {
        IEnergyGridEngine engine = getEngine(level);
        if (engine != null) {
            engine.removeDevice(pos);
        }
    }

    public static int routeEnergy(Level level, BlockPos cablePos, int amount, boolean simulate) {
        if (level == null || level.isClientSide() || amount <= 0) {
            return 0;
        }

        IEnergyGridEngine engine = getEngine(level);
        if (engine == null) {
            return 0;
        }

        long[] connectedDevices = engine.getConnectedDevices(cablePos);
        if (connectedDevices == null || connectedDevices.length == 0) {
            return 0;
        }

        int remaining = amount;
        for (long packed : connectedDevices) {
            BlockPos targetPos = BlockPos.of(packed);
            if (!level.isLoaded(targetPos)) {
                continue;
            }
            BlockEntity be = level.getBlockEntity(targetPos);
            if (be instanceof IEnergyStorage storage && !(be instanceof CableBlockEntity)) {
                if (!storage.canReceive()) {
                    continue;
                }
                int received = storage.receiveEnergy(remaining, simulate);
                remaining -= received;
                if (remaining <= 0) {
                    break;
                }
            }
        }

        return amount - remaining;
    }

    public static void tick(ServerLevel level) {
        ExerionCrashGuard.safeNetwork("GlobalEnergyNetwork#tick", () -> {
            IEnergyGridEngine engine = getEngine(level);
            if (engine == null || engine.getCableCount() == 0) {
                return;
            }

            long[] transactions = engine.tick();
            if (transactions == null || transactions.length == 0) {
                return;
            }

            for (int i = 0; i < transactions.length; i += 2) {
                long packedPos = transactions[i];
                long delta = transactions[i + 1];

                BlockPos targetPos = BlockPos.of(packedPos);
                if (!level.isLoaded(targetPos)) {
                    continue;
                }

                BlockEntity be = level.getBlockEntity(targetPos);
                if (be instanceof IEnergyStorage storage) {
                    if (delta > 0) {
                        storage.receiveEnergy((int) delta, false);
                    } else if (delta < 0) {
                        storage.extractEnergy((int) -delta, false);
                    }
                }
            }
        });
    }

    public static void clear() {
        ExerionCrashGuard.safeNetwork("GlobalEnergyNetwork#clear", () -> {
            for (IEnergyGridEngine engine : ENGINES.values()) {
                engine.close();
            }
            ENGINES.clear();
        });
    }
}
