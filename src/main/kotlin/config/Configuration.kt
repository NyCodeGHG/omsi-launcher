package dev.nycode.omsilauncher.config

import dev.nycode.omsilauncher.serialization.SerializablePath
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class Configuration(val rootInstallation: SerializablePath)

val Configuration.gameDirectory: Path
    get() = rootInstallation.resolve("game")
