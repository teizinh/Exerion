package net.exerion.energy;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnergyGridEngineTest {

    private void runLifecycleTest(IEnergyGridEngine engine) {
        assertNotNull(engine, "Engine should be created");

        BlockPos cable = new BlockPos(0, 64, 0);
        engine.addCable(cable, 1000, 5000);
        assertEquals(1, engine.getCableCount());
        assertEquals(1, engine.getGridCount());

        BlockPos source = new BlockPos(1, 64, 0);
        BlockPos sink = new BlockPos(-1, 64, 0);

        engine.registerDevice(source, true, 100, 100);
        engine.registerDevice(sink, false, 60, 60);

        long[] transactions = engine.tick();
        assertNotNull(transactions);
        assertTrue(transactions.length >= 4, "Should have at least 2 transactions (source drain + sink receive)");

        boolean sourceDrained = false;
        boolean sinkReceived = false;

        for (int i = 0; i < transactions.length; i += 2) {
            long packedPos = transactions[i];
            long delta = transactions[i + 1];

            if (packedPos == source.asLong()) {
                assertEquals(-60, delta, "Source should drain exactly 60 EU");
                sourceDrained = true;
            } else if (packedPos == sink.asLong()) {
                assertEquals(60, delta, "Sink should receive exactly 60 EU");
                sinkReceived = true;
            }
        }

        assertTrue(sourceDrained, "Source transaction must be present");
        assertTrue(sinkReceived, "Sink transaction must be present");

        engine.close();
    }

    private void runSplitTest(IEnergyGridEngine engine) {
        BlockPos c1 = new BlockPos(0, 64, 0);
        BlockPos c2 = new BlockPos(1, 64, 0);
        BlockPos c3 = new BlockPos(2, 64, 0);

        engine.addCable(c1, 1000, 5000);
        engine.addCable(c2, 1000, 5000);
        engine.addCable(c3, 1000, 5000);
        assertEquals(3, engine.getCableCount());
        assertEquals(1, engine.getGridCount());

        // Remove middle cable
        engine.removeCable(c2);
        assertEquals(2, engine.getCableCount());
        assertEquals(2, engine.getGridCount());

        engine.close();
    }

    @Test
    public void testFactoryEngine() {
        try (IEnergyGridEngine engine = EnergyNetworkFactory.createEngine()) {
            runLifecycleTest(engine);
        }
        try (IEnergyGridEngine engine = EnergyNetworkFactory.createEngine()) {
            runSplitTest(engine);
        }
    }

    @Test
    public void testRustEngineExplicit() {
        if (RustGridEngine.isAvailable()) {
            try (IEnergyGridEngine engine = new RustGridEngine()) {
                runLifecycleTest(engine);
            }
            try (IEnergyGridEngine engine = new RustGridEngine()) {
                runSplitTest(engine);
            }
        }
    }

    @Test
    public void testJavaFallbackExplicit() {
        try (IEnergyGridEngine engine = new JavaGridFallback()) {
            runLifecycleTest(engine);
        }
        try (IEnergyGridEngine engine = new JavaGridFallback()) {
            runSplitTest(engine);
        }
    }
}
