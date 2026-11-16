use exerion_grid::grid::{BlockPos, DeviceType, EnergyNetworkManager};

#[test]
fn test_add_single_cable() {
    let mut manager = EnergyNetworkManager::new();
    let pos = BlockPos::new(0, 64, 0);
    manager.add_cable(pos, 100, 500);

    assert_eq!(manager.cable_count(), 1);
    assert_eq!(manager.grid_count(), 1);
}

#[test]
fn test_cables_merge_into_single_grid() {
    let mut manager = EnergyNetworkManager::new();
    let pos1 = BlockPos::new(0, 64, 0);
    let pos2 = BlockPos::new(1, 64, 0);
    let pos3 = BlockPos::new(2, 64, 0);

    manager.add_cable(pos1, 100, 500);
    manager.add_cable(pos2, 100, 500);
    manager.add_cable(pos3, 100, 500);

    assert_eq!(manager.cable_count(), 3);
    assert_eq!(manager.grid_count(), 1);
}

#[test]
fn test_cable_removal_splits_grid() {
    let mut manager = EnergyNetworkManager::new();
    let pos1 = BlockPos::new(0, 64, 0);
    let pos2 = BlockPos::new(1, 64, 0);
    let pos3 = BlockPos::new(2, 64, 0);

    manager.add_cable(pos1, 100, 500);
    manager.add_cable(pos2, 100, 500);
    manager.add_cable(pos3, 100, 500);
    assert_eq!(manager.grid_count(), 1);

    // Remove middle cable
    manager.remove_cable(pos2);
    assert_eq!(manager.cable_count(), 2);
    assert_eq!(manager.grid_count(), 2);
}

#[test]
fn test_energy_distribution_single_source_single_sink() {
    let mut manager = EnergyNetworkManager::new();
    let cable_pos = BlockPos::new(0, 64, 0);
    manager.add_cable(cable_pos, 1000, 5000);

    let source_pos = BlockPos::new(1, 64, 0);
    let sink_pos = BlockPos::new(-1, 64, 0);

    // Source offering 100 EU, max rate 100
    manager.register_device(source_pos, DeviceType::Source, 100, 100);
    // Sink demanding 60 EU, max rate 60
    manager.register_device(sink_pos, DeviceType::Sink, 60, 60);

    let tick_results = manager.solve_tick();

    // Verify source drained 60 EU
    assert_eq!(tick_results.get_drained(&source_pos), 60);
    // Verify sink received 60 EU
    assert_eq!(tick_results.get_received(&sink_pos), 60);
}

#[test]
fn test_energy_distribution_fair_share() {
    let mut manager = EnergyNetworkManager::new();
    let cable_pos = BlockPos::new(0, 64, 0);
    manager.add_cable(cable_pos, 1000, 5000);

    let source_pos = BlockPos::new(0, 65, 0);
    let sink1_pos = BlockPos::new(1, 64, 0);
    let sink2_pos = BlockPos::new(-1, 64, 0);

    // Source offers 100 EU
    manager.register_device(source_pos, DeviceType::Source, 100, 100);
    // Two sinks each requesting 100 EU (total 200 EU)
    manager.register_device(sink1_pos, DeviceType::Sink, 100, 100);
    manager.register_device(sink2_pos, DeviceType::Sink, 100, 100);

    let tick_results = manager.solve_tick();

    // With 100 available and 200 demand, each should receive 50 EU
    assert_eq!(tick_results.get_received(&sink1_pos), 50);
    assert_eq!(tick_results.get_received(&sink2_pos), 50);
    assert_eq!(tick_results.get_drained(&source_pos), 100);
}

#[test]
fn test_disconnected_grids_isolation() {
    let mut manager = EnergyNetworkManager::new();
    
    // Grid 1 at x=0
    let cable1 = BlockPos::new(0, 64, 0);
    manager.add_cable(cable1, 500, 1000);
    let source1 = BlockPos::new(0, 65, 0);
    manager.register_device(source1, DeviceType::Source, 100, 100);

    // Grid 2 at x=100 (isolated)
    let cable2 = BlockPos::new(100, 64, 0);
    manager.add_cable(cable2, 500, 1000);
    let sink2 = BlockPos::new(100, 65, 0);
    manager.register_device(sink2, DeviceType::Sink, 100, 100);

    let tick_results = manager.solve_tick();

    // Sink in isolated grid should get 0 EU because there's no source in its grid
    assert_eq!(tick_results.get_received(&sink2), 0);
    assert_eq!(tick_results.get_drained(&source1), 0);
}

#[test]
fn test_scale_benchmark_1000_cables() {
    let mut manager = EnergyNetworkManager::new();
    
    // Create a line of 1000 cables
    for i in 0..1000 {
        manager.add_cable(BlockPos::new(i, 64, 0), 1000, 5000);
    }
    assert_eq!(manager.cable_count(), 1000);
    assert_eq!(manager.grid_count(), 1);

    // Attach 50 sources along the line
    for i in 0..50 {
        let pos = BlockPos::new(i * 20, 65, 0);
        manager.register_device(pos, DeviceType::Source, 100, 100);
    }

    // Attach 50 sinks along the line
    for i in 0..50 {
        let pos = BlockPos::new(i * 20 + 10, 65, 0);
        manager.register_device(pos, DeviceType::Sink, 50, 50);
    }

    let start = std::time::Instant::now();
    let tick_results = manager.solve_tick();
    let elapsed = start.elapsed();

    println!("⚡ 1000 cables + 100 devices tick latency: {:?}", elapsed);
    assert!(elapsed.as_micros() < 500, "Tick must execute in less than 500 microseconds!");

    // Verify 50 sinks * 50 = 2500 EU distributed
    let total_received: u32 = (0..50)
        .map(|i| tick_results.get_received(&BlockPos::new(i * 20 + 10, 65, 0)))
        .sum();
    assert_eq!(total_received, 2500);
}

#[test]
fn test_get_connected_devices_across_grid() {
    let mut manager = EnergyNetworkManager::new();

    let c1 = BlockPos::new(0, 64, 0);
    let c2 = BlockPos::new(1, 64, 0);
    manager.add_cable(c1, 1000, 5000);
    manager.add_cable(c2, 1000, 5000);

    let dev_pos = BlockPos::new(2, 64, 0);
    manager.register_device(dev_pos, DeviceType::Sink, 100, 100);

    // Query connected devices from the opposite end of the grid (c1)
    let devices = manager.get_connected_devices(&c1);
    assert_eq!(devices.len(), 1);
    assert_eq!(devices[0], dev_pos);
}
