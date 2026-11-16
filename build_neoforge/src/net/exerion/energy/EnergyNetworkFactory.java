package net.exerion.energy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnergyNetworkFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger("Exerion-EnergyNetwork");

    public static IEnergyGridEngine createEngine() {
        if (RustGridEngine.isAvailable()) {
            try {
                LOGGER.info("⚡ Creating high-performance native Rust energy grid solver.");
                return new RustGridEngine();
            } catch (Throwable t) {
                LOGGER.warn("⚠️ Failed to initialize RustGridEngine instance, falling back to Java: {}", t.getMessage());
            }
        }
        LOGGER.info("☕ Creating pure-Java energy grid solver fallback.");
        return new JavaGridFallback();
    }
}
