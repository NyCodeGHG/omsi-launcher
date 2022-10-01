use std::path::{Path, PathBuf};
use std::{fs, io};

use clap::Parser;
use fs_util::directory_copier::mirror_folder;
use fs_util::launcher::with_symlink_permission;
use log::info;

#[derive(Debug, Parser)]
#[command(
    name = "relink-omsi",
    about = "Clones the base game into a new instance folder"
)]
struct Opt {
    #[arg(help = "The base game installation")]
    base_game_folder: PathBuf,

    #[arg(short, long, help = "A path to a instance to relink")]
    omsi_instance_folder: Vec<PathBuf>,
}

fn main() {
    simple_logger::SimpleLogger::new().env().init().unwrap();

    with_symlink_permission(run)
}

fn run() -> io::Result<()> {
    let opt = Opt::parse();

    for instance in opt.omsi_instance_folder {
        info!(
            "Checking dead symlinks for instance {}",
            instance.to_str().unwrap()
        );
        delete_dead_symlinks(&opt.base_game_folder, &instance)?;

        info!("Relinking instance {}", instance.to_str().unwrap());
        // Re-link non existent files
        mirror_folder(&opt.base_game_folder, &instance, None)?;
    }

    Ok(())
}

fn delete_dead_symlinks(base_game_folder: &PathBuf, instance: &PathBuf) -> io::Result<()> {
    for item in fs::read_dir(instance)? {
        let path = item?.path();
        if path.is_symlink() {
            let symlink_destination = path.read_link()?;
            // Do check if links to omsi main
            if !symlink_destination.exists() && symlink_destination.part_of(base_game_folder) {
                info!(
                    "Deleting old symlink {}, because it links to nowhere",
                    path.to_str().unwrap()
                );

                fs::remove_file(path)?;
            }
        } else if path.is_dir() {
            delete_dead_symlinks(base_game_folder, &path)?;
        }
    }

    Ok(())
}

trait PathContains {
    fn part_of(self, other: &Path) -> bool;
}

impl PathContains for PathBuf {
    fn part_of(self, other: &Path) -> bool {
        self.starts_with(other)
    }
}
