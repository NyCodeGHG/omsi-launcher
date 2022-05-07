package dev.nycode.omsilauncher.config

import dev.nycode.omsilauncher.serialization.SerializablePath
import kotlinx.serialization.Serializable

@Serializable
data class Configuration(val rootInstallation: SerializablePath?)
