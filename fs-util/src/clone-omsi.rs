use std::fs;
use std::os::windows::fs as windows_fs;
use std::path::PathBuf;

use structopt::*;

use crate::launcher::with_privileges;

mod launcher;

#[derive(Debug, StructOpt)]
#[structopt(
    name = "clone-omsi",
    about = "Clones the base game into a new instance folder"
)]
struct Opt {
    #[structopt(help = "The base game installation", parse(from_os_str))]
    base_game_folder: PathBuf,

    #[structopt(help = "The path to the new desired instance", parse(from_os_str))]
    omsi_instance_folder: PathBuf,
    #[structopt(
        help = "Path to the Omsi.exe you want to use for this instance",
        parse(from_os_str)
    )]
    binary_path: PathBuf,
}

fn main() -> std::io::Result<()> {
    with_privileges(run)
}

fn run() -> std::io::Result<()> {
    let opt: Opt = Opt::from_args();

    mirror_folder(opt.base_game_folder, opt.omsi_instance_folder)?;
    //windows_fs::symlink_file(opt.binary_path, opt.omsi_instance_folder.join("Omsi.exe"))?;

    Ok(())
}

fn mirror_folder(from: PathBuf, to: PathBuf) -> std::io::Result<()> {
    fs::create_dir_all(to.as_path())?;

    for item in fs::read_dir(from)? {
        let path = item?.path();
        let target_name = path.file_name().unwrap().to_str().unwrap();
        let target = to.join(target_name);
        if path.is_dir() {
            mirror_folder(path, target).unwrap()
        } else {
            windows_fs::symlink_file(path, target)?
        }
    }

    Ok(())
}
