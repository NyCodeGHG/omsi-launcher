extern crate core;

use std::fs;
use std::os::windows::fs as windows_fs;
use std::path::PathBuf;

use log::info;
use structopt::*;

use crate::directory_copier::mirror_folder;
use crate::launcher::with_symlink_permission;

mod directory_copier;
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

    #[structopt(short, long, help = "Only links binary_path to Omsi.exe")]
    only_link_binary: bool,
}

fn main() {
    simple_logger::SimpleLogger::new().env().init().unwrap();
    with_symlink_permission(run);
}

fn run() -> std::io::Result<()> {
    let opt: Opt = Opt::from_args();

    if !opt.only_link_binary {
        info!(
            "Linking {} to {}",
            &opt.base_game_folder.to_str().unwrap(),
            &opt.omsi_instance_folder.to_str().unwrap()
        );
        mirror_folder(&opt.base_game_folder, &opt.omsi_instance_folder)?;
    } else {
        info!("Only link binary flag present, skipping clone")
    }

    let omsi_executable = &opt.omsi_instance_folder.join("Omsi.exe");
    info!(
        "Linking {} to {}",
        &opt.binary_path.to_str().unwrap(),
        &omsi_executable.to_str().unwrap()
    );
    if opt.only_link_binary {
        // Delete old OMSI.exe for relink
        fs::remove_file(&omsi_executable)?;
    }
    windows_fs::symlink_file(&opt.binary_path, omsi_executable)?;

    if !opt.only_link_binary {
        let base_manifest = opt.base_game_folder.join("manifest.acf");
        let destination = opt.omsi_instance_folder.join("manifest.acf");
        info!(
            "Copying base manifest {} to {}",
            &base_manifest.to_str().unwrap(),
            &destination.to_str().unwrap()
        );
        fs::copy(base_manifest, destination)?;
    }

    Ok(())
}
