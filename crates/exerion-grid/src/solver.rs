use std::collections::HashMap;
use crate::grid::{BlockPos, DeviceNode};

#[derive(Debug, Default, Clone)]
pub struct TickResults {
    pub drained: HashMap<BlockPos, u32>,
    pub received: HashMap<BlockPos, u32>,
}

impl TickResults {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn get_drained(&self, pos: &BlockPos) -> u32 {
        *self.drained.get(pos).unwrap_or(&0)
    }

    pub fn get_received(&self, pos: &BlockPos) -> u32 {
        *self.received.get(pos).unwrap_or(&0)
    }

    pub fn record_drain(&mut self, pos: BlockPos, amount: u32) {
        *self.drained.entry(pos).or_insert(0) += amount;
    }

    pub fn record_receive(&mut self, pos: BlockPos, amount: u32) {
        *self.received.entry(pos).or_insert(0) += amount;
    }
}

pub struct Solver;

impl Solver {
    /// Solves energy distribution for a single connected component (Grid).
    /// Ensures zero energy duplication or loss.
    pub fn solve_component(
        sources: &[(BlockPos, &DeviceNode)],
        sinks: &[(BlockPos, &DeviceNode)],
        results: &mut TickResults,
    ) {
        if sources.is_empty() || sinks.is_empty() {
            return;
        }

        // Calculate total available energy from sources
        let mut total_available: u64 = 0;
        let mut source_caps: Vec<(BlockPos, u32)> = Vec::with_capacity(sources.len());
        for (pos, node) in sources {
            let cap = node.energy_amount.min(node.rate_limit);
            if cap > 0 {
                total_available += cap as u64;
                source_caps.push((*pos, cap));
            }
        }

        // Calculate total demand from sinks
        let mut total_demand: u64 = 0;
        let mut sink_demands: Vec<(BlockPos, u32)> = Vec::with_capacity(sinks.len());
        for (pos, node) in sinks {
            let dem = node.energy_amount.min(node.rate_limit);
            if dem > 0 {
                total_demand += dem as u64;
                sink_demands.push((*pos, dem));
            }
        }

        if total_available == 0 || total_demand == 0 {
            return;
        }

        let total_transfer = total_available.min(total_demand);

        // 1. Distribute energy to sinks
        if total_available >= total_demand {
            // All sinks get 100% of their demand
            for (pos, dem) in sink_demands {
                results.record_receive(pos, dem);
            }
        } else {
            // Sinks get fair-share proportional to their demand
            let mut distributed: u64 = 0;
            let count = sink_demands.len();
            for (idx, (pos, dem)) in sink_demands.iter().enumerate() {
                let share = ((*dem as u64) * total_transfer) / total_demand;
                let mut amount = share as u32;
                distributed += share;

                // On the last sink, assign any rounding remainder
                if idx == count - 1 && distributed < total_transfer {
                    amount += (total_transfer - distributed) as u32;
                }
                results.record_receive(*pos, amount);
            }
        }

        // 2. Drain energy from sources
        if total_available <= total_demand {
            // All sources are fully drained up to their available capacity
            for (pos, cap) in source_caps {
                results.record_drain(pos, cap);
            }
        } else {
            // Sources drain proportionally
            let mut total_drained: u64 = 0;
            let count = source_caps.len();
            for (idx, (pos, cap)) in source_caps.iter().enumerate() {
                let share = ((*cap as u64) * total_transfer) / total_available;
                let mut amount = share as u32;
                total_drained += share;

                // On the last source, assign any rounding remainder
                if idx == count - 1 && total_drained < total_transfer {
                    amount += (total_transfer - total_drained) as u32;
                }
                results.record_drain(*pos, amount);
            }
        }
    }
}
