use std::collections::HashMap;
use std::time::{Duration, Instant};

#[derive(Debug, Clone)]
pub struct CircuitBreaker {
    pub id: u32,
    pub is_tripped: bool,
    pub trip_count: u32,
    pub tripped_at: Option<Instant>,
    pub cooldown: Duration,
    pub last_reason: String,
}

impl CircuitBreaker {
    pub fn new(id: u32, cooldown: Duration) -> Self {
        Self {
            id,
            is_tripped: false,
            trip_count: 0,
            tripped_at: None,
            cooldown,
            last_reason: String::new(),
        }
    }

    pub fn trip(&mut self, reason: &str) {
        self.is_tripped = true;
        self.trip_count += 1;
        self.tripped_at = Some(Instant::now());
        self.last_reason = reason.to_string();
    }

    pub fn is_active(&mut self) -> bool {
        if !self.is_tripped {
            return false;
        }

        if let Some(tripped_time) = self.tripped_at {
            if tripped_time.elapsed() >= self.cooldown {
                // Auto-reset after cooldown expires to heal the network
                self.is_tripped = false;
                self.tripped_at = None;
                return false;
            }
        }

        true
    }

    pub fn reset(&mut self) {
        self.is_tripped = false;
        self.tripped_at = None;
        self.last_reason.clear();
    }
}

#[derive(Debug)]
pub struct Watchdog {
    breakers: HashMap<u32, CircuitBreaker>,
    default_cooldown: Duration,
    max_tick_duration: Duration,
    last_op_duration: Duration,
}

impl Default for Watchdog {
    fn default() -> Self {
        Self::new(Duration::from_secs(5), Duration::from_millis(20))
    }
}

impl Watchdog {
    pub fn new(default_cooldown: Duration, max_tick_duration: Duration) -> Self {
        Self {
            breakers: HashMap::new(),
            default_cooldown,
            max_tick_duration,
            last_op_duration: Duration::ZERO,
        }
    }

    pub fn check_network(&mut self, id: u32) -> bool {
        if let Some(breaker) = self.breakers.get_mut(&id) {
            breaker.is_active()
        } else {
            false
        }
    }

    pub fn trip_network(&mut self, id: u32, reason: &str) {
        let cooldown = self.default_cooldown;
        let breaker = self.breakers.entry(id).or_insert_with(|| CircuitBreaker::new(id, cooldown));
        breaker.trip(reason);
    }

    pub fn reset_network(&mut self, id: u32) {
        if let Some(breaker) = self.breakers.get_mut(&id) {
            breaker.reset();
        }
    }

    pub fn reset_all(&mut self) {
        self.breakers.clear();
    }

    pub fn record_execution<F, R>(&mut self, id: u32, op_name: &str, f: F) -> Option<R>
    where
        F: FnOnce() -> R,
    {
        if self.check_network(id) {
            // Circuit is tripped, isolate network to prevent server lag/crash
            return None;
        }

        let start = Instant::now();
        let result = f();
        let elapsed = start.elapsed();
        self.last_op_duration = elapsed;

        if elapsed > self.max_tick_duration {
            self.trip_network(
                id,
                &format!("Operation '{}' exceeded max allowed time: {:?}", op_name, elapsed),
            );
        }

        Some(result)
    }

    pub fn last_operation_duration_micros(&self) -> u64 {
        self.last_op_duration.as_micros() as u64
    }

    pub fn tripped_count(&mut self) -> usize {
        let mut count = 0;
        for b in self.breakers.values_mut() {
            if b.is_active() {
                count += 1;
            }
        }
        count
    }
}
