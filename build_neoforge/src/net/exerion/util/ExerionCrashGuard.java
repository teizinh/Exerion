package net.exerion.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExerionCrashGuard: Proteção Anti-Crash para máquinas, block entities e redes do Exerion.
 * Captura qualquer Throwable / Exception não tratada, impedindo a queda do servidor,
 * aplicando cooldown de execução e throttling de logs e notificações in-game para administradores.
 */
public class ExerionCrashGuard {
    private static final Logger LOGGER = LoggerFactory.getLogger("ExerionCrashGuard");

    // Throttling: máximo 1 log a cada 5 segundos por bloco / rede
    public static final long LOG_THROTTLE_MS = 5000L;
    // Cooldown base: pausar máquina por 40 ticks (~2000 ms) em caso de falha contínua
    public static final long BASE_PAUSE_MS = 2000L;
    public static final long MAX_PAUSE_MS = 10000L;
    private static final long CLEANUP_THRESHOLD_MS = 60000L;

    private static final Map<BlockPos, Long> LAST_LOG_TIMES = new ConcurrentHashMap<>();
    private static final Map<BlockPos, Long> PAUSED_UNTIL = new ConcurrentHashMap<>();
    private static final Map<BlockPos, Integer> ERROR_COUNTS = new ConcurrentHashMap<>();

    private static final Map<String, Long> NETWORK_LOG_TIMES = new ConcurrentHashMap<>();

    /**
     * Envolve a execução do tick de um BlockEntity com proteção anti-crash.
     *
     * @param level  Nível do mundo
     * @param pos    Posição do bloco no mundo
     * @param state  Estado do bloco (BlockState)
     * @param be     Instância do BlockEntity
     * @param ticker Lógica de execução do tick
     */
    public static void safeTick(Level level, BlockPos pos, BlockState state, BlockEntity be, Runnable ticker) {
        BlockPos immutablePos = (pos != null) ? pos.immutable() : null;

        if (immutablePos != null && isPaused(immutablePos)) {
            return; // Máquina em cooldown devido a falha anterior
        }

        try {
            ticker.run();
            // Se o tick executou com sucesso, limpa o contador de falhas consecutivas
            if (immutablePos != null && ERROR_COUNTS.containsKey(immutablePos)) {
                ERROR_COUNTS.remove(immutablePos);
            }
        } catch (Throwable t) {
            handleCrash(level, immutablePos, state, be, t);
        }
    }

    /**
     * Sobrecarga de safeTick para conveniência.
     */
    public static void safeTick(Level level, BlockPos pos, BlockEntity be, Runnable ticker) {
        safeTick(level, pos, (be != null ? be.getBlockState() : null), be, ticker);
    }

    /**
     * Verifica se a máquina numa determinada posição está temporariamente pausada.
     */
    public static boolean isPaused(BlockPos pos) {
        if (pos == null) {
            return false;
        }
        Long pauseTime = PAUSED_UNTIL.get(pos);
        if (pauseTime == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now < pauseTime) {
            return true;
        }
        PAUSED_UNTIL.remove(pos);
        return false;
    }

    /**
     * Tratamento centralizado de exceções capturadas.
     */
    private static void handleCrash(Level level, BlockPos pos, BlockState state, BlockEntity be, Throwable t) {
        long now = System.currentTimeMillis();
        String machineType = getMachineType(state, be);
        String posStr = (pos != null) ? String.format("[%d, %d, %d]", pos.getX(), pos.getY(), pos.getZ()) : "[Desconhecido]";

        if (pos != null) {
            int errors = ERROR_COUNTS.merge(pos, 1, Integer::sum);
            long pauseDuration = Math.min(BASE_PAUSE_MS * errors, MAX_PAUSE_MS);
            PAUSED_UNTIL.put(pos, now + pauseDuration);
        }

        // Throttling de logs (máximo 1 a cada 5 segundos por bloco)
        boolean shouldLog = true;
        if (pos != null) {
            Long lastLog = LAST_LOG_TIMES.get(pos);
            if (lastLog != null && (now - lastLog) < LOG_THROTTLE_MS) {
                shouldLog = false;
            } else {
                LAST_LOG_TIMES.put(pos, now);
            }
        }

        if (shouldLog) {
            String errorMsg = t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();
            String logMsg = String.format("[EXERION CRASH GUARD] Erro capturado em %s (%s): %s. O servidor manteve-se ativo!",
                    posStr, machineType, errorMsg);
            LOGGER.error(logMsg, t);
            System.err.println(logMsg);

            // Notifica administradores in-game se estivermos no servidor
            if (level instanceof ServerLevel serverLevel) {
                notifyAdmins(serverLevel, posStr, machineType);
            }
        }

        cleanupOldEntries(now);
    }

