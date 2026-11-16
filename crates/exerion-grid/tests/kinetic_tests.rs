use exerion_grid::grid::BlockPos;
use exerion_grid::kinetic::{Axis, KineticNetworkManager};

#[test]
fn test_add_shaft() {
    let mut manager = KineticNetworkManager::new();
    let pos = BlockPos::new(0, 64, 0);
    manager.add_shaft(pos, Axis::X);

    assert_eq!(manager.shaft_count(), 1);
    assert_eq!(manager.network_count(), 1);
}

#[test]
fn test_shafts_merge_along_axis() {
    let mut manager = KineticNetworkManager::new();
    let s1 = BlockPos::new(0, 64, 0);
    let s2 = BlockPos::new(1, 64, 0);
    let s3 = BlockPos::new(2, 64, 0);

    manager.add_shaft(s1, Axis::X);
    manager.add_shaft(s2, Axis::X);
    manager.add_shaft(s3, Axis::X);

    assert_eq!(manager.shaft_count(), 3);
    assert_eq!(manager.network_count(), 1);

    // Shaft on different axis should not merge
    let s_diff = BlockPos::new(0, 65, 0);
    manager.add_shaft(s_diff, Axis::Y);
    assert_eq!(manager.shaft_count(), 4);
    assert_eq!(manager.network_count(), 2);
}

#[test]
fn test_kinetic_source_and_consumer() {
    let mut manager = KineticNetworkManager::new();

    let source_pos = BlockPos::new(0, 64, 0);
    let s1 = BlockPos::new(1, 64, 0);
    let s2 = BlockPos::new(2, 64, 0);
    let consumer_pos = BlockPos::new(3, 64, 0);

    manager.add_shaft(s1, Axis::X);
    manager.add_shaft(s2, Axis::X);

    manager.register_source(source_pos, 40, true);
    manager.register_consumer(consumer_pos, 20);

    // Both shafts should be spinning
    assert!(manager.is_spinning(&s1));
    assert!(manager.is_spinning(&s2));

    // Consumer should receive FM from source with distance loss (dist = 3, loss = 3 / 4 = 0)
    let available = manager.get_available_fm(&consumer_pos);
    assert_eq!(available, 40);

    // Deactivate source
    manager.update_source(source_pos, 40, false);
    assert!(!manager.is_spinning(&s1));
    assert_eq!(manager.get_available_fm(&consumer_pos), 0);
}

#[test]
fn test_shaft_removal_splits_kinetic_network() {
    let mut manager = KineticNetworkManager::new();
    let s1 = BlockPos::new(0, 64, 0);
    let s2 = BlockPos::new(1, 64, 0);
    let s3 = BlockPos::new(2, 64, 0);

    manager.add_shaft(s1, Axis::X);
    manager.add_shaft(s2, Axis::X);
    manager.add_shaft(s3, Axis::X);
    assert_eq!(manager.network_count(), 1);

    // Remove middle shaft
    manager.remove_shaft(s2);
    assert_eq!(manager.shaft_count(), 2);
    assert_eq!(manager.network_count(), 2);
}

#[test]
fn test_scale_benchmark_1000_shafts() {
    let mut manager = KineticNetworkManager::new();

    for i in 0..1000 {
        manager.add_shaft(BlockPos::new(i, 64, 0), Axis::X);
    }
    assert_eq!(manager.shaft_count(), 1000);
    assert_eq!(manager.network_count(), 1);

    manager.register_source(BlockPos::new(-1, 64, 0), 100, true);
    manager.register_consumer(BlockPos::new(1000, 64, 0), 20);

    let start = std::time::Instant::now();
    let spinning = manager.is_spinning(&BlockPos::new(500, 64, 0));
    let elapsed = start.elapsed();

    assert!(spinning);
    println!("⚡ 1000 shafts is_spinning lookup: {:?}", elapsed);
    assert!(elapsed.as_micros() < 50, "Lookup must take less than 50 µs");
}

#[test]
fn test_direct_source_to_consumer() {
    let mut manager = KineticNetworkManager::new();
    let source_pos = BlockPos::new(10, 64, 10);
    let consumer_pos = BlockPos::new(11, 64, 10); // adjacent, no shafts

    manager.register_source(source_pos, 25, true);
    assert!(manager.is_spinning(&source_pos));

    let available = manager.get_available_fm(&consumer_pos);
    assert_eq!(available, 25);

    manager.update_source(source_pos, 25, false);
    assert!(!manager.is_spinning(&source_pos));
    assert_eq!(manager.get_available_fm(&consumer_pos), 0);
}
