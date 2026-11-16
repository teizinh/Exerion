package net.exerion.mechanical;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class JavaKineticFallback implements IKineticEngine {
    private static class ShaftInfo {
        final BlockPos pos;
        final Direction.Axis axis;
        int networkId;

        ShaftInfo(BlockPos pos, Direction.Axis axis, int networkId) {
            this.pos = pos;
            this.axis = axis;
            this.networkId = networkId;
        }
    }

    private static class SourceInfo {
        final BlockPos pos;
        int outputFm;
        boolean isGenerating;
        Integer networkId;

        SourceInfo(BlockPos pos, int outputFm, boolean isGenerating, Integer networkId) {
            this.pos = pos;
            this.outputFm = outputFm;
            this.isGenerating = isGenerating;
            this.networkId = networkId;
        }
    }

    private static class ConsumerInfo {
        final BlockPos pos;
        int requiredFm;
        Integer networkId;

        ConsumerInfo(BlockPos pos, int requiredFm, Integer networkId) {
            this.pos = pos;
            this.requiredFm = requiredFm;
            this.networkId = networkId;
        }
    }

    private static class SubNetwork {
        final int id;
        final Direction.Axis axis;
        final Set<BlockPos> shafts = new HashSet<>();
        final Set<BlockPos> sources = new HashSet<>();
        final Set<BlockPos> consumers = new HashSet<>();
        boolean isSpinning = false;
        int totalGeneratedFm = 0;

        SubNetwork(int id, Direction.Axis axis) {
            this.id = id;
            this.axis = axis;
        }
    }

    private final Map<BlockPos, ShaftInfo> shafts = new HashMap<>();
    private final Map<BlockPos, SourceInfo> sources = new HashMap<>();
    private final Map<BlockPos, ConsumerInfo> consumers = new HashMap<>();
    private final Map<Integer, SubNetwork> networks = new HashMap<>();
    private int nextNetworkId = 1;

    private static final int[][] ALL_DIRS = {
        {1, 0, 0}, {-1, 0, 0},
        {0, 1, 0}, {0, -1, 0},
        {0, 0, 1}, {0, 0, -1}
    };

    private List<BlockPos> getAxisNeighbors(BlockPos pos, Direction.Axis axis) {
        List<BlockPos> list = new ArrayList<>(2);
        if (axis == Direction.Axis.X) {
            list.add(pos.offset(1, 0, 0));
            list.add(pos.offset(-1, 0, 0));
        } else if (axis == Direction.Axis.Y) {
            list.add(pos.offset(0, 1, 0));
            list.add(pos.offset(0, -1, 0));
        } else {
            list.add(pos.offset(0, 0, 1));
            list.add(pos.offset(0, 0, -1));
        }
        return list;
    }

    private List<BlockPos> getAllNeighbors(BlockPos pos) {
        List<BlockPos> list = new ArrayList<>(6);
        for (int[] d : ALL_DIRS) {
            list.add(pos.offset(d[0], d[1], d[2]));
        }
        return list;
    }

    private void recomputeNetwork(int netId) {
        SubNetwork net = networks.get(netId);
        if (net == null) return;

        boolean spinning = false;
        int totalFm = 0;
        for (BlockPos sPos : net.sources) {
            SourceInfo src = sources.get(sPos);
            if (src != null && src.isGenerating) {
                spinning = true;
                totalFm += src.outputFm;
            }
        }
        net.isSpinning = spinning;
        net.totalGeneratedFm = totalFm;
    }

    @Override
    public synchronized void addShaft(BlockPos pos, Direction.Axis axis) {
        if (shafts.containsKey(pos)) return;

        List<BlockPos> neighbors = getAxisNeighbors(pos, axis);
        Set<Integer> adjacentNetIds = new HashSet<>();
        for (BlockPos n : neighbors) {
            ShaftInfo s = shafts.get(n);
            if (s != null && s.axis == axis) {
                adjacentNetIds.add(s.networkId);
            }
        }

        int assignedId;
        if (adjacentNetIds.isEmpty()) {
            assignedId = nextNetworkId++;
            SubNetwork net = new SubNetwork(assignedId, axis);
            net.shafts.add(pos);
            networks.put(assignedId, net);
        } else if (adjacentNetIds.size() == 1) {
            assignedId = adjacentNetIds.iterator().next();
            SubNetwork net = networks.get(assignedId);
            if (net != null) {
                net.shafts.add(pos);
            }
        } else {
            Iterator<Integer> it = adjacentNetIds.iterator();
            int mainNid = it.next();
            SubNetwork mainNet = networks.get(mainNid);

            while (it.hasNext()) {
                int otherNid = it.next();
                SubNetwork otherNet = networks.remove(otherNid);
                if (otherNet != null) {
                    for (BlockPos sPos : otherNet.shafts) {
                        ShaftInfo s = shafts.get(sPos);
                        if (s != null) s.networkId = mainNid;
                    }
                    for (BlockPos srcPos : otherNet.sources) {
                        SourceInfo src = sources.get(srcPos);
                        if (src != null) src.networkId = mainNid;
                    }
                    for (BlockPos conPos : otherNet.consumers) {
                        ConsumerInfo con = consumers.get(conPos);
                        if (con != null) con.networkId = mainNid;
                    }
                    if (mainNet != null) {
                        mainNet.shafts.addAll(otherNet.shafts);
                        mainNet.sources.addAll(otherNet.sources);
                        mainNet.consumers.addAll(otherNet.consumers);
                    }
                }
            }
            if (mainNet != null) {
                mainNet.shafts.add(pos);
            }
            assignedId = mainNid;
        }

        shafts.put(pos, new ShaftInfo(pos, axis, assignedId));

        for (BlockPos n : getAllNeighbors(pos)) {
            SourceInfo src = sources.get(n);
            if (src != null && src.networkId == null) {
                src.networkId = assignedId;
                SubNetwork net = networks.get(assignedId);
                if (net != null) net.sources.add(n);
            }
            ConsumerInfo con = consumers.get(n);
            if (con != null && con.networkId == null) {
                con.networkId = assignedId;
                SubNetwork net = networks.get(assignedId);
                if (net != null) net.consumers.add(n);
            }
        }

        recomputeNetwork(assignedId);
    }

    @Override
    public synchronized void removeShaft(BlockPos pos) {
        ShaftInfo shaft = shafts.remove(pos);
        if (shaft == null) return;

        int oldNid = shaft.networkId;
        Direction.Axis axis = shaft.axis;
        SubNetwork net = networks.get(oldNid);
        if (net != null) {
            net.shafts.remove(pos);
        }

        if (net == null || net.shafts.isEmpty()) {
            SubNetwork removed = networks.remove(oldNid);
            if (removed != null) {
                for (BlockPos sPos : removed.sources) {
                    SourceInfo src = sources.get(sPos);
                    if (src != null) src.networkId = null;
                }
                for (BlockPos cPos : removed.consumers) {
                    ConsumerInfo con = consumers.get(cPos);
                    if (con != null) con.networkId = null;
                }
            }
            return;
        }

        Set<BlockPos> remaining = new HashSet<>(net.shafts);
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
                for (BlockPos n : getAxisNeighbors(cur, axis)) {
                    if (remaining.remove(n)) {
                        queue.add(n);
                    }
                }
            }
            components.add(comp);
        }

        if (components.size() == 1) {
            recomputeNetwork(oldNid);
            return;
        }

        Set<BlockPos> oldSources = new HashSet<>(net.sources);
        Set<BlockPos> oldConsumers = new HashSet<>(net.consumers);
        net.shafts.clear();
        net.shafts.addAll(components.get(0));
        net.sources.clear();
        net.consumers.clear();

        for (int i = 1; i < components.size(); i++) {
            int newNid = nextNetworkId++;
            SubNetwork newNet = new SubNetwork(newNid, axis);
            Set<BlockPos> comp = components.get(i);
            newNet.shafts.addAll(comp);
            for (BlockPos sPos : comp) {
                ShaftInfo s = shafts.get(sPos);
                if (s != null) s.networkId = newNid;
            }
            networks.put(newNid, newNet);
        }

        for (BlockPos sPos : oldSources) {
            SourceInfo src = sources.get(sPos);
            if (src != null) {
                src.networkId = null;
                for (BlockPos n : getAllNeighbors(sPos)) {
                    ShaftInfo s = shafts.get(n);
                    if (s != null) {
                        src.networkId = s.networkId;
                        SubNetwork targetNet = networks.get(s.networkId);
                        if (targetNet != null) targetNet.sources.add(sPos);
                        break;
                    }
                }
            }
        }

        for (BlockPos cPos : oldConsumers) {
            ConsumerInfo con = consumers.get(cPos);
            if (con != null) {
                con.networkId = null;
                for (BlockPos n : getAllNeighbors(cPos)) {
                    ShaftInfo s = shafts.get(n);
                    if (s != null) {
                        con.networkId = s.networkId;
                        SubNetwork targetNet = networks.get(s.networkId);
                        if (targetNet != null) targetNet.consumers.add(cPos);
                        break;
                    }
                }
            }
        }

        recomputeNetwork(oldNid);
        for (int i = 1; i < components.size(); i++) {
            BlockPos first = components.get(i).iterator().next();
            ShaftInfo s = shafts.get(first);
            if (s != null) recomputeNetwork(s.networkId);
        }
    }

    @Override
    public synchronized void registerSource(BlockPos pos, int outputFm, boolean isGenerating) {
        Integer connectedNid = null;
        for (BlockPos n : getAllNeighbors(pos)) {
            ShaftInfo s = shafts.get(n);
            if (s != null) {
                connectedNid = s.networkId;
                break;
            }
        }

        if (connectedNid != null) {
            SubNetwork net = networks.get(connectedNid);
            if (net != null) net.sources.add(pos);
        }

        sources.put(pos, new SourceInfo(pos, outputFm, isGenerating, connectedNid));
        if (connectedNid != null) recomputeNetwork(connectedNid);
    }

    @Override
    public synchronized void updateSource(BlockPos pos, int outputFm, boolean isGenerating) {
        SourceInfo src = sources.get(pos);
        if (src != null) {
            src.outputFm = outputFm;
            src.isGenerating = isGenerating;
            if (src.networkId != null) {
                recomputeNetwork(src.networkId);
            }
        }
    }

    @Override
    public synchronized void removeSource(BlockPos pos) {
        SourceInfo src = sources.remove(pos);
        if (src != null && src.networkId != null) {
            SubNetwork net = networks.get(src.networkId);
            if (net != null) net.sources.remove(pos);
            recomputeNetwork(src.networkId);
        }
    }

    @Override
    public synchronized void registerConsumer(BlockPos pos, int requiredFm) {
        Integer connectedNid = null;
        for (BlockPos n : getAllNeighbors(pos)) {
            ShaftInfo s = shafts.get(n);
            if (s != null) {
                connectedNid = s.networkId;
                break;
            }
        }

        if (connectedNid != null) {
            SubNetwork net = networks.get(connectedNid);
            if (net != null) net.consumers.add(pos);
        }

        consumers.put(pos, new ConsumerInfo(pos, requiredFm, connectedNid));
    }

    @Override
    public synchronized void removeConsumer(BlockPos pos) {
        ConsumerInfo con = consumers.remove(pos);
        if (con != null && con.networkId != null) {
            SubNetwork net = networks.get(con.networkId);
            if (net != null) net.consumers.remove(pos);
        }
    }

    @Override
    public synchronized boolean isSpinning(BlockPos pos) {
        ShaftInfo s = shafts.get(pos);
        if (s != null) {
            SubNetwork net = networks.get(s.networkId);
            return net != null && net.isSpinning;
        }
        SourceInfo src = sources.get(pos);
        if (src != null) {
            return src.isGenerating;
        }
        return false;
    }

    @Override
    public synchronized int getAvailableFM(BlockPos pos) {
        int totalEffective = 0;
        Set<BlockPos> countedSources = new HashSet<>();

        // 1. Direct neighbor sources
        for (BlockPos n : getAllNeighbors(pos)) {
            SourceInfo src = sources.get(n);
            if (src != null && src.isGenerating && src.outputFm > 0) {
                totalEffective += src.outputFm;
                countedSources.add(n);
            }
        }

        // 2. Shaft-connected sources
        List<Integer> nids = new ArrayList<>();
        ConsumerInfo con = consumers.get(pos);
        if (con != null && con.networkId != null) {
            nids.add(con.networkId);
        }
        for (BlockPos n : getAllNeighbors(pos)) {
            ShaftInfo s = shafts.get(n);
            if (s != null && !nids.contains(s.networkId)) {
                nids.add(s.networkId);
            }
        }

        for (int nid : nids) {
            SubNetwork net = networks.get(nid);
            if (net == null || !net.isSpinning || net.totalGeneratedFm == 0) continue;

            for (BlockPos sPos : net.sources) {
                if (countedSources.contains(sPos)) continue;
                SourceInfo src = sources.get(sPos);
                if (src != null && src.isGenerating && src.outputFm > 0) {
                    int dist = Math.abs(pos.getX() - sPos.getX())
                        + Math.abs(pos.getY() - sPos.getY())
                        + Math.abs(pos.getZ() - sPos.getZ());
                    if (dist <= 24) {
                        int loss = dist / 4;
                        totalEffective += Math.max(0, src.outputFm - loss);
                        countedSources.add(sPos);
                    }
                }
            }
        }

        return totalEffective;
    }

    @Override
    public synchronized int getShaftCount() {
        return shafts.size();
    }

    @Override
    public void close() {
        shafts.clear();
        sources.clear();
        consumers.clear();
        networks.clear();
    }
}
