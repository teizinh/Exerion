package net.exerion.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExerionCrashGuardTest {

    @BeforeEach
    @AfterEach
    public void setup() {
        ExerionCrashGuard.clearAll();
    }

    @Test
    public void testSafeTickCatchesExceptionWithoutCrashing() {
        BlockPos pos = new BlockPos(10, 64, 20);
        AtomicInteger runs = new AtomicInteger(0);

        assertDoesNotThrow(() -> {
            ExerionCrashGuard.safeTick(null, pos, null, null, () -> {
                runs.incrementAndGet();
                throw new RuntimeException("Simulated catastrophic failure in machine tick");
            });
        });

        assertEquals(1, runs.get());
        assertTrue(ExerionCrashGuard.isPaused(pos), "BlockPos should be paused after catching an error");
    }

    @Test
    public void testMachinePausedDuringCooldown() {
        BlockPos pos = new BlockPos(100, 50, 100);
        AtomicInteger runs = new AtomicInteger(0);

        // First tick throws an exception
        ExerionCrashGuard.safeTick(null, pos, null, null, () -> {
            runs.incrementAndGet();
            throw new IllegalStateException("Explosion!");
        });
        assertEquals(1, runs.get());
        assertTrue(ExerionCrashGuard.isPaused(pos));

        // Subsequent ticks during cooldown should be skipped completely
        for (int i = 0; i < 5; ++i) {
            ExerionCrashGuard.safeTick(null, pos, null, null, () -> {
                runs.incrementAndGet();
            });
        }

        assertEquals(1, runs.get(), "Ticker should not execute while machine is in paused cooldown");
    }

    @Test
    public void testClearPosResetsState() {
        BlockPos pos = new BlockPos(5, 5, 5);

        ExerionCrashGuard.safeTick(null, pos, null, null, () -> {
            throw new RuntimeException("Error");
        });
        assertTrue(ExerionCrashGuard.isPaused(pos));

        ExerionCrashGuard.clearPos(pos);
        assertFalse(ExerionCrashGuard.isPaused(pos), "clearPos should remove paused state");

        AtomicBoolean ran = new AtomicBoolean(false);
        ExerionCrashGuard.safeTick(null, pos, null, null, () -> ran.set(true));
        assertTrue(ran.get(), "Ticker should execute again after clearPos");
    }

    @Test
    public void testSafeNetworkSupplyCatchesExceptionAndReturnsFallback() {
        String result = ExerionCrashGuard.safeNetworkSupply("TestNetwork", () -> {
            if (true) throw new RuntimeException("Network pipe failure");
            return "Success";
        }, "Fallback");

        assertEquals("Fallback", result);
    }

    @Test
    public void testSafeNetworkCatchesExceptionWithoutCrashing() {
        assertDoesNotThrow(() -> {
            ExerionCrashGuard.safeNetwork("TestVoidNetwork", () -> {
                throw new RuntimeException("Network void failure");
            });
        });
    }

    @Test
    public void testSuccessfulExecutionClearsErrors() {
        BlockPos pos = new BlockPos(1, 2, 3);
        AtomicInteger count = new AtomicInteger(0);

        // Runs normally
        ExerionCrashGuard.safeTick(null, pos, null, null, count::incrementAndGet);
        assertEquals(1, count.get());
        assertFalse(ExerionCrashGuard.isPaused(pos));
    }
}
