package net.exerion.mechanical;

import java.util.ArrayDeque;
import java.util.HashSet;
import net.exerion.block.ShaftBlock;
import net.exerion.block.entity.ShaftBlockEntity;
import net.exerion.util.ExerionCrashGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalNetwork {
    public static final int MAX_PROPAGATION_DISTANCE = 24;
    public static final int LOSS_INTERVAL = 4;
    private static final Direction[] DIRECTIONS = Direction.values();

    private record NetworkNode(BlockPos pos, int distance) {}

    public static int getAvailableFM(Level level, BlockPos machinePos) {
        return ExerionCrashGuard.safeNetworkSupply("MechanicalNetwork#getAvailableFM", () -> {
            if (level == null || level.isClientSide()) {
                return 0;
            }
            HashSet<BlockPos> visited = new HashSet<>();
            ArrayDeque<NetworkNode> queue = new ArrayDeque<>();
            visited.add(machinePos);
            for (Direction dir : DIRECTIONS) {
                BlockPos neighbor = machinePos.relative(dir);
                if (visited.add(neighbor)) {
                    queue.add(new NetworkNode(neighbor, 1));
                }
            }
            int totalFM = 0;
            while (!queue.isEmpty()) {
                NetworkNode current = queue.poll();
                BlockPos pos = current.pos();
                int distance = current.distance();
                if (distance > MAX_PROPAGATION_DISTANCE) {
                    continue;
                }
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IFMSource source) {
                    int outputFM = source.getOutputFM();
                    int loss = distance / LOSS_INTERVAL;
                    int effectiveFM = Math.max(0, outputFM - loss);
                    totalFM += effectiveFM;
                    continue;
                }
                if (be instanceof ShaftBlockEntity) {
                    BlockState shaftState = level.getBlockState(pos);
                    if (shaftState.hasProperty(ShaftBlock.AXIS)) {
                        Direction.Axis axis = shaftState.getValue(ShaftBlock.AXIS);
                        for (Direction dir : DIRECTIONS) {
                            if (dir.getAxis() != axis) {
                                continue;
                            }
                            BlockPos neighbor = pos.relative(dir);
                            if (visited.add(neighbor)) {
                                queue.add(new NetworkNode(neighbor, distance + 1));
                            }
                        }
                    }
                }
            }
            return totalFM;
        }, 0);
    }

    public static boolean hasActiveSource(Level level, BlockPos startPos) {
        return ExerionCrashGuard.safeNetworkSupply("MechanicalNetwork#hasActiveSource", () -> {
            if (level == null || level.isClientSide()) {
                return false;
            }
            HashSet<BlockPos> visited = new HashSet<>();
            ArrayDeque<NetworkNode> queue = new ArrayDeque<>();
            visited.add(startPos);
            queue.add(new NetworkNode(startPos, 0));
            while (!queue.isEmpty()) {
                NetworkNode current = queue.poll();
                BlockPos pos = current.pos();
                int distance = current.distance();
                if (distance > MAX_PROPAGATION_DISTANCE) {
                    continue;
                }
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IFMSource source) {
                    if (source.isGeneratingFM()) {
                        return true;
                    }
                    continue;
                }
                if (be instanceof ShaftBlockEntity) {
                    BlockState shaftState = level.getBlockState(pos);
                    if (shaftState.hasProperty(ShaftBlock.AXIS)) {
                        Direction.Axis axis = shaftState.getValue(ShaftBlock.AXIS);
                        for (Direction dir : DIRECTIONS) {
                            if (dir.getAxis() != axis) {
                                continue;
                            }
                            BlockPos neighbor = pos.relative(dir);
                            if (visited.add(neighbor)) {
                                queue.add(new NetworkNode(neighbor, distance + 1));
                            }
                        }
                    }
                }
            }
            return false;
        }, false);
    }
}
