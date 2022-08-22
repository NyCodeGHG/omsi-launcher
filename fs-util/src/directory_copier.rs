use std::fs;
use std::os::windows::fs as windows_fs;
use std::path::{Path, PathBuf};

use log::info;

const HARD_LINK_FILES: [&str; 2] = ["omninavigation.cfg", "omsinavigation.cfg"];
const IGNORED_FILES: [&str; 1] = ["manifest.acf"];

pub fn mirror_folder(
    from: &PathBuf,
    to: &Path,
    do_multi_symlink: Option<bool>,
) -> std::io::Result<()> {
    if !to.exists() {
        fs::create_dir_all(to)?;
    }

    for item in fs::read_dir(from)? {
        let path = item?.path();
        let target_name = path.file_name().unwrap().to_str().unwrap();
        let target = to.join(target_name);
        if path.is_dir() {
            mirror_folder(&path, &target, do_multi_symlink).unwrap()
        } else if !target.exists() && (do_multi_symlink.unwrap_or(false) || !path.is_symlink()) {
            let name = target.file_name().unwrap().to_str().unwrap();
            if HARD_LINK_FILES.contains(&name) {
                info!(
                    "Hard-linking {} because it is known to be used by a program not supporting symlinks.",
                    &name
                );
                fs::hard_link(path, target)?
            } else if IGNORED_FILES.contains(&name) {
                info!(
                    "Not symlinking {} because that file is known to be used otherwise",
                    &name
                );
            } else {
                windows_fs::symlink_file(path, target)?
            }
        }
    }

    Ok(())
}
