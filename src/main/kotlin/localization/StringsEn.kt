package dev.nycode.omsilauncher.localization

import cafe.adriel.lyricist.LyricistStrings
import dev.nycode.omsilauncher.util.removeLineBreaks

@LyricistStrings(languageTag = "en", default = true)
val StringsEn = Strings(
    ok = "Ok",
    `continue` = "Continue",
    instances = "Instances",
    addNewInstance = "Add new Instance",
    runInstance = "Run instance",
    launch = "Launch",
    runEditor = "Run editor",
    launchEditor = "Launch editor",
    instancePatchVersion = "Instance patch version",
    edit = "Edit",
    delete = "Delete",
    deleteConfirmation = "Delete Confirmation",
    yes = "Yes",
    no = "No",
    confirmDeletion = { instance ->
        "Are you sure you want to delete $instance?"
    },
    instanceName = "Instance name",
    activateInstance = "Activate instance",
    activate = "Activate",
    closeSteamInfo = "Please close Steam™ while setting up the launcher.",
    closeSteam = "Close Steam",
    unableToCloseSteam = "We were not able to close Steam gracefully. Please close it manually. The setup will continue automatically.",
    unableToCloseSteamTitle = "Cannot close Steam.",
    createBaseGame = "Create base game instance",
    baseGameExplanation = """In order to share AddOns between your instances, you need to select a list of base addons,
        |the easiest way is to reinstall OMSI, with just the base AddOns selected
    """.trimMargin().removeLineBreaks(),
    setup = "Setup",
    setupChooseDirectory = "Choose a directory where your instances and the base game files should be stored.",
    chooseGameFileDirectory = "Select Game File Directory",
    noGameFileDirectory = "No Game Directory selected.",
    newInstance = "New Instance",
    biArticulatedBusPatch = "Bi articulated bus Patch",
    tramPatch = "Tram Patch",
    createANewInstance = "Create a new Instance",
    instanceDirectory = "Instance Directory",
    patchVersion = "Patch Version",
    use4gbPatch = "Use 4 GB Memory Patch",
    createInstance = "Create Instance"
)
