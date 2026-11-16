use std::collections::{HashMap, HashSet, VecDeque};
use crate::solver::{Solver, TickResults};
use crate::watchdog::Watchdog;

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash, PartialOrd, Ord)]
pub struct BlockPos {
    pub x: i32,
    pub y: i32,
    pub z: i32,
}

impl BlockPos {
    pub const fn new(x: i32, y: i32, z: i32) -> Self {
        Self { x, y, z }
    }

    /// Packs into 64-bit long using standard Minecraft coordinates bit-packing:
    /// ((x & 0x3FFFFFF) << 38) | ((z & 0x3FFFFFF) << 12) | (y & 0xFFF)
    pub fn to_packed_i64(&self) -> i64 {
        let x_part = (self.x as i64 & 0x3FF_FFFF) << 38;
        let z_part = (self.z as i64 & 0x3FF_FFFF) << 12;
        let y_part = self.y as i64 & 0xFFF;
        x_part | z_part | y_part
    }

    /// Unpacks from Minecraft 64-bit packed BlockPos
    pub fn from_packed_i64(packed: i64) -> Self {
        let x = (packed >> 38) as i32;
        let y = (packed << 52 >> 52) as i32;
        let z = (packed << 26 >> 38) as i32;
        Self { x, y, z }
    }

    /// Returns the 6 orthogonal neighbor positions
    pub fn neighbors(&self) -> [BlockPos; 6] {
        [
            BlockPos::new(self.x + 1, self.y, self.z),
            BlockPos::new(self.x - 1, self.y, self.z),
            BlockPos::new(self.x, self.y + 1, self.z),
            BlockPos::new(self.x, self.y - 1, self.z),
            BlockPos::new(self.x, self.y, self.z + 1),
            BlockPos::new(self.x, self.y, self.z - 1),
        ]
    }

    pub fn is_adjacent(&self, other: &BlockPos) -> bool {
        (self.x - other.x).abs() + (self.y - other.y).abs() + (self.z - other.z).abs() == 1
    }
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum DeviceType {
    Source,
    Sink,
}

#[derive(Debug, Clone)]
pub struct CableNode {
    pub pos: BlockPos,
    pub max_transfer: u32,
    pub capacity: u32,
    pub grid_id: u32,
}

#[derive(Debug, Clone)]
pub struct DeviceNode {
    pub pos: BlockPos,
    pub device_type: DeviceType,
    pub energy_amount: u32,
    pub rate_limit: u32,
    pub connected_grid_id: Option<u32>,
}

#[derive(Debug, Clone)]
pub struct Grid {
    pub id: u32,
    pub cables: HashSet<BlockPos>,
    pub devices: HashSet<BlockPos>,
}

impl Grid {
    pub fn new(id: u32) -> Self {
        Self {
            id,
            cables: HashSet::new(),
            devices: HashSet::new(),
        }
    }
}

#[derive(Debug, Default)]
pub struct EnergyNetworkManager {
    cables: HashMap<BlockPos, CableNode>,
    devices: HashMap<BlockPos, DeviceNode>,
    grids: HashMap<u32, Grid>,
    next_grid_id: u32,
    pub watchdog: Watchdog,
}

impl EnergyNetworkManager {
    pub fn new() -> Self {
        Self {
            cables: HashMap::new(),
            devices: HashMap::new(),
            grids: HashMap::new(),
            next_grid_id: 1,
            watchdog: Watchdog::default(),
        }
    }

    pub fn cable_count(&self) -> usize {
        self.cables.len()
    }

    pub fn grid_count(&self) -> usize {
        self.grids.len()
    }

    pub fn get_grid_id(&self, pos: &BlockPos) -> Option<u32> {
        self.cables.get(pos).map(|c| c.grid_id)
    }

    /// Returns all device positions connected to the grid containing `cable_pos`
    pub fn get_connected_devices(&self, cable_pos: &BlockPos) -> Vec<BlockPos> {
        if let Some(gid) = self.get_grid_id(cable_pos) {
            if let Some(grid) = self.grids.get(&gid) {
                return grid.devices.iter().copied().collect();
            }
        }
        Vec::new()
    }

