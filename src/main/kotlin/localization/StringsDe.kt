package dev.nycode.omsilauncher.localization

import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "de")
val StringsDe = Strings(
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
    }
)
