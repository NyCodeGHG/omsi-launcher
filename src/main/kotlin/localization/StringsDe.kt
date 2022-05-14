package dev.nycode.omsilauncher.localization

import cafe.adriel.lyricist.LyricistStrings
import dev.nycode.omsilauncher.util.removeLineBreaks

@LyricistStrings(languageTag = "de")
val StringsDe = Strings(
    ok = "Ok",
    `continue` = "Weiter",
    instances = "Instanzen",
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
    chooseGameFileDirectory = "Wähle dein neues Spielverzeichnis aus",
    noGameFileDirectory = "Kein Spielverzeichnis ausgewählt.",
    newInstance = "Neue Instanz",
    biArticulatedBusPatch = "Doppelgelenkbus-Patch",
    tramPatch = "Straßenbahn-Patch",
    createANewInstance = "Eine neue Instanz erstellen",
    instanceDirectory = "Instanz-Ordner",
    patchVersion = "Patch-Version",
    use4gbPatch = "4 GB Arbeitsspeicher-Patch verwenden",
    createInstance = "Instanz erstellen",
    omsiNotInstalledTitle = "OMSI ist nicht installiert",
    omsiNotInstalledDescription = "Das Installationsverzeichnis konnte nicht gefunden werden, bitte starte OMSI über Steam",
    steamNotInstalledTitle = "Steam ist nicht installiert",
    steamNotInstalledDescription = "Der Launcher ist nur für die Steam-Version von OMSI 2 verfügbar"
)
