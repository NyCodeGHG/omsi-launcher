package dev.nycode.omsilauncher.instance

import java.util.*

data class InstanceViewModel(
    val id: UUID,
    val name: String,
    val patchVersion: Instance.PatchVersion,
    val options: Instance.Options,
)
