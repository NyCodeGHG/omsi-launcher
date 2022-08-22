extern crate core;

use std::fs;
use std::path::PathBuf;

use log::info;
use structopt::*;

use crate::directory_copier::mirror_folder;
use crate::launcher::with_symlink_permission;
use crate::omsi_linker::link_omsi;

mod directory_copier;
mod launcher;
mod omsi_linker;

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

    #[structopt(short, long, help = "Use hard links to link Omsi.exe")]
    hard_link_binary: bool,

    #[structopt(short, long, help = "Do multi sym-linking")]
    do_multi_symlink: bool,
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
        mirror_folder(
            &opt.base_game_folder,
            &opt.omsi_instance_folder,
            Some(opt.do_multi_symlink),
        )?;
    } else {
        info!("Only link binary flag present, skipping clone")
    }

    link_omsi(
        &opt.omsi_instance_folder,
        &opt.binary_path,
        opt.hard_link_binary,
    )?;

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
