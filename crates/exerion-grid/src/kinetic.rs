use std::collections::{HashMap, HashSet, VecDeque};
use crate::grid::BlockPos;

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum Axis {
    X,
    Y,
    Z,
}

impl Axis {
    pub fn from_i32(val: i32) -> Self {
        match val {
            0 => Axis::X,
            1 => Axis::Y,
            _ => Axis::Z,
        }
    }

    pub fn to_i32(&self) -> i32 {
        match self {
            Axis::X => 0,
            Axis::Y => 1,
            Axis::Z => 2,
        }
    }

    pub fn neighbors_along_axis(&self, pos: &BlockPos) -> [BlockPos; 2] {
        match self {
            Axis::X => [
                BlockPos::new(pos.x + 1, pos.y, pos.z),
                BlockPos::new(pos.x - 1, pos.y, pos.z),
            ],
            Axis::Y => [
                BlockPos::new(pos.x, pos.y + 1, pos.z),
                BlockPos::new(pos.x, pos.y - 1, pos.z),
            ],
            Axis::Z => [
                BlockPos::new(pos.x, pos.y, pos.z + 1),
                BlockPos::new(pos.x, pos.y, pos.z - 1),
            ],
        }
    }
}

#[derive(Debug, Clone)]
pub struct ShaftNode {
    pub pos: BlockPos,
    pub axis: Axis,
    pub network_id: u32,
}

#[derive(Debug, Clone)]
pub struct KineticSourceNode {
    pub pos: BlockPos,
    pub output_fm: u32,
    pub is_generating: bool,
    pub connected_network_id: Option<u32>,
}

#[derive(Debug, Clone)]
pub struct KineticConsumerNode {
    pub pos: BlockPos,
    pub required_fm: u32,
    pub connected_network_id: Option<u32>,
}

#[derive(Debug, Clone)]
pub struct KineticNetwork {
    pub id: u32,
    pub axis: Axis,
    pub shafts: HashSet<BlockPos>,
    pub sources: HashSet<BlockPos>,
    pub consumers: HashSet<BlockPos>,
    pub is_spinning: bool,
    pub total_generated_fm: u32,
}

impl KineticNetwork {
    pub fn new(id: u32, axis: Axis) -> Self {
        Self {
            id,
            axis,
            shafts: HashSet::new(),
            sources: HashSet::new(),
            consumers: HashSet::new(),
            is_spinning: false,
            total_generated_fm: 0,
        }
    }
}

#[derive(Debug, Default)]
pub struct KineticNetworkManager {
    shafts: HashMap<BlockPos, ShaftNode>,
    sources: HashMap<BlockPos, KineticSourceNode>,
    consumers: HashMap<BlockPos, KineticConsumerNode>,
    networks: HashMap<u32, KineticNetwork>,
    next_network_id: u32,
}

impl KineticNetworkManager {
    pub fn new() -> Self {
        Self {
            shafts: HashMap::new(),
            sources: HashMap::new(),
            consumers: HashMap::new(),
            networks: HashMap::new(),
            next_network_id: 1,
        }
    }

    pub fn shaft_count(&self) -> usize {
        self.shafts.len()
    }

    pub fn network_count(&self) -> usize {
        self.networks.len()
    }

    fn recompute_network(&mut self, net_id: u32) {
        if let Some(net) = self.networks.get_mut(&net_id) {
            let mut spinning = false;
            let mut total_fm: u32 = 0;

            for s_pos in &net.sources {
                if let Some(source) = self.sources.get(s_pos) {
                    if source.is_generating {
                        spinning = true;
                        total_fm = total_fm.saturating_add(source.output_fm);
                    }
                }
            }

            net.is_spinning = spinning;
            net.total_generated_fm = total_fm;
        }
    }

