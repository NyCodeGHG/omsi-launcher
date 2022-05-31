extern crate core;

use std::fmt::Debug;
use std::fs;
use std::fs::File;
use std::os::windows::fs as windows_fs;
use std::path::PathBuf;
use std::time::{SystemTime, UNIX_EPOCH};

use log::{info, warn};
use structopt::*;

use crate::launcher::with_symlink_permission;
use crate::omsi_linker::link_omsi;

mod launcher;
mod omsi_linker;

#[derive(Debug, StructOpt)]
#[structopt(name = "activate-omsi", about = "Activates a specific Omsi instance")]
struct Opt {
    #[structopt(help = "The Steam installation folder of OMSI", parse(from_os_str))]
    omsi_installation_folder: PathBuf,

    #[structopt(
        help = "The folder of the instance you want to activate",
        parse(from_os_str)
    )]
    omsi_instance_folder: PathBuf,

    #[structopt(
        help = "The path to the Omsi.exe patch to validate",
        parse(from_os_str)
    )]
    omsi_executable_path: Option<PathBuf>,
}

fn main() {
    simple_logger::SimpleLogger::new().env().init().unwrap();
    with_symlink_permission(run);
}

const OMSI_APP_ID: i32 = 252530;

fn run() -> std::io::Result<()> {
    let opt: Opt = Opt::from_args();

    let current_manifest = opt
        .omsi_installation_folder
        .parent()
        .unwrap()
        .parent()
        .unwrap()
        .join(format!("appmanifest_{}.acf", OMSI_APP_ID));

    if opt.omsi_installation_folder.is_symlink() && current_manifest.exists() {
        let current_actual_installation = opt.omsi_installation_folder.read_link()?;
        info!(
            "Backing up old manifest to {}",
            current_actual_installation.to_str().unwrap()
        );

        fs::rename(
            &current_manifest,
            current_actual_installation.join("manifest.acf"),
        )?;
    } else {
        warn!("OMSI folder is not symlink, manifest restoration failed")
    }

    match &opt.omsi_executable_path {
        None => {}
        Some(path) => {
            let current_executable = opt.omsi_installation_folder.join("Omsi.exe");
            if !current_executable.exists()
                || !current_executable.is_symlink()
                || &current_executable.read_link()? != path
            {
                link_omsi(&opt.omsi_instance_folder, path)?;
            }
        }
    }

    symlink_global_omsi_entry_point(&opt)?;
    let new_manifest = opt.omsi_instance_folder.join("manifest.acf");
    info!(
        "Copying new manifest {} to {}",
        &new_manifest.to_str().unwrap(),
        &current_manifest.to_str().unwrap()
    );

    if !current_manifest.exists() {
        File::create(&current_manifest)?;
    }

    fs::copy(&new_manifest, &current_manifest).map(|_| ())
}

fn symlink_global_omsi_entry_point(opt: &Opt) -> std::io::Result<()> {
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
        info!(
            "Deleting old symlink {}",
            &target.file_name().unwrap().to_str().unwrap()
        );
        fs::remove_dir(target)?;
    }

    info!(
        "Symlinking {} to {}",
        &opt.omsi_instance_folder.to_str().unwrap(),
        &target.to_str().unwrap()
    );
    windows_fs::symlink_dir(&opt.omsi_instance_folder, &target)
}
