package net.exerion.mechanical;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class GlobalKineticNetwork {
    private static final Map<String, IKineticEngine> ENGINES = new ConcurrentHashMap<>();

    public static IKineticEngine getEngineForDimension(String dimensionId) {
        if (dimensionId == null) return null;
        return ENGINES.computeIfAbsent(dimensionId, k -> KineticNetworkFactory.createEngine());
    }

    public static IKineticEngine getEngine(Level level) {
        if (level == null || level.isClientSide()) {
            return null;
        }
        return getEngineForDimension(level.dimension().identifier().toString());
    }

    public static void addShaft(Level level, BlockPos pos, Direction.Axis axis) {
        if (level == null || level.isClientSide()) return;
        IKineticEngine engine = getEngine(level);
        if (engine != null) {
            engine.addShaft(pos, axis);
        }
    }

    public static void removeShaft(Level level, BlockPos pos) {
        if (level == null || level.isClientSide()) return;
        IKineticEngine engine = getEngine(level);
        if (engine != null) {
            engine.removeShaft(pos);
        }
    }

    public static void registerSource(Level level, BlockPos pos, int outputFm, boolean isGenerating) {
        if (level == null || level.isClientSide()) return;
        IKineticEngine engine = getEngine(level);
        if (engine != null) {
            engine.registerSource(pos, outputFm, isGenerating);
        }
    }

    public static void updateSource(Level level, BlockPos pos, int outputFm, boolean isGenerating) {
        if (level == null || level.isClientSide()) return;
        IKineticEngine engine = getEngine(level);
        if (engine != null) {
            engine.updateSource(pos, outputFm, isGenerating);
        }
    }

    public static void removeSource(Level level, BlockPos pos) {
        if (level == null || level.isClientSide()) return;
        IKineticEngine engine = getEngine(level);
        if (engine != null) {
            engine.removeSource(pos);
        }
    }

    public static void registerConsumer(Level level, BlockPos pos, int requiredFm) {
        if (level == null || level.isClientSide()) return;
        IKineticEngine engine = getEngine(level);
        if (engine != null) {
            engine.registerConsumer(pos, requiredFm);
        }
    }

    public static void removeConsumer(Level level, BlockPos pos) {
        if (level == null || level.isClientSide()) return;
        IKineticEngine engine = getEngine(level);
        if (engine != null) {
            engine.removeConsumer(pos);
        }
    }

    public static boolean isSpinning(Level level, BlockPos pos) {
        if (level == null || level.isClientSide()) return false;
        IKineticEngine engine = getEngine(level);
        return engine != null && engine.isSpinning(pos);
    }

    public static int getAvailableFM(Level level, BlockPos pos) {
        if (level == null || level.isClientSide()) return 0;
        IKineticEngine engine = getEngine(level);
        return engine != null ? engine.getAvailableFM(pos) : 0;
    }

    public static int getShaftCount(Level level) {
        if (level == null || level.isClientSide()) return 0;
        IKineticEngine engine = getEngine(level);
        return engine != null ? engine.getShaftCount() : 0;
    }

    public static void clear() {
        ExerionCrashGuard.safeNetwork("GlobalKineticNetwork#clear", () -> {
            for (IKineticEngine engine : ENGINES.values()) {
                engine.close();
            }
            ENGINES.clear();
        });
    }
}
