use std::fs;
use std::path::{Path, PathBuf};

use log::info;
use structopt::StructOpt;

use crate::directory_copier::mirror_folder;
use crate::launcher::with_symlink_permission;

mod directory_copier;
mod launcher;

#[derive(Debug, StructOpt)]
#[structopt(
    name = "relink-omsi",
    about = "Clones the base game into a new instance folder"
)]
struct Opt {
    #[structopt(help = "The base game installation", parse(from_os_str))]
    base_game_folder: PathBuf,

    #[structopt(
        short,
        long,
        help = "A path to a instance to relink",
        parse(from_os_str)
    )]
    omsi_instance_folder: Vec<PathBuf>,
}

fn main() {
    simple_logger::SimpleLogger::new().env().init().unwrap();

    with_symlink_permission(run)
}

fn run() -> std::io::Result<()> {
    let opt = Opt::from_args();

    for instance in opt.omsi_instance_folder {
        info!("Relinking instance {}", instance.to_str().unwrap());
        for item in fs::read_dir(&instance)? {
            let path = item?.path();
            if path.is_symlink() {
                let symlink_destination = path.read_link()?;
                // Do check if links to omsi main
                if !symlink_destination.exists()
                    && symlink_destination.part_of(&opt.base_game_folder)
                {
                    info!(
                        "Deleting old symlink {}, because it links to nowhere",
                        path.to_str().unwrap()
                    );

                    fs::remove_file(path)?;
                }
            }
        }

        // Re-link non existent files
        mirror_folder(&opt.base_game_folder, &instance)?;
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
