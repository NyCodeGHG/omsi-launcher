use std::fmt::Debug;
use std::fs;
use std::os::windows::fs as windows_fs;
use std::path::PathBuf;
use std::time::{SystemTime, UNIX_EPOCH};

use log::{info, warn};
use structopt::*;

use crate::launcher::with_privileges;

mod launcher;

#[derive(Debug, StructOpt)]
#[structopt(name = "activate-omsi", about = "Activates a specific Omsi instance")]
struct Opt {
    #[structopt(help = "The Steam installation folder of OMSI", parse(from_os_str))]
    omsi_installation_folder: PathBuf,

    // test13
    #[structopt(
        help = "The folder of the instance you want to activate",
        parse(from_os_str)
    )]
    omsi_instance_folder: PathBuf,
}

fn main() -> std::io::Result<()> {
    simple_logger::SimpleLogger::new().env().init().unwrap();
    with_privileges(run)
}

fn run() -> std::io::Result<()> {
    let opt: Opt = Opt::from_args();

    let target = &opt.omsi_installation_folder;
    if target.is_dir() && !target.is_symlink() {
        warn!("OMSI Installation folder is not a symlink. Creating a backup.");
        let backup_name = target.parent().unwrap().join(format!(
            "OMSI 2 - Backup - {}",
            SystemTime::now()
                .duration_since(UNIX_EPOCH)
                .unwrap()
                .as_millis()
        ));
        fs::rename(&target, &backup_name)?;
        info!(
            "Backup has been created at {}",
            &backup_name.to_str().unwrap()
        );
    }
    if target.is_symlink() {
        info!("Deleting old symlink {}", &target.file_name().unwrap().to_str().unwrap());
        fs::remove_dir(target)?;
    }

    info!(
        "Symlinking {} to {}",
        &opt.omsi_installation_folder.to_str().unwrap(),
        &target.to_str().unwrap()
    );
    windows_fs::symlink_dir(&opt.omsi_instance_folder, &target)
}
