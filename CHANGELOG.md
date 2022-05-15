# Changelog

## [Unreleased]

### Added

- Tooltips ([#23](https://github.com/NyCodeGHG/omsi-launcher/pull/23))
- Context Menus ([#24](https://github.com/NyCodeGHG/omsi-launcher/pull/24))
- Add relink native
  executable ([edfd472](https://github.com/NyCodeGHG/omsi-launcher/commit/edfd4722e238da15a9faaa11b95f0549c3e75db2))
- Add app manifest merging logic ([ee8b73d](https://github.com/NyCodeGHG/omsi-launcher/commit/ee8b73deac980611b2e1976eb70d47a7ae45b631))
- Add vdf parser ([d8b9ac4](https://github.com/NyCodeGHG/omsi-launcher/commit/d8b9ac4fa371fe89940e182a4c6e68421534a524))
- Add all instance options to creation UI

### Changed

- Upgrade kotlinx-serialization-json to
  1.3.3 ([#17](https://github.com/NyCodeGHG/omsi-launcher/pull/17))
- Upgrade lyricst to 1.2.1 ([#19](https://github.com/NyCodeGHG/omsi-launcher/pull/19)
  , [#18](https://github.com/NyCodeGHG/omsi-launcher/pull/18))

### Deprecated

### Removed

### Fixed

- Handle when Steam/OMSI is not
  installed ([#20](https://github.com/NyCodeGHG/omsi-launcher/pull/20))
- Log native executables correctly ([#22](https://github.com/NyCodeGHG/omsi-launcher/pull/22))

### Security

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

### Deprecated

### Removed

- Old routing system based on enums

### Fixed

### Security

## [0.2.0] - 2022-05-09

### Added

- Add proper `CHANGELOG.md`

### Fixed

- Fix installer not including Rust binaries
