package dev.nycode.omsilauncher.localization

data class Strings(
    val instances: String,
    val addNewInstance: String,
    val eCitaro: String = "eCitaro",
    val runInstance: String,
    val launch: String,
    val runEditor: String,
    val launchEditor: String,
    val instancePatchVersion: String,
    val edit: String,
    val delete: String,
    val deleteConfirmation: String,
    val yes: String,
    val no: String,
    val confirmDeletion: (instance: String) -> String
)
