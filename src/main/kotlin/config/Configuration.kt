package dev.nycode.omsilauncher.config

import dev.nycode.omsilauncher.serialization.SerializablePath
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class Configuration(val rootInstallation: SerializablePath) : PersistentValue<Configuration> {
    override fun toSavedData(): Configuration = this

    val gameDirectory: Path
        get() = rootInstallation.resolve("game")

    val instancesDirectory: Path
        get() = rootInstallation.resolve("instances")
}
