use std::fs;
use std::os::windows::fs as windows_fs;
use std::path::{Path, PathBuf};

use log::info;
use structopt::*;

use crate::launcher::with_symlink_permission;

mod launcher;
mod privileges;

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

fn main() -> std::io::Result<()> {
    simple_logger::SimpleLogger::new().env().init().unwrap();
    with_symlink_permission(run)
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
    windows_fs::symlink_file(&opt.binary_path, omsi_executable)?;

    let base_manifest = opt.base_game_folder.join("manifest.acf");
    let destination = opt.omsi_instance_folder.join("manifest.acf");
    info!(
        "Copying base manifest {} to {}",
        &base_manifest.to_str().unwrap(),
        &destination.to_str().unwrap()
    );

    fs::copy(base_manifest, destination)?;
    Ok(())
}

fn mirror_folder(from: &PathBuf, to: &Path) -> std::io::Result<()> {
    if !to.exists() {
        fs::create_dir_all(to)?;
    }

    for item in fs::read_dir(from)? {
        let path = item?.path();
        let target_name = path.file_name().unwrap().to_str().unwrap();
        let target = to.join(target_name);
        if path.is_dir() {
            mirror_folder(&path, &target).unwrap()
        } else if target.file_name().unwrap() != "manifest.acf" {
            windows_fs::symlink_file(path, target)?
        }
    }

    Ok(())
}
