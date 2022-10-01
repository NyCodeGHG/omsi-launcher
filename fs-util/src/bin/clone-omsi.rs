extern crate core;

use std::fs;
use std::path::PathBuf;

use clap::Parser;
use fs_util::directory_copier::mirror_folder;
use fs_util::launcher::with_symlink_permission;
use fs_util::omsi_linker::link_omsi;
use log::info;

#[derive(Debug, Parser)]
#[command(
    name = "clone-omsi",
    about = "Clones the base game into a new instance folder"
)]
struct Opt {
    #[arg(help = "The base game installation")]
    base_game_folder: PathBuf,

    #[arg(help = "The path to the new desired instance")]
    omsi_instance_folder: PathBuf,
    #[arg(help = "Path to the Omsi.exe you want to use for this instance")]
    binary_path: PathBuf,

    #[arg(short, long, help = "Only links binary_path to Omsi.exe")]
    only_link_binary: bool,

    #[arg(short, long, help = "Use hard links to link Omsi.exe")]
    hard_link_binary: bool,

    #[arg(short, long, help = "Do multi sym-linking")]
    do_multi_symlink: bool,
}

fn main() {
    simple_logger::SimpleLogger::new().env().init().unwrap();
    with_symlink_permission(run);
}

fn run() -> std::io::Result<()> {
    let opt = Opt::parse();

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
