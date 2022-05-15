use std::fs;
use std::os::windows::fs as windows_fs;
use std::path::{Path, PathBuf};

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
        } else if target.file_name().unwrap() != "manifest.acf"
            && !target.exists()
            && !target.is_symlink()
        {
            windows_fs::symlink_file(path, target)?
        }
    }

    Ok(())
}
