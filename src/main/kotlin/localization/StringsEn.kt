package dev.nycode.omsilauncher.localization

import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "en", default = true)
val StringsEn = Strings(
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
    instanceName = "Instance name"
)