    private static void notifyAdmins(ServerLevel serverLevel, String posStr, String machineType) {
        try {
            MinecraftServer server = serverLevel.getServer();
            if (server != null && server.getPlayerList() != null) {
                PlayerList playerList = server.getPlayerList();
                String alertText = String.format("§c[Exerion Alert] Máquina em %s teve um erro e foi pausada temporariamente para segurança do servidor.", posStr);
                Component alertMsg = Component.literal(alertText);

                for (ServerPlayer player : playerList.getPlayers()) {
                    try {
                        NameAndId nameAndId = new NameAndId(player.getGameProfile());
                        boolean isOp = playerList.isOp(nameAndId) || server.isSingleplayerOwner(nameAndId);
                        if (isOp) {
                            player.sendSystemMessage(alertMsg);
                        }
                    } catch (Throwable ignored) {
                        // Segurança total: erro de permissão não propaga
                    }
                }
            }
        } catch (Throwable ignored) {
            // Segurança total: notificação in-game nunca deve lançar exceção
        }
    }

    private static String getMachineType(BlockState state, BlockEntity be) {
        if (be != null) {
            return be.getClass().getSimpleName();
        }
        if (state != null && state.getBlock() != null) {
            return state.getBlock().getName().getString();
        }
        return "Desconhecido";
    }

    /**
     * Executa com segurança uma operação de rede (sem retorno).
     */
    public static void safeNetwork(String networkName, Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            long now = System.currentTimeMillis();
            Long lastLog = NETWORK_LOG_TIMES.get(networkName);
            if (lastLog == null || (now - lastLog) >= LOG_THROTTLE_MS) {
                NETWORK_LOG_TIMES.put(networkName, now);
                String errorMsg = t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();
                String logMsg = String.format("[EXERION CRASH GUARD] Erro capturado na rede (%s): %s. O servidor manteve-se ativo!", networkName, errorMsg);
                LOGGER.error(logMsg, t);
                System.err.println(logMsg);
            }
        }
    }

    /**
     * Executa com segurança um fornecedor (Supplier) de rede com valor fallback em caso de erro.
     */
    public static <T> T safeNetworkSupply(String networkName, Supplier<T> supplier, T fallback) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            long now = System.currentTimeMillis();
            Long lastLog = NETWORK_LOG_TIMES.get(networkName);
            if (lastLog == null || (now - lastLog) >= LOG_THROTTLE_MS) {
                NETWORK_LOG_TIMES.put(networkName, now);
                String errorMsg = t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();
                String logMsg = String.format("[EXERION CRASH GUARD] Erro capturado na rede (%s): %s. O servidor manteve-se ativo!", networkName, errorMsg);
                LOGGER.error(logMsg, t);
                System.err.println(logMsg);
            }
            return fallback;
        }
    }

    /**
     * Remove entradas expiradas para evitar fugas de memória.
     */
    private static void cleanupOldEntries(long now) {
        if (PAUSED_UNTIL.size() > 50) {
            PAUSED_UNTIL.entrySet().removeIf(entry -> now > entry.getValue() + CLEANUP_THRESHOLD_MS);
        }
        if (LAST_LOG_TIMES.size() > 50) {
            LAST_LOG_TIMES.entrySet().removeIf(entry -> (now - entry.getValue()) > CLEANUP_THRESHOLD_MS);
        }
        if (ERROR_COUNTS.size() > 50) {
            ERROR_COUNTS.entrySet().removeIf(entry -> !PAUSED_UNTIL.containsKey(entry.getKey()));
        }
        if (NETWORK_LOG_TIMES.size() > 20) {
            NETWORK_LOG_TIMES.entrySet().removeIf(entry -> (now - entry.getValue()) > CLEANUP_THRESHOLD_MS);
        }
    }

    /**
     * Limpa o estado rastreado de uma posição de bloco quando removido do mundo.
     */
    public static void clearPos(BlockPos pos) {
        if (pos != null) {
            BlockPos immutablePos = pos.immutable();
            PAUSED_UNTIL.remove(immutablePos);
            LAST_LOG_TIMES.remove(immutablePos);
            ERROR_COUNTS.remove(immutablePos);
        }
    }

    /**
     * Limpa todos os estados (ex: encerramento do servidor ou mudança de nível).
     */
    public static void clearAll() {
        PAUSED_UNTIL.clear();
        LAST_LOG_TIMES.clear();
        ERROR_COUNTS.clear();
        NETWORK_LOG_TIMES.clear();
    }
}
