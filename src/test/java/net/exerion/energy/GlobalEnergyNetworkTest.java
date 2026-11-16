package net.exerion.energy;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalEnergyNetworkTest {

    @BeforeEach
    @AfterEach
    public void cleanup() {
        GlobalEnergyNetwork.clear();
    }

    @Test
    public void testNetworkCreationAndLifecycle() {
        IEnergyGridEngine engine = GlobalEnergyNetwork.getEngineForDimension("minecraft:overworld");
        assertNotNull(engine);
        assertEquals(0, engine.getCableCount());

        BlockPos pos = new BlockPos(10, 64, 10);
        GlobalEnergyNetwork.onCableAdded("minecraft:overworld", pos, 1000, 5000);
        assertEquals(1, engine.getCableCount());

        GlobalEnergyNetwork.onCableRemoved("minecraft:overworld", pos);
        assertEquals(0, engine.getCableCount());
    }
}
