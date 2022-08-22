package dev.nycode.omsilauncher.localization

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LyricistStrings
import dev.nycode.omsilauncher.util.removeLineBreaks

@LyricistStrings(languageTag = "en", default = true)
val StringsEn = Strings(
    ok = "Ok",
    `continue` = "Continue",
    instances = "Instances",
    settings = "Settings",
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
    confirmDeletionWithDependencies = { instance, dependencies ->
        "Are you sure you want to delete $instance and the following instances: ${
            dependencies.joinToString(
                "\n",
                prefix = "\n",
            ) { " - ${it.name}" }
        }"
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
    setupChooseDirectoryWarning = "This is NOT the Steam/OMSI installation directory. Create a new directory for the launcher.",
    chooseGameFileDirectory = "Select Game File Directory",
    noGameFileDirectory = "No Game Directory selected.",
    newInstance = "New Instance",
    biArticulatedBusPatch = "Bi articulated bus Patch",
    tramPatch = "Tram Patch",
    createANewInstance = "Create a new Instance",
    instanceDirectory = "Instance Directory",
    patchVersion = "Patch Version",
    use4gbPatch = "Use 4 GB Memory Patch",
    use4gbPatchTooltip = "Allows OMSI to use 4 GB of memory",
    createInstance = "Create Instance",
    continueSetup = "Continue with the Setup",
    setupLauncherDirectoryHeadline = "Launcher Directory:",
    setupOmsiDirectoryHeadline = "OMSI Installation:",
    setupStepCopyGameFiles = "Copying game files",
    setupStepCopyManifest = "Copying manifest",
    setupStepCreatingInstancesJson = "Creating instances.json",
    setupStepSavingConfiguration = "Saving Configuration",
    startInstance = "Start Instance",
    startEditorInstance = "Start Instance in editor mode",
    selectedPatchVersion = { patchVersion ->
        "Selected Patch: $patchVersion"
    },
    editInstance = "Edit instance",
    deleteInstance = "Delete instance",
    omsiNotInstalledTitle = "OMSI not installed",
    omsiNotInstalledDescription = "Cannot find OMSI installation path, please try starting it through Steam",
    steamNotInstalledTitle = "Steam not installed",
    steamNotInstalledDescription = "This launcher is only available for the Steam version of OMSI 2",
    saveLogs = "Save Logs",
    saveLogsTooltip = { "Saves all logs after closing OMSI in ${it ?: "<not set>"}" },
    debugMode = "Debug mode",
    debugModeTooltip = "This mode allows modders to modify core functionality of the game (e.g. AI-Speed)",
    defaultLogLevel = "Default Log Level",
    noLogLogLevel = "No Log",
    logAllLogLevel = "Full Log",
    defaultWindowMode = "Default Screen mode",
    windowedWindowMode = "Windowed",
    fullScreenWindowMode = "Full Screen",
    logLevel = "Log Level",
    screenMode = "Screen Mode",
    showInstanceFiles = "Show instance files",
    closeSteamLaunchInfo = "Please close Steam before switching instances",
    directoryNeedsToBeEmpty = "The directory must be empty",
    cannotDeleteMainInstance = "You can't delete the base instance",
    baseInstance = "Base Instance",
    reSyncInstance = "Re-Synchronise Instance",
    reSyncInstanceExplainer = buildAnnotatedString {
        append("Use this, ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("before ")
        }
        append("you make changes to the base instance")
    },
    iAmDone = "I am done",
    mergingSteamManifest = { name -> "Merging Steam manifest of $name" },
    preparingReSynchronisation = "Preparing re-synchronisation",
    reLinkingInstances = { current: Int, total: Int -> "Relinking instanced, round $current/$total" },
    waitingForChanges = "Waiting for changes",
    save = "Save",
    editInstanceTitle = { name -> "Edit instance $name" },
    editBaseInstance = "Edit base-instance",
    reset = "Reset",
    onlyImages = "Only Images",
    instanceIcon = "Instance icon",
    eCitaro = "eCitaro",
    clickToChangeInstanceIcon = "Click to change instance icon",
    useHardLinks = "Use Hard Links",
    useHardLinksDescription = "Use Hard Links for Omsi.exe files (use this if you use OmniNavigation)",
    language = "Language",
    checkForUpdates = "Check for Updates",
    checkForUpdatesDescription = "Check for updates on startup",
    newVersionAvailableTitle = "A new version is available",
    newVersionDescription = { version -> "The version $version is ready for download" },
    viewReleaseOnGithub = "View change log on GitHub",
    downloadNewVersion = "Download Version"
)
