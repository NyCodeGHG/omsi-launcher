use std::os::windows::fs as windows_fs;
use std::path::{PathBuf};

use structopt::*;

use crate::launcher::with_privileges;

mod launcher;

#[derive(Debug, StructOpt)]
#[structopt(name = "activate-omsi", about = "Activates a specific Omsi instance")]
struct Opt {
    #[structopt(
    help = "The Steam installation folder of OMSI",
    parse(from_os_str)
    )]
    omsi_installation_folder: PathBuf,

    // test13
    #[structopt(
    help = "The folder of the instance you want to activate",
    parse(from_os_str)
    )]
    omsi_instance_folder: PathBuf,
}

fn main() -> std::io::Result<()> {
    with_privileges(run)
}

fn run() -> std::io::Result<()> {
    let opt: Opt = Opt::from_args();

    let target = opt.omsi_installation_folder;
    if target.is_dir() && !target.is_symlink() {
        // let backup_name = target.parent().unwrap().join("Omsi 2 - backup");
        // fs::rename(target, backup_name)?;
    }

    windows_fs::symlink_dir(opt.omsi_instance_folder, &target)
}