    /// Adds a cable to the network, automatically handling grid creation or merging.
    pub fn add_cable(&mut self, pos: BlockPos, max_transfer: u32, capacity: u32) {
        if self.cables.contains_key(&pos) {
            return;
        }

        let neighbors = pos.neighbors();
        let mut adjacent_grid_ids: Vec<u32> = Vec::new();

        for n_pos in &neighbors {
            if let Some(cable) = self.cables.get(n_pos) {
                if !adjacent_grid_ids.contains(&cable.grid_id) {
                    adjacent_grid_ids.push(cable.grid_id);
                }
            }
        }

        let assigned_grid_id = if adjacent_grid_ids.is_empty() {
            // New isolated grid
            let gid = self.next_grid_id;
            self.next_grid_id += 1;
            let mut grid = Grid::new(gid);
            grid.cables.insert(pos);
            self.grids.insert(gid, grid);
            gid
        } else if adjacent_grid_ids.len() == 1 {
            // Join existing single grid
            let gid = adjacent_grid_ids[0];
            if let Some(grid) = self.grids.get_mut(&gid) {
                grid.cables.insert(pos);
            }
            gid
        } else {
            // Merge multiple grids into the first one
            let main_gid = adjacent_grid_ids[0];
            for &other_gid in &adjacent_grid_ids[1..] {
                if let Some(other_grid) = self.grids.remove(&other_gid) {
                    // Update cable references
                    for c_pos in &other_grid.cables {
                        if let Some(c) = self.cables.get_mut(c_pos) {
                            c.grid_id = main_gid;
                        }
                    }
                    // Update device references
                    for d_pos in &other_grid.devices {
                        if let Some(d) = self.devices.get_mut(d_pos) {
                            d.connected_grid_id = Some(main_gid);
                        }
                    }

                    if let Some(main_grid) = self.grids.get_mut(&main_gid) {
                        main_grid.cables.extend(other_grid.cables);
                        main_grid.devices.extend(other_grid.devices);
                    }
                }
            }

            if let Some(main_grid) = self.grids.get_mut(&main_gid) {
                main_grid.cables.insert(pos);
            }
            main_gid
        };

        // Insert new cable
        self.cables.insert(
            pos,
            CableNode {
                pos,
                max_transfer,
                capacity,
                grid_id: assigned_grid_id,
            },
        );

        // Check if any neighboring devices can now attach to this grid
        for n_pos in &neighbors {
            if let Some(dev) = self.devices.get_mut(n_pos) {
                if dev.connected_grid_id.is_none() {
                    dev.connected_grid_id = Some(assigned_grid_id);
                    if let Some(grid) = self.grids.get_mut(&assigned_grid_id) {
                        grid.devices.insert(*n_pos);
                    }
                }
            }
        }
    }

    /// Removes a cable, recalculating connected components and splitting grids if necessary.
    pub fn remove_cable(&mut self, pos: BlockPos) {
        let cable = match self.cables.remove(&pos) {
            Some(c) => c,
            None => return,
        };

        let old_grid_id = cable.grid_id;

        // Remove cable from its grid
        if let Some(grid) = self.grids.get_mut(&old_grid_id) {
            grid.cables.remove(&pos);
        }

        // If no cables left in grid, clean up grid and unbind devices
        let remaining_cables: Vec<BlockPos> = match self.grids.get(&old_grid_id) {
            Some(g) => g.cables.iter().copied().collect(),
            None => return,
        };

        if remaining_cables.is_empty() {
            if let Some(grid) = self.grids.remove(&old_grid_id) {
                for d_pos in grid.devices {
                    if let Some(dev) = self.devices.get_mut(&d_pos) {
                        dev.connected_grid_id = None;
                    }
                }
            }
            return;
        }

        // Check if the grid was split into multiple connected components
        let mut unvisited: HashSet<BlockPos> = remaining_cables.into_iter().collect();
        let mut components: Vec<HashSet<BlockPos>> = Vec::new();

        while !unvisited.is_empty() {
            let start = *unvisited.iter().next().unwrap();
            let mut component: HashSet<BlockPos> = HashSet::new();
            let mut queue = VecDeque::new();
            queue.push_back(start);
            unvisited.remove(&start);

            while let Some(current) = queue.pop_front() {
                component.insert(current);
                for n in current.neighbors() {
                    if unvisited.remove(&n) {
                        queue.push_back(n);
                    }
                }
            }
            components.push(component);
        }

        // If only 1 component, the grid did not split!
        if components.len() == 1 {
            return;
        }

        // Multiple components: first component keeps old_grid_id, others get new grid IDs
        let mut old_devices = HashSet::new();
        if let Some(old_grid) = self.grids.get_mut(&old_grid_id) {
            old_grid.cables = components[0].clone();
            old_devices = old_grid.devices.clone();
            old_grid.devices.clear();
        }

        for comp in components.iter().skip(1) {
            let new_gid = self.next_grid_id;
            self.next_grid_id += 1;

            let mut new_grid = Grid::new(new_gid);
            new_grid.cables = comp.clone();

            for c_pos in comp {
                if let Some(c) = self.cables.get_mut(c_pos) {
                    c.grid_id = new_gid;
                }
            }

            self.grids.insert(new_gid, new_grid);
        }

        // Reconnect all devices that were attached to the old grid
        for d_pos in old_devices {
            if let Some(dev) = self.devices.get_mut(&d_pos) {
                dev.connected_grid_id = None;
                for n in d_pos.neighbors() {
                    if let Some(c) = self.cables.get(&n) {
                        dev.connected_grid_id = Some(c.grid_id);
                        if let Some(g) = self.grids.get_mut(&c.grid_id) {
                            g.devices.insert(d_pos);
                        }
                        break;
                    }
                }
            }
        }
    }

