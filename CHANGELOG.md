# Changelog

## [Unreleased]
### Added

### Changed
- Adjusted fs-util project to rust conventions.

- Migrated from structopt to clap.

- Upgraded to JDK 19

### Deprecated

### Removed

### Fixed

### Security

## [0.7.1] - 2022-09-22
### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [0.7.0] - 2022-08-22
### Added
- Add ability to choose a different parent instance
- Notification about launcher updates

## [0.6.4] - 2022-08-07
### Fixed
- Fix clone-omsi not creating symlinks

## [0.6.3] - 2022-06-26
### Fixed
- Fix exception when creating a new instance

## [0.6.2] - 2022-06-26
### Fixed
- Add missing JDK modules

## [0.6.0] - 2022-06-04
### Added
- Add option to change the instance icon
- Implement Settings tab
- App icon

### Changed
- Revert compose to `1.2.0-alpha01-dev686`

### Fixed
- No longer display base instance name and path when creating new instance
- Fix ClassNotFoundException when trying to call native picker

## [0.5.3] - 2022-06-01
### Changed
- Only prompt UAC once when relinking base instance
- Also check if Omsi.exe links to correct patch and not only if it exists

### Fixed
- Disable re-sync button, when OMSI is running
- Add ability to use hard-links for OMSI.exe
- Use hard-links for omninavigation configs
- Also save name when editing

## [0.5.2] - 2022-05-30
### Fixed
- Fix error when re-linking base instance

## [0.5.1] - 2022-05-30
### Fixed
- Fix edit dialog for base instance
  - Disable name field
  - Fix title showing `<base instance>`
- Translate edit dialog
- Fix checkbox area not toggling properly

## [0.5.0] - 2022-05-29
### Added
- Modify base instance button
- Implement edit button

### Changed
- Increase Checkbox hitbox to also register clicks on text
- Bump `kotlinx.coroutines` to `1.6.2`

## [0.4.1] - 2022-05-26
### Fixed
- Fix instance creation failing, if base instance is started once
  - This was caused by `clone-omsi.exe` symlinking the `Omsi.exe` symlink in base game and therefore panicking on new instances

## [0.4.0] - 2022-05-26
### Added
- Tooltips ([#23](https://github.com/NyCodeGHG/omsi-launcher/pull/23))
- Context Menus ([#24](https://github.com/NyCodeGHG/omsi-launcher/pull/24))
- Add relink native
  executable ([edfd472](https://github.com/NyCodeGHG/omsi-launcher/commit/edfd4722e238da15a9faaa11b95f0549c3e75db2))
- Add app manifest merging logic ([ee8b73d](https://github.com/NyCodeGHG/omsi-launcher/commit/ee8b73deac980611b2e1976eb70d47a7ae45b631))
- Add vdf parser ([d8b9ac4](https://github.com/NyCodeGHG/omsi-launcher/commit/d8b9ac4fa371fe89940e182a4c6e68421534a524))
- Add all instance options to creation UI ([#26](https://github.com/NyCodeGHG/omsi-launcher/pull/26))
- Show base game instance in instances view
- Add Context Menu entry to show instance files ([#29](https://github.com/NyCodeGHG/omsi-launcher/pull/29))
- Show "Close Steam" dialog as well when switching instances ([#27](https://github.com/NyCodeGHG/omsi-launcher/pull/27))

### Changed
- Upgrade kotlinx-serialization-json to
  1.3.3 ([#17](https://github.com/NyCodeGHG/omsi-launcher/pull/17))
- Upgrade lyricst to 1.2.1 ([#19](https://github.com/NyCodeGHG/omsi-launcher/pull/19)
  , [#18](https://github.com/NyCodeGHG/omsi-launcher/pull/18))
- Check whether directories are empty in setup
  and creation dialogs ([#33](https://github.com/NyCodeGHG/omsi-launcher/pull/33))
- Make launcher suggest directories based on instance names
  ([#33](https://github.com/NyCodeGHG/omsi-launcher/pull/33))

### Fixed
- Handle when Steam/OMSI is not
  installed ([#20](https://github.com/NyCodeGHG/omsi-launcher/pull/20))
- Log native executables correctly ([#22](https://github.com/NyCodeGHG/omsi-launcher/pull/22))
- Fix input boxes in instance creation dialog exceeding UI space
  ([#33](https://github.com/NyCodeGHG/omsi-launcher/pull/33))

## [0.3.0] - 2022-05-14
### Added
- Add internal support for 4 GB patch
- Synchronise Steam manifest between instances
- Add internal routing system
- Add explanation for base game instance in setup
- Add instance activate button
- Add UAC error dialog

### Changed
- Update LICENSE software author
- Update program vendor
- Translate missing strings in setup

### Removed
- Old routing system based on enums

## [0.2.0] - 2022-05-09
### Added
- Add proper `CHANGELOG.md`

### Fixed
- Fix installer not including Rust binaries
