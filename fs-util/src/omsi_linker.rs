use std::fs;
use std::os::windows::fs as windows_fs;
use std::path::{Path, PathBuf};

use log::{info, warn};

pub fn link_omsi(
    instance_folder: &Path,
    binary_path: &PathBuf,
    hard_link: bool,
) -> std::io::Result<()> {
    let omsi_executable = &instance_folder.join("Omsi.exe");
    if omsi_executable.exists() {
        // Delete old OMSI.exe for relink
        warn!(
            "Deleting old executable {}",
            &omsi_executable.to_str().unwrap()
        );
        fs::remove_file(omsi_executable)?;
    }
    info!(
        "Linking {} to {}",
        &binary_path.to_str().unwrap(),
        &omsi_executable.to_str().unwrap()
    );

    if hard_link {
        fs::hard_link(binary_path, omsi_executable)
    } else {
        windows_fs::symlink_file(binary_path, omsi_executable)
    }
}