    /// Registers a device (generator, battery, machine). Automatically links to adjacent cable grid if present.
    pub fn register_device(
        &mut self,
        pos: BlockPos,
        device_type: DeviceType,
        energy_amount: u32,
        rate_limit: u32,
    ) {
        let mut connected_gid = None;
        for n in pos.neighbors() {
            if let Some(c) = self.cables.get(&n) {
                connected_gid = Some(c.grid_id);
                break;
            }
        }

        if let Some(gid) = connected_gid {
            if let Some(grid) = self.grids.get_mut(&gid) {
                grid.devices.insert(pos);
            }
        }

        self.devices.insert(
            pos,
            DeviceNode {
                pos,
                device_type,
                energy_amount,
                rate_limit,
                connected_grid_id: connected_gid,
            },
        );
    }

    /// Updates dynamic energy availability or demand for a device prior to tick solving
    pub fn update_device(&mut self, pos: BlockPos, energy_amount: u32, rate_limit: u32) {
        if let Some(dev) = self.devices.get_mut(&pos) {
            dev.energy_amount = energy_amount;
            dev.rate_limit = rate_limit;
        }
    }

    /// Removes a device from the network
    pub fn remove_device(&mut self, pos: BlockPos) {
        if let Some(dev) = self.devices.remove(&pos) {
            if let Some(gid) = dev.connected_grid_id {
                if let Some(grid) = self.grids.get_mut(&gid) {
                    grid.devices.remove(&pos);
                }
            }
        }
    }

    /// Solves energy distribution for all grids across the entire dimension.
    pub fn solve_tick(&mut self) -> TickResults {
        let mut results = TickResults::new();

        let grid_ids: Vec<u32> = self.grids.keys().copied().collect();

        for gid in grid_ids {
            if self.watchdog.check_network(gid) {
                // Circuit breaker is active: isolate this grid to prevent server freeze/crash
                continue;
            }

            let grid = match self.grids.get(&gid) {
                Some(g) => g,
                None => continue,
            };

            let mut sources: Vec<(BlockPos, &DeviceNode)> = Vec::new();
            let mut sinks: Vec<(BlockPos, &DeviceNode)> = Vec::new();

            for d_pos in &grid.devices {
                if let Some(dev) = self.devices.get(d_pos) {
                    match dev.device_type {
                        DeviceType::Source => sources.push((*d_pos, dev)),
                        DeviceType::Sink => sinks.push((*d_pos, dev)),
                    }
                }
            }

            let start = std::time::Instant::now();
            Solver::solve_component(&sources, &sinks, &mut results);
            let elapsed = start.elapsed();

            if elapsed > std::time::Duration::from_millis(20) {
                self.watchdog.trip_network(
                    gid,
                    &format!("Energy grid {} solve_component took {:?}", gid, elapsed),
                );
            }
        }

        results
    }
}
