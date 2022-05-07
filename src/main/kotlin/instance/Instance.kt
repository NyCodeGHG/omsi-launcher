package dev.nycode.omsilauncher.instance

import dev.nycode.omsilauncher.serialization.SerializablePath
import kotlinx.serialization.Serializable

@Serializable
data class Instance(val name: String, val directory: SerializablePath)
