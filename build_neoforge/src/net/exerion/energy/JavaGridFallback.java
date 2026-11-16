package net.exerion.energy;

import java.util.*;
import net.minecraft.core.BlockPos;

public class JavaGridFallback implements IEnergyGridEngine {

    private static class CableInfo {
        final BlockPos pos;
        final int maxTransfer;
        final int capacity;
        int gridId;

        CableInfo(BlockPos pos, int maxTransfer, int capacity, int gridId) {
            this.pos = pos;
            this.maxTransfer = maxTransfer;
            this.capacity = capacity;
            this.gridId = gridId;
        }
    }

    private static class DeviceInfo {
        final BlockPos pos;
        final boolean isSource;
        int amount;
        int rateLimit;
        Integer gridId;

        DeviceInfo(BlockPos pos, boolean isSource, int amount, int rateLimit, Integer gridId) {
            this.pos = pos;
            this.isSource = isSource;
            this.amount = amount;
            this.rateLimit = rateLimit;
            this.gridId = gridId;
        }
    }

    private static class SubGrid {
        final int id;
        final Set<BlockPos> cables = new HashSet<>();
        final Set<BlockPos> devices = new HashSet<>();

        SubGrid(int id) {
            this.id = id;
        }
    }

    private final Map<BlockPos, CableInfo> cables = new HashMap<>();
    private final Map<BlockPos, DeviceInfo> devices = new HashMap<>();
    private final Map<Integer, SubGrid> grids = new HashMap<>();
    private int nextGridId = 1;

    private static final int[][] DIRS = {
        {1, 0, 0}, {-1, 0, 0},
        {0, 1, 0}, {0, -1, 0},
        {0, 0, 1}, {0, 0, -1}
    };

