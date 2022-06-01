use std::fs;
use std::os::windows::fs as windows_fs;
use std::path::{Path, PathBuf};

const HARD_LINK_FILES: [&str; 2] = ["omninavigation.cfg", "omsinavigation.cfg"];
const IGNORED_FILES: [&str; 1] = ["manifest.acf"];

pub fn mirror_folder(from: &PathBuf, to: &Path) -> std::io::Result<()> {
    if !to.exists() {
        fs::create_dir_all(to)?;
    }

    for item in fs::read_dir(from)? {
        let path = item?.path();
        let target_name = path.file_name().unwrap().to_str().unwrap();
        let target = to.join(target_name);
        if path.is_dir() {
            mirror_folder(&path, &target).unwrap()
        } else if IGNORED_FILES.contains(&target.file_name().unwrap().to_str().unwrap())
            && !target.exists()
            && !path.is_symlink()
        {
            let name = target.file_name().unwrap().to_str().unwrap();
            if HARD_LINK_FILES.contains(&name) {
                fs::hard_link(path, target)?
            } else {
                windows_fs::symlink_file(path, target)?
            }
        }
    }

    Ok(())
}
