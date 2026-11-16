package net.exerion.mechanical;

import net.exerion.energy.RustGridEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KineticNetworkFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger("Exerion-KineticNetwork");

    public static IKineticEngine createEngine() {
        if (RustGridEngine.isAvailable()) {
            try {
                LOGGER.info("⚙️ Creating high-performance native Rust kinetic engine.");
                return new RustKineticEngine();
            } catch (Throwable t) {
                LOGGER.warn("⚠️ Failed to initialize RustKineticEngine instance, falling back to Java: {}", t.getMessage());
            }
        }
        LOGGER.info("☕ Creating pure-Java kinetic engine fallback.");
        return new JavaKineticFallback();
    }
}