    private List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> list = new ArrayList<>(6);
        for (int[] d : DIRS) {
            list.add(pos.offset(d[0], d[1], d[2]));
        }
        return list;
    }

    @Override
    public synchronized void addCable(BlockPos pos, int maxTransfer, int capacity) {
        if (cables.containsKey(pos)) return;

        List<BlockPos> neighbors = getNeighbors(pos);
        Set<Integer> adjacentGridIds = new HashSet<>();
        for (BlockPos n : neighbors) {
            CableInfo neighborCable = cables.get(n);
            if (neighborCable != null) {
                adjacentGridIds.add(neighborCable.gridId);
            }
        }

        int assignedGridId;
        if (adjacentGridIds.isEmpty()) {
            assignedGridId = nextGridId++;
            SubGrid grid = new SubGrid(assignedGridId);
            grid.cables.add(pos);
            grids.put(assignedGridId, grid);
        } else if (adjacentGridIds.size() == 1) {
            assignedGridId = adjacentGridIds.iterator().next();
            SubGrid grid = grids.get(assignedGridId);
            if (grid != null) {
                grid.cables.add(pos);
            }
        } else {
            Iterator<Integer> it = adjacentGridIds.iterator();
            int mainGid = it.next();
            SubGrid mainGrid = grids.get(mainGid);

            while (it.hasNext()) {
                int otherGid = it.next();
                SubGrid otherGrid = grids.remove(otherGid);
                if (otherGrid != null) {
                    for (BlockPos cPos : otherGrid.cables) {
                        CableInfo c = cables.get(cPos);
                        if (c != null) c.gridId = mainGid;
                    }
                    for (BlockPos dPos : otherGrid.devices) {
                        DeviceInfo d = devices.get(dPos);
                        if (d != null) d.gridId = mainGid;
                    }
                    if (mainGrid != null) {
                        mainGrid.cables.addAll(otherGrid.cables);
                        mainGrid.devices.addAll(otherGrid.devices);
                    }
                }
            }
            if (mainGrid != null) {
                mainGrid.cables.add(pos);
            }
            assignedGridId = mainGid;
        }

        cables.put(pos, new CableInfo(pos, maxTransfer, capacity, assignedGridId));

        for (BlockPos n : neighbors) {
            DeviceInfo dev = devices.get(n);
            if (dev != null && dev.gridId == null) {
                dev.gridId = assignedGridId;
                SubGrid grid = grids.get(assignedGridId);
                if (grid != null) {
                    grid.devices.add(n);
                }
            }
        }
    }

    @Override
    public synchronized void removeCable(BlockPos pos) {
        CableInfo cable = cables.remove(pos);
        if (cable == null) return;

        int oldGridId = cable.gridId;
        SubGrid grid = grids.get(oldGridId);
        if (grid != null) {
            grid.cables.remove(pos);
        }

        if (grid == null || grid.cables.isEmpty()) {
            SubGrid removed = grids.remove(oldGridId);
            if (removed != null) {
                for (BlockPos dPos : removed.devices) {
                    DeviceInfo d = devices.get(dPos);
                    if (d != null) d.gridId = null;
                }
            }
            return;
        }

        // BFS connected component discovery
        Set<BlockPos> remaining = new HashSet<>(grid.cables);
        List<Set<BlockPos>> components = new ArrayList<>();

        while (!remaining.isEmpty()) {
            BlockPos start = remaining.iterator().next();
            Set<BlockPos> comp = new HashSet<>();
            Queue<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);
            remaining.remove(start);

            while (!queue.isEmpty()) {
                BlockPos cur = queue.poll();
                comp.add(cur);
                for (BlockPos n : getNeighbors(cur)) {
                    if (remaining.remove(n)) {
                        queue.add(n);
                    }
                }
            }
            components.add(comp);
        }

        if (components.size() == 1) {
            return;
        }

        Set<BlockPos> oldDevices = new HashSet<>(grid.devices);
        grid.cables.clear();
        grid.cables.addAll(components.get(0));
        grid.devices.clear();

        for (int i = 1; i < components.size(); i++) {
            int newGid = nextGridId++;
            SubGrid newGrid = new SubGrid(newGid);
            Set<BlockPos> comp = components.get(i);
            newGrid.cables.addAll(comp);
            for (BlockPos cPos : comp) {
                CableInfo c = cables.get(cPos);
                if (c != null) c.gridId = newGid;
            }
            grids.put(newGid, newGrid);
        }

        // Reconnect old devices
        for (BlockPos dPos : oldDevices) {
            DeviceInfo d = devices.get(dPos);
            if (d != null) {
                d.gridId = null;
                for (BlockPos n : getNeighbors(dPos)) {
                    CableInfo c = cables.get(n);
                    if (c != null) {
                        d.gridId = c.gridId;
                        SubGrid targetGrid = grids.get(c.gridId);
                        if (targetGrid != null) {
                            targetGrid.devices.add(dPos);
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public synchronized void registerDevice(BlockPos pos, boolean isSource, int amount, int rateLimit) {
        Integer connectedGid = null;
        for (BlockPos n : getNeighbors(pos)) {
            CableInfo c = cables.get(n);
            if (c != null) {
                connectedGid = c.gridId;
                break;
            }
        }

        if (connectedGid != null) {
            SubGrid grid = grids.get(connectedGid);
            if (grid != null) {
                grid.devices.add(pos);
            }
        }

        devices.put(pos, new DeviceInfo(pos, isSource, amount, rateLimit, connectedGid));
    }

    @Override
    public synchronized void updateDevice(BlockPos pos, int amount, int rateLimit) {
        DeviceInfo d = devices.get(pos);
        if (d != null) {
            d.amount = amount;
            d.rateLimit = rateLimit;
        }
    }

    @Override
    public synchronized void removeDevice(BlockPos pos) {
        DeviceInfo d = devices.remove(pos);
        if (d != null && d.gridId != null) {
            SubGrid grid = grids.get(d.gridId);
            if (grid != null) {
                grid.devices.remove(pos);
            }
        }
    }

    @Override
    public synchronized int getCableCount() {
        return cables.size();
    }

    @Override
    public synchronized int getGridCount() {
        return grids.size();
    }

    @Override
    public synchronized long[] getConnectedDevices(BlockPos cablePos) {
        CableInfo cable = cables.get(cablePos);
        if (cable == null) {
            return new long[0];
        }
        SubGrid grid = grids.get(cable.gridId);
        if (grid == null || grid.devices.isEmpty()) {
            return new long[0];
        }
        long[] result = new long[grid.devices.size()];
        int idx = 0;
        for (BlockPos devPos : grid.devices) {
            result[idx++] = devPos.asLong();
        }
        return result;
    }

    @Override
    public synchronized long[] tick() {
        List<Long> txBuffer = new ArrayList<>();

        for (SubGrid grid : grids.values()) {
            List<DeviceInfo> sources = new ArrayList<>();
            List<DeviceInfo> sinks = new ArrayList<>();

            for (BlockPos dPos : grid.devices) {
                DeviceInfo d = devices.get(dPos);
                if (d != null) {
                    if (d.isSource) sources.add(d);
                    else sinks.add(d);
                }
            }

            if (sources.isEmpty() || sinks.isEmpty()) continue;

            long totalAvailable = 0;
            for (DeviceInfo s : sources) {
                totalAvailable += Math.min(s.amount, s.rateLimit);
            }

            long totalDemand = 0;
            for (DeviceInfo s : sinks) {
                totalDemand += Math.min(s.amount, s.rateLimit);
            }

            if (totalAvailable == 0 || totalDemand == 0) continue;

            long totalTransfer = Math.min(totalAvailable, totalDemand);

            // Sinks
            if (totalAvailable >= totalDemand) {
                for (DeviceInfo s : sinks) {
                    int dem = Math.min(s.amount, s.rateLimit);
                    txBuffer.add(s.pos.asLong());
                    txBuffer.add((long) dem);
                }
            } else {
                long distributed = 0;
                for (int i = 0; i < sinks.size(); i++) {
                    DeviceInfo s = sinks.get(i);
                    int dem = Math.min(s.amount, s.rateLimit);
                    long share = ((long) dem * totalTransfer) / totalDemand;
                    if (i == sinks.size() - 1 && distributed + share < totalTransfer) {
                        share = totalTransfer - distributed;
                    }
                    distributed += share;
                    txBuffer.add(s.pos.asLong());
                    txBuffer.add(share);
                }
            }

            // Sources
            if (totalAvailable <= totalDemand) {
                for (DeviceInfo s : sources) {
                    int cap = Math.min(s.amount, s.rateLimit);
                    txBuffer.add(s.pos.asLong());
                    txBuffer.add(-(long) cap);
                }
            } else {
                long drained = 0;
                for (int i = 0; i < sources.size(); i++) {
                    DeviceInfo s = sources.get(i);
                    int cap = Math.min(s.amount, s.rateLimit);
                    long share = ((long) cap * totalTransfer) / totalAvailable;
                    if (i == sources.size() - 1 && drained + share < totalTransfer) {
                        share = totalTransfer - drained;
                    }
                    drained += share;
                    txBuffer.add(s.pos.asLong());
                    txBuffer.add(-share);
                }
            }
        }

        long[] result = new long[txBuffer.size()];
        for (int i = 0; i < txBuffer.size(); i++) {
            result[i] = txBuffer.get(i);
        }
        return result;
    }

    @Override
    public void close() {
        cables.clear();
        devices.clear();
        grids.clear();
    }
}
