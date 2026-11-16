use jni::objects::JClass;
use jni::sys::{jboolean, jint, jlong, jlongArray, JNI_TRUE};
use jni::JNIEnv;
use std::panic::catch_unwind;

use crate::grid::{BlockPos, DeviceType, EnergyNetworkManager};

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
