use jni::objects::JClass;
use jni::sys::{jboolean, jint, jlong, jlongArray, JNI_TRUE};
use jni::JNIEnv;
use std::panic::catch_unwind;

use crate::grid::{BlockPos, DeviceType, EnergyNetworkManager};
use crate::kinetic::{Axis, KineticNetworkManager};

#[no_mangle]
pub extern "system" fn Java_net_exerion_energy_RustGridEngine_createNetwork(
    _env: JNIEnv,
    _class: JClass,
) -> jlong {
    let result = catch_unwind(|| {
        let manager = Box::new(EnergyNetworkManager::new());
        Box::into_raw(manager) as jlong
    });
    result.unwrap_or(0)
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_energy_RustGridEngine_destroyNetwork(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let _ = Box::from_raw(handle as *mut EnergyNetworkManager);
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_energy_RustGridEngine_addCable(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
    max_transfer: jint,
    capacity: jint,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut EnergyNetworkManager);
        let pos = BlockPos::new(x, y, z);
        manager.add_cable(pos, max_transfer.max(0) as u32, capacity.max(0) as u32);
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_energy_RustGridEngine_removeCable(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut EnergyNetworkManager);
        manager.remove_cable(BlockPos::new(x, y, z));
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_energy_RustGridEngine_registerDevice(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
    is_source: jboolean,
    amount: jint,
    rate_limit: jint,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut EnergyNetworkManager);
        let dev_type = if is_source == JNI_TRUE {
            DeviceType::Source
        } else {
            DeviceType::Sink
        };
        manager.register_device(
            BlockPos::new(x, y, z),
            dev_type,
            amount.max(0) as u32,
            rate_limit.max(0) as u32,
        );
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_energy_RustGridEngine_updateDevice(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
    amount: jint,
    rate_limit: jint,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut EnergyNetworkManager);
        manager.update_device(
            BlockPos::new(x, y, z),
            amount.max(0) as u32,
            rate_limit.max(0) as u32,
        );
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_energy_RustGridEngine_removeDevice(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut EnergyNetworkManager);
        manager.remove_device(BlockPos::new(x, y, z));
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_energy_RustGridEngine_getCableCount(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) -> jint {
    if handle == 0 {
        return 0;
    }
    let result = catch_unwind(|| unsafe {
        let manager = &*(handle as *const EnergyNetworkManager);
        manager.cable_count() as jint
    });
    result.unwrap_or(0)
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_energy_RustGridEngine_getGridCount(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) -> jint {
    if handle == 0 {
        return 0;
    }
    let result = catch_unwind(|| unsafe {
        let manager = &*(handle as *const EnergyNetworkManager);
        manager.grid_count() as jint
    });
    result.unwrap_or(0)
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_energy_RustGridEngine_getConnectedDevices(
    env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
) -> jlongArray {
    if handle == 0 {
        return std::ptr::null_mut();
    }

    let devices = catch_unwind(|| unsafe {
        let manager = &*(handle as *const EnergyNetworkManager);
        let cable_pos = BlockPos::new(x, y, z);
        let devs = manager.get_connected_devices(&cable_pos);
        devs.into_iter().map(|p| p.to_packed_i64()).collect::<Vec<i64>>()
    });

    let buffer = match devices {
        Ok(b) => b,
        Err(_) => return std::ptr::null_mut(),
    };

    match env.new_long_array(buffer.len() as i32) {
        Ok(arr) => {
            if let Err(_) = env.set_long_array_region(&arr, 0, &buffer) {
                return std::ptr::null_mut();
            }
            arr.into_raw()
        }
        Err(_) => std::ptr::null_mut(),
    }
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_energy_RustGridEngine_solveTick(
    env: JNIEnv,
    _class: JClass,
    handle: jlong,
) -> jlongArray {
    if handle == 0 {
        return std::ptr::null_mut();
    }

    let flat_data = catch_unwind(move || unsafe {
        let manager = &mut *(handle as *mut EnergyNetworkManager);
        let results = manager.solve_tick();

        let total_tx = results.drained.len() + results.received.len();
        let mut buffer: Vec<i64> = Vec::with_capacity(total_tx * 2);

        // Sources: drained values are recorded as negative deltas
        for (pos, amount) in results.drained {
            buffer.push(pos.to_packed_i64());
            buffer.push(-(amount as i64));
        }

        // Sinks: received values are recorded as positive deltas
        for (pos, amount) in results.received {
            buffer.push(pos.to_packed_i64());
            buffer.push(amount as i64);
        }

        buffer
    });

    let buffer = match flat_data {
        Ok(b) => b,
        Err(_) => return std::ptr::null_mut(),
    };

    match env.new_long_array(buffer.len() as i32) {
        Ok(arr) => {
            if let Err(_) = env.set_long_array_region(&arr, 0, &buffer) {
                return std::ptr::null_mut();
            }
            arr.into_raw()
        }
        Err(_) => std::ptr::null_mut(),
    }
}

// -------------------------------------------------------------
// KINETIC / MECHANICAL JNI BINDINGS
// -------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_createKineticNetwork(
    _env: JNIEnv,
    _class: JClass,
) -> jlong {
    let result = catch_unwind(|| {
        let manager = Box::new(KineticNetworkManager::new());
        Box::into_raw(manager) as jlong
    });
    result.unwrap_or(0)
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_destroyKineticNetwork(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let _ = Box::from_raw(handle as *mut KineticNetworkManager);
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_addShaft(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
    axis_ord: jint,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut KineticNetworkManager);
        manager.add_shaft(BlockPos::new(x, y, z), Axis::from_i32(axis_ord));
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_removeShaft(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut KineticNetworkManager);
        manager.remove_shaft(BlockPos::new(x, y, z));
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_registerSource(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
    output_fm: jint,
    is_generating: jboolean,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut KineticNetworkManager);
        manager.register_source(
            BlockPos::new(x, y, z),
            output_fm.max(0) as u32,
            is_generating == JNI_TRUE,
        );
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_updateSource(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
    output_fm: jint,
    is_generating: jboolean,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut KineticNetworkManager);
        manager.update_source(
            BlockPos::new(x, y, z),
            output_fm.max(0) as u32,
            is_generating == JNI_TRUE,
        );
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_removeSource(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut KineticNetworkManager);
        manager.remove_source(BlockPos::new(x, y, z));
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_registerConsumer(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
    required_fm: jint,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut KineticNetworkManager);
        manager.register_consumer(BlockPos::new(x, y, z), required_fm.max(0) as u32);
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_removeConsumer(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
) {
    if handle == 0 {
        return;
    }
    let _ = catch_unwind(|| unsafe {
        let manager = &mut *(handle as *mut KineticNetworkManager);
        manager.remove_consumer(BlockPos::new(x, y, z));
    });
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_isSpinning(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
) -> jboolean {
    if handle == 0 {
        return 0;
    }
    let result = catch_unwind(|| unsafe {
        let manager = &*(handle as *const KineticNetworkManager);
        if manager.is_spinning(&BlockPos::new(x, y, z)) {
            1
        } else {
            0
        }
    });
    result.unwrap_or(0)
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_getAvailableFM(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
    x: jint,
    y: jint,
    z: jint,
) -> jint {
    if handle == 0 {
        return 0;
    }
    let result = catch_unwind(|| unsafe {
        let manager = &*(handle as *const KineticNetworkManager);
        manager.get_available_fm(&BlockPos::new(x, y, z)) as jint
    });
    result.unwrap_or(0)
}

#[no_mangle]
pub extern "system" fn Java_net_exerion_mechanical_RustKineticEngine_getShaftCount(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) -> jint {
    if handle == 0 {
        return 0;
    }
    let result = catch_unwind(|| unsafe {
        let manager = &*(handle as *const KineticNetworkManager);
        manager.shaft_count() as jint
    });
    result.unwrap_or(0)
}
