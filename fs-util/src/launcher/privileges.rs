use std::{
    env, fs,
    time::{SystemTime, UNIX_EPOCH},
};

use std::os::windows::fs as windows_fs;

pub fn can_create_symlinks() -> bool {
    let temp_dir = env::temp_dir();
    let timestamp = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_millis();
    let first_path = temp_dir.join(format!("{}_0", timestamp));
    fs::File::create(&first_path).unwrap();
    let second = temp_dir.join(format!("{}_1", timestamp));
    let result = windows_fs::symlink_file(&first_path, &second).is_ok();
    fs::remove_file(second).ok();
    fs::remove_file(first_path).ok();
    result
}
