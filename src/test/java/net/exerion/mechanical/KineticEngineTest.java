package net.exerion.mechanical;

import net.exerion.energy.RustGridEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KineticEngineTest {

    private void runLifecycleTest(IKineticEngine engine) {
        assertNotNull(engine, "Kinetic engine should be created");

        BlockPos sourcePos = new BlockPos(0, 64, 0);
        BlockPos s1 = new BlockPos(1, 64, 0);
        BlockPos s2 = new BlockPos(2, 64, 0);
        BlockPos consumerPos = new BlockPos(3, 64, 0);

        engine.addShaft(s1, Direction.Axis.X);
        engine.addShaft(s2, Direction.Axis.X);
        assertEquals(2, engine.getShaftCount());

        engine.registerSource(sourcePos, 40, true);
        engine.registerConsumer(consumerPos, 20);

        assertTrue(engine.isSpinning(s1), "Shaft 1 should be spinning");
        assertTrue(engine.isSpinning(s2), "Shaft 2 should be spinning");

        // Distance from (0, 64, 0) to (3, 64, 0) is 3. Loss = 3 / 4 = 0.
        int available = engine.getAvailableFM(consumerPos);
        assertEquals(40, available, "Consumer should receive 40 FM");

        // Deactivate source
        engine.updateSource(sourcePos, 40, false);
        assertFalse(engine.isSpinning(s1), "Shaft 1 should stop spinning");
        assertEquals(0, engine.getAvailableFM(consumerPos), "No FM should be available when source is inactive");

        engine.close();
    }

    private void runSplitTest(IKineticEngine engine) {
        BlockPos s1 = new BlockPos(0, 64, 0);
        BlockPos s2 = new BlockPos(1, 64, 0);
        BlockPos s3 = new BlockPos(2, 64, 0);

        engine.addShaft(s1, Direction.Axis.X);
        engine.addShaft(s2, Direction.Axis.X);
        engine.addShaft(s3, Direction.Axis.X);
        assertEquals(3, engine.getShaftCount());

        // Remove middle shaft
        engine.removeShaft(s2);
        assertEquals(2, engine.getShaftCount());

        engine.close();
    }

    private void runDirectNeighborTest(IKineticEngine engine) {
        BlockPos sourcePos = new BlockPos(10, 64, 10);
        BlockPos consumerPos = new BlockPos(11, 64, 10);

        engine.registerSource(sourcePos, 25, true);
        assertTrue(engine.isSpinning(sourcePos));

        int available = engine.getAvailableFM(consumerPos);
        assertEquals(25, available, "Consumer directly touching source should receive 25 FM");

        engine.updateSource(sourcePos, 25, false);
        assertFalse(engine.isSpinning(sourcePos));
        assertEquals(0, engine.getAvailableFM(consumerPos));

        engine.close();
    }

    @Test
    public void testFactoryEngine() {
        try (IKineticEngine engine = KineticNetworkFactory.createEngine()) {
            runLifecycleTest(engine);
        }
        try (IKineticEngine engine = KineticNetworkFactory.createEngine()) {
            runSplitTest(engine);
        }
        try (IKineticEngine engine = KineticNetworkFactory.createEngine()) {
            runDirectNeighborTest(engine);
        }
    }

    @Test
    public void testRustEngineExplicit() {
        if (RustGridEngine.isAvailable()) {
            try (IKineticEngine engine = new RustKineticEngine()) {
                runLifecycleTest(engine);
            }
            try (IKineticEngine engine = new RustKineticEngine()) {
                runSplitTest(engine);
            }
            try (IKineticEngine engine = new RustKineticEngine()) {
                runDirectNeighborTest(engine);
            }
        }
    }

    @Test
    public void testJavaFallbackExplicit() {
        try (IKineticEngine engine = new JavaKineticFallback()) {
            runLifecycleTest(engine);
        }
        try (IKineticEngine engine = new JavaKineticFallback()) {
            runSplitTest(engine);
        }
        try (IKineticEngine engine = new JavaKineticFallback()) {
            runDirectNeighborTest(engine);
        }
    }
}
