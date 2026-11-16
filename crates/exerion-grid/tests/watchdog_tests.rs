use std::thread::sleep;
use std::time::Duration;
use exerion_grid::watchdog::Watchdog;

#[test]
fn test_watchdog_circuit_breaker_trip_and_cooldown() {
    let mut watchdog = Watchdog::new(Duration::from_millis(100), Duration::from_millis(10));

    assert!(!watchdog.check_network(1));

    // Manually trip network 1
    watchdog.trip_network(1, "Simulated overload");
    assert!(watchdog.check_network(1));
    assert_eq!(watchdog.tripped_count(), 1);

    // Wait for cooldown to expire (auto-heal)
    sleep(Duration::from_millis(150));
    assert!(!watchdog.check_network(1));
    assert_eq!(watchdog.tripped_count(), 0);
}

#[test]
fn test_watchdog_record_execution_trip_on_overtime() {
    let mut watchdog = Watchdog::new(Duration::from_millis(200), Duration::from_millis(15));

    // Normal fast operation
    let res = watchdog.record_execution(2, "fast_op", || 42);
    assert_eq!(res, Some(42));
    assert!(!watchdog.check_network(2));

    // Slow operation that exceeds 15ms
    let slow_res = watchdog.record_execution(2, "slow_op", || {
        sleep(Duration::from_millis(25));
        100
    });
    assert_eq!(slow_res, Some(100));

    // Next call should be rejected because circuit breaker tripped
    assert!(watchdog.check_network(2));
    let blocked_res = watchdog.record_execution(2, "blocked_op", || 999);
    assert_eq!(blocked_res, None);
}
