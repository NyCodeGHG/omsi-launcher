# omsi-launcher

**Archived: This project is not maintained anymore, feel free to fork and maintain further while respecting the [license](LICENSE)**

WIP [OMSI 2](https://store.steampowered.com/app/252530/OMSI_2_Steam_Edition/) game launcher &
manager built using [Kotlin](https://kotlinlang.org)
and [Compose](https://github.com/JetBrains/compose-jb).

**WARNING: This is unstable software. Create Backups of your game files before you use
it.**

# Why use this?

Omsi is optimized extremely poorly for usage with a lot of AddOns, as a single AddOn doesn't exist
in a single location,
it exists in a lot of locations (maps, Vehicles, Sceneryobjects, Splines, Scripts), this combined
with OMSI seemingly
checking your entire OMSI folder each time some UI refreshes the game gets increasingly more laggy
the more addons/mods
you add.

A simple solution is to not have as many addons installed, or having multiple installations of the
game itself for
different groups of addons.

This tool helps you to create multiple installation, without taking up more storage by
[symlinking](https://en.wikipedia.org/wiki/Symbolic_link) common resources to a base installation of
the game, therefore
each individual instance just takes the space of the individual addons it has installed

# How to launch an instance

Whilst it is theoretically possible to launch Omsi through Steam, it is recommended to let the
launcher call Steam
instead.

# What is "omsi-launcher elevation helper" and why does it request admin permissions?

`omsi-launcher elevation helper` is a tool we use to temporarily launch
a [elevated process](https://en.wikipedia.org/wiki/User_Account_Control)
for some file system
operations, this is required as Windows decided that it is necessary to be an administrator in order
to create symlinks. People interested in source code can
click [here](https://github.com/NyCodeGHG/omsi-elevate)

# How does this work?

When starting Omsi through Steam, Steam just calls `<SteamLibrary>\OMSI 2\Launcher.exe` it doesn't
care what that file
is.
So when starting an instance through the launcher it will create a symlink
at `<SteamLibrary>\OMSI 2` pointing to the
instance you want to launch, this way Steam will actually run `<selected instance>\Omsi.exe`

<details>
<summary>Nerdy explanation of how we store which AddOns are enabled for each instance</summary>

Steam keeps track of which AddOns you have installed at `<library>\steamapps\appmanifest_252530.acf` (`252530` being
Omsi's Steam id)

Since Steam overwrites this file if it is a symlink, we needed a different approach.

Instead, when creating the base game installation we know copy the base manifest into `<launcher>\game\manifest.acf`,
each time we create a new instance, we copy that file into `<instance>\manifest.acf`

Then if you launch an instance we check:

- Does `<Steam library>\OMSI 2` already symlink to that instance
    - If yes:
        - Copy `appmanifest_252530.acf` into  `<instance>\manifest.acf`
    - If not (but symlink):
        - Resolve that symlink to the previous instance
        - Copy the current `appmanifest_252530.acf` to the resolved instance
        - Copy the manifest of the instance we want to start to `appmanifest_252530.acf`
    - If not (but directory)
        - Copy the manifest of the instance we want to start to `appmanifest_252530.acf`

That way no only OMSI will only see the selected AddOns
(as they're the only addons physically present in the instance folder)
also Steam will show them and not download any additional ones

</details>

# Folder structure

Base game: When starting the launcher for the first time, it asks you to set up your "base game
folder", this will
contain the actual base game and addons you want to share between all other addons (OmniNavigation,
bus DLCs)

Individual Instance: Each instance of Omsi has its own installation folder, during the creation of
the instance, the
launcher will go through all contents of the base game folder and create a symlink pointing to the
base game. The
launcher will only symlink files and not directories, those will instead be created and their
contents will be
symlinked.
Using this method addon installers can write to base game locations, without writing their contents
to the base game
folders

## Developer folder structure

This documents internal configuration files of the launcher, if you are not a dev working on the
project, just stop
reading

`%APPDATA%\omsi-launcher\config.json`: This currently only stores the actual installation location
of the launcher, set
during setup
`<installation>\instances.json`: This stores all set instances with their launch flags and patch
version

<details>
    <summary>Trivia we found whilst making this</summary>

- You can symlink `<steam library>\OMSI 2` to wherever you want and Steam will launch it.
- You can symlink anything in your `OMSI 2` directory and Omsi will load everything
- Steam stores it's install location at: `HKEY_LOCAL_MACHINE\SOFTWARE\WOW6432Node\Valve\Steam` Value: `InstallPath`
- Omsi stores it's install location at: `HKEY_LOCAL_MACHINE\SOFTWARE\WOW6432Node\aerosoft\OMSI 2` Value: `Product_Path`


</details>