    pub fn add_shaft(&mut self, pos: BlockPos, axis: Axis) {
        if self.shafts.contains_key(&pos) {
            return;
        }

        let neighbors = axis.neighbors_along_axis(&pos);
        let mut adjacent_net_ids: Vec<u32> = Vec::new();

        for n_pos in &neighbors {
            if let Some(neighbor_shaft) = self.shafts.get(n_pos) {
                if neighbor_shaft.axis == axis && !adjacent_net_ids.contains(&neighbor_shaft.network_id) {
                    adjacent_net_ids.push(neighbor_shaft.network_id);
                }
            }
        }

        let assigned_id = if adjacent_net_ids.is_empty() {
            let nid = self.next_network_id;
            self.next_network_id += 1;
            let mut net = KineticNetwork::new(nid, axis);
            net.shafts.insert(pos);
            self.networks.insert(nid, net);
            nid
        } else if adjacent_net_ids.len() == 1 {
            let nid = adjacent_net_ids[0];
            if let Some(net) = self.networks.get_mut(&nid) {
                net.shafts.insert(pos);
            }
            nid
        } else {
            let main_nid = adjacent_net_ids[0];
            for &other_nid in &adjacent_net_ids[1..] {
                if let Some(other_net) = self.networks.remove(&other_nid) {
                    for s_pos in &other_net.shafts {
                        if let Some(s) = self.shafts.get_mut(s_pos) {
                            s.network_id = main_nid;
                        }
                    }
                    for src_pos in &other_net.sources {
                        if let Some(src) = self.sources.get_mut(src_pos) {
                            src.connected_network_id = Some(main_nid);
                        }
                    }
                    for con_pos in &other_net.consumers {
                        if let Some(con) = self.consumers.get_mut(con_pos) {
                            con.connected_network_id = Some(main_nid);
                        }
                    }
                    if let Some(main_net) = self.networks.get_mut(&main_nid) {
                        main_net.shafts.extend(other_net.shafts);
                        main_net.sources.extend(other_net.sources);
                        main_net.consumers.extend(other_net.consumers);
                    }
                }
            }
            if let Some(main_net) = self.networks.get_mut(&main_nid) {
                main_net.shafts.insert(pos);
            }
            main_nid
        };

        self.shafts.insert(
            pos,
            ShaftNode {
                pos,
                axis,
                network_id: assigned_id,
            },
        );

        // Check if any neighboring sources or consumers can attach to this network
        for n in pos.neighbors() {
            if let Some(src) = self.sources.get_mut(&n) {
                if src.connected_network_id.is_none() {
                    src.connected_network_id = Some(assigned_id);
                    if let Some(net) = self.networks.get_mut(&assigned_id) {
                        net.sources.insert(n);
                    }
                }
            }
            if let Some(con) = self.consumers.get_mut(&n) {
                if con.connected_network_id.is_none() {
                    con.connected_network_id = Some(assigned_id);
                    if let Some(net) = self.networks.get_mut(&assigned_id) {
                        net.consumers.insert(n);
                    }
                }
            }
        }

        self.recompute_network(assigned_id);
    }

    pub fn remove_shaft(&mut self, pos: BlockPos) {
        let shaft = match self.shafts.remove(&pos) {
            Some(s) => s,
            None => return,
        };

        let old_nid = shaft.network_id;
        let axis = shaft.axis;

        if let Some(net) = self.networks.get_mut(&old_nid) {
            net.shafts.remove(&pos);
        }

        let remaining: Vec<BlockPos> = match self.networks.get(&old_nid) {
            Some(net) => net.shafts.iter().copied().collect(),
            None => return,
        };

        if remaining.is_empty() {
            if let Some(net) = self.networks.remove(&old_nid) {
                for s_pos in net.sources {
                    if let Some(src) = self.sources.get_mut(&s_pos) {
                        src.connected_network_id = None;
                    }
                }
                for c_pos in net.consumers {
                    if let Some(con) = self.consumers.get_mut(&c_pos) {
                        con.connected_network_id = None;
                    }
                }
            }
            return;
        }

        // BFS component detection along shaft axis
        let mut unvisited: HashSet<BlockPos> = remaining.into_iter().collect();
        let mut components: Vec<HashSet<BlockPos>> = Vec::new();

        while !unvisited.is_empty() {
            let start = *unvisited.iter().next().unwrap();
            let mut comp = HashSet::new();
            let mut queue = VecDeque::new();
            queue.push_back(start);
            unvisited.remove(&start);

            while let Some(cur) = queue.pop_front() {
                comp.insert(cur);
                for n in axis.neighbors_along_axis(&cur) {
                    if unvisited.remove(&n) {
                        queue.push_back(n);
                    }
                }
            }
            components.push(comp);
        }

        if components.len() == 1 {
            self.recompute_network(old_nid);
            return;
        }

        let mut old_sources = HashSet::new();
        let mut old_consumers = HashSet::new();

        if let Some(net) = self.networks.get_mut(&old_nid) {
            net.shafts = components[0].clone();
            old_sources = net.sources.clone();
            old_consumers = net.consumers.clone();
            net.sources.clear();
            net.consumers.clear();
        }

        for comp in components.iter().skip(1) {
            let new_nid = self.next_network_id;
            self.next_network_id += 1;
            let mut net = KineticNetwork::new(new_nid, axis);
            net.shafts = comp.clone();
            for s_pos in comp {
                if let Some(s) = self.shafts.get_mut(s_pos) {
                    s.network_id = new_nid;
                }
            }
            self.networks.insert(new_nid, net);
        }

        // Reconnect sources
        for s_pos in old_sources {
            if let Some(src) = self.sources.get_mut(&s_pos) {
                src.connected_network_id = None;
                for n in s_pos.neighbors() {
                    if let Some(sh) = self.shafts.get(&n) {
                        src.connected_network_id = Some(sh.network_id);
                        if let Some(net) = self.networks.get_mut(&sh.network_id) {
                            net.sources.insert(s_pos);
                        }
                        break;
                    }
                }
            }
        }

        // Reconnect consumers
        for c_pos in old_consumers {
            if let Some(con) = self.consumers.get_mut(&c_pos) {
                con.connected_network_id = None;
                for n in c_pos.neighbors() {
                    if let Some(sh) = self.shafts.get(&n) {
                        con.connected_network_id = Some(sh.network_id);
                        if let Some(net) = self.networks.get_mut(&sh.network_id) {
                            net.consumers.insert(c_pos);
                        }
                        break;
                    }
                }
            }
        }

        self.recompute_network(old_nid);
        for comp in components.iter().skip(1) {
            if let Some(first_pos) = comp.iter().next() {
                if let Some(sh) = self.shafts.get(first_pos) {
                    self.recompute_network(sh.network_id);
                }
            }
        }
    }

