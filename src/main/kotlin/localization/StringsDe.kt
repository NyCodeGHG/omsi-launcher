package dev.nycode.omsilauncher.localization

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LyricistStrings
import dev.nycode.omsilauncher.util.removeLineBreaks

@LyricistStrings(languageTag = "de")
val StringsDe = Strings(
    ok = "Ok",
    `continue` = "Weiter",
    instances = "Instanzen",
    settings = "Einstellungen",
    addNewInstance = "Neue Instanz erstellen",
    runInstance = "Startet diese Instanz",
    launch = "Starten",
    runEditor = "Startet den Editor dieser Instanz",
    launchEditor = "Editor starten",
    instancePatchVersion = "Patch Version der Instanz",
    edit = "Bearbeiten",
    delete = "Löschen",
    deleteConfirmation = "Löschen bestätigen",
    yes = "Ja",
    no = "Nein",
    confirmDeletion = { instance ->
        "Bist du dir sicher, das du $instance löschen willst?"
    },
    instanceName = "Instanzname",
    activateInstance = "Aktiviert eine Instanz",
    activate = "Aktivieren",
    closeSteamInfo = "Bitte schließe Steam™ während du den Launcher einrichtest",
    closeSteam = "Steam schließen",
    unableToCloseSteam = "Wir konnten Steam nicht schließen, bitte schließe es selbst",
    unableToCloseSteamTitle = "Steam konnte nicht geschlossen werden",
    createBaseGame = "Basis-Instanz erstellen",
    baseGameExplanation = """Um verschiedene AddOns in mehreren AddOns installiert zu haben, musst du eine Basis-Instanz
        |erstellen, am einfachsten geht das, indem du OMSI neu installierst und dabei nur die gewünschten Basis-AddOns
        |auswählst
    """.trimMargin().removeLineBreaks(),
    setup = "Setup",
    setupChooseDirectory = "Wähle einen Ordner in dem deine Instanzen und Spieldateien gespeichert werden sollen.",
    setupChooseDirectoryWarning = "Dies ist NICHT das Steam/OMSI Installationsverzeichnis. Erstelle einen neuen Ordner für den Launcher.",
    chooseGameFileDirectory = "Wähle dein neues Spielverzeichnis aus",
    noGameFileDirectory = "Kein Spielverzeichnis ausgewählt.",
    newInstance = "Neue Instanz",
    biArticulatedBusPatch = "Doppelgelenkbus-Patch",
    tramPatch = "Straßenbahn-Patch",
    createANewInstance = "Eine neue Instanz erstellen",
    instanceDirectory = "Instanz-Ordner",
    patchVersion = "Patch-Version",
    use4gbPatch = "4 GB Arbeitsspeicher-Patch verwenden",
    use4gbPatchTooltip = "Erlaubt es OMSI 4GB an Arbeitsspeicher zu verwenden",
    createInstance = "Instanz erstellen",
    continueSetup = "Mit dem Setup fortfahren",
    setupLauncherDirectoryHeadline = "Launcherverzeichnis:",
    setupOmsiDirectoryHeadline = "OMSI Installation:",
    setupStepCopyGameFiles = "Kopiere Spieldateien",
    setupStepCopyManifest = "Kopiere Spielmanifest",
    setupStepCreatingInstancesJson = "Erstelle instances.json",
    setupStepSavingConfiguration = "Speichere Configuration",
    startInstance = "Starte Instanz",
    startEditorInstance = "Starte Instanz im Editor Modus",
    selectedPatchVersion = { patchVersion ->
        "Ausgewählter Patch: $patchVersion"
    },
    editInstance = "Instanz bearbeiten",
    deleteInstance = "Instanz löschen",
    omsiNotInstalledTitle = "OMSI ist nicht installiert",
    omsiNotInstalledDescription = "Das Installationsverzeichnis konnte nicht gefunden werden, bitte starte OMSI über Steam",
    steamNotInstalledTitle = "Steam ist nicht installiert",
    steamNotInstalledDescription = "Der Launcher ist nur für die Steam-Version von OMSI 2 verfügbar",
    saveLogs = "Logs speichern",
    saveLogsTooltip = { "Speichert die Logs nach dem Schließen von OMSI in ${it ?: "<nicht gesetzt>"}" },
    debugMode = "Debug Modus",
    debugModeTooltip = "In diesem Modus können beispielsweise Karten-Entwickler ihre Karten testen, z.B. die KI-Menschen schneller laufen lassen.",
    defaultLogLevel = "Standard Log Level",
    noLogLogLevel = "Log deaktivieren",
    logAllLogLevel = "Vollständiger Log",
    defaultWindowMode = "Standard Fenstermodus",
    windowedWindowMode = "Fenstermodus",
    fullScreenWindowMode = "Vollbild",
    logLevel = "Log Level",
    screenMode = "Fenstermodus",
    showInstanceFiles = "Lokale Dateien anzeigen",
    cannotDeleteMainInstance = "Du kannst die Basis-Instanz nicht löschen",
    baseInstance = "Basis-Instanz",
    directoryNeedsToBeEmpty = "Das Verzeichnis muss leer sein",
    closeSteamLaunchInfo = "Bitte schließe Steam um die Instanz zu wechseln",
    reSyncInstance = "Instanz neu synchronisieren",
    reSyncInstanceExplainer = buildAnnotatedString {
        append("Benutze dies, ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("bevor ")
        }
        append("du Änderungen an der Basis-Instanz vornimmst")
    },
    iAmDone = "Ich bin fertig",
    mergingSteamManifest = { name -> "Steam Manifest von $name wird neu-zusammengefasst" },
    preparingReSynchronisation = "Neu-Synchronisation wird vorbereitet",
    reLinkingInstances = "Instanzen werden neu gelinkt",
    waitingForChanges = "Es wird auf Änderungen gewartet",
    save = "Speichern",
    editInstanceTitle = { name -> "Instanz $name bearbeiten" },
    editBaseInstance = "Basis-Instanz bearbeiten",
    clickToChangeInstanceIcon = "Klicke um das Insanz-Symbol zu ändern",
    eCitaro = "eCitaro",
    instanceIcon = "Instanz-Symbol",
    onlyImages = "Nur Bilddateien",
    reset = "Zurücksetzen",
    useHardLinks = "Hard-Links benutzen",
    useHardLinksDescription = "Benutzt Hard anstelle von Soft links für Omsi.exe Dateien (benutze dies wenn du z.B. OmniNavigation benutzt)",
    language = "Sprache"
)