    pub fn register_source(&mut self, pos: BlockPos, output_fm: u32, is_generating: bool) {
        let mut connected_nid = None;
        for n in pos.neighbors() {
            if let Some(sh) = self.shafts.get(&n) {
                connected_nid = Some(sh.network_id);
                break;
            }
        }

        if let Some(nid) = connected_nid {
            if let Some(net) = self.networks.get_mut(&nid) {
                net.sources.insert(pos);
            }
        }

        self.sources.insert(
            pos,
            KineticSourceNode {
                pos,
                output_fm,
                is_generating,
                connected_network_id: connected_nid,
            },
        );

        if let Some(nid) = connected_nid {
            self.recompute_network(nid);
        }
    }

    pub fn update_source(&mut self, pos: BlockPos, output_fm: u32, is_generating: bool) {
        let mut nid_to_update = None;
        if let Some(src) = self.sources.get_mut(&pos) {
            src.output_fm = output_fm;
            src.is_generating = is_generating;
            nid_to_update = src.connected_network_id;
        }

        if let Some(nid) = nid_to_update {
            self.recompute_network(nid);
        }
    }

    pub fn remove_source(&mut self, pos: BlockPos) {
        if let Some(src) = self.sources.remove(&pos) {
            if let Some(nid) = src.connected_network_id {
                if let Some(net) = self.networks.get_mut(&nid) {
                    net.sources.remove(&pos);
                }
                self.recompute_network(nid);
            }
        }
    }

    pub fn register_consumer(&mut self, pos: BlockPos, required_fm: u32) {
        let mut connected_nid = None;
        for n in pos.neighbors() {
            if let Some(sh) = self.shafts.get(&n) {
                connected_nid = Some(sh.network_id);
                break;
            }
        }

        if let Some(nid) = connected_nid {
            if let Some(net) = self.networks.get_mut(&nid) {
                net.consumers.insert(pos);
            }
        }

        self.consumers.insert(
            pos,
            KineticConsumerNode {
                pos,
                required_fm,
                connected_network_id: connected_nid,
            },
        );
    }

    pub fn remove_consumer(&mut self, pos: BlockPos) {
        if let Some(con) = self.consumers.remove(&pos) {
            if let Some(nid) = con.connected_network_id {
                if let Some(net) = self.networks.get_mut(&nid) {
                    net.consumers.remove(&pos);
                }
            }
        }
    }

    /// O(1) instantaneous check whether a shaft or source is spinning
    pub fn is_spinning(&self, pos: &BlockPos) -> bool {
        if let Some(sh) = self.shafts.get(pos) {
            if let Some(net) = self.networks.get(&sh.network_id) {
                return net.is_spinning;
            }
        }
        if let Some(src) = self.sources.get(pos) {
            return src.is_generating;
        }
        false
    }

    /// O(1) lookup of available FM reaching a consumer machine with distance loss
    pub fn get_available_fm(&self, consumer_pos: &BlockPos) -> u32 {
        let mut total_effective: u32 = 0;
        let mut counted_sources: HashSet<BlockPos> = HashSet::new();

        // 1. Direct neighbor sources
        for n in consumer_pos.neighbors() {
            if let Some(src) = self.sources.get(&n) {
                if src.is_generating && src.output_fm > 0 {
                    total_effective = total_effective.saturating_add(src.output_fm);
                    counted_sources.insert(n);
                }
            }
        }

        // 2. Shaft-connected sources
        let mut nids: Vec<u32> = Vec::new();
        if let Some(con) = self.consumers.get(consumer_pos) {
            if let Some(nid) = con.connected_network_id {
                nids.push(nid);
            }
        }
        for n in consumer_pos.neighbors() {
            if let Some(sh) = self.shafts.get(&n) {
                if !nids.contains(&sh.network_id) {
                    nids.push(sh.network_id);
                }
            }
        }

        for nid in nids {
            if let Some(net) = self.networks.get(&nid) {
                if net.is_spinning && net.total_generated_fm > 0 {
                    for s_pos in &net.sources {
                        if counted_sources.contains(s_pos) {
                            continue;
                        }
                        if let Some(src) = self.sources.get(s_pos) {
                            if src.is_generating && src.output_fm > 0 {
                                let dist = (consumer_pos.x - s_pos.x).abs()
                                    + (consumer_pos.y - s_pos.y).abs()
                                    + (consumer_pos.z - s_pos.z).abs();

                                if dist <= 24 {
                                    let loss = (dist / 4) as u32;
                                    let effective = src.output_fm.saturating_sub(loss);
                                    total_effective = total_effective.saturating_add(effective);
                                    counted_sources.insert(*s_pos);
                                }
                            }
                        }
                    }
                }
            }
        }

        total_effective
    }
}
