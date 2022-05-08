package dev.nycode.omsilauncher.instance

import dev.nycode.omsilauncher.serialization.SerializablePath
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div

@Serializable
data class Instance(
    val name: String,
    val directory: SerializablePath,
    val patchVersion: PatchVersion,
) {

    val executable: Path
        get() = directory / "Omsi.exe"

    @Serializable
    enum class PatchVersion(val executable: String) {
        BI_ARTICULATED_BUS_VERSION("Omsi_current.exe"),
        TRAM_VERSION("Omsi_older.exe");

        val relativePath: Path
            get() = Path("_Straßenbahn") / executable
    }

}

@Serializable
enum class LaunchFlag(val cliName: String) {
    EDITOR("editor"),
    SAVE_LOGS("savelogs"),
    NO_LOG("nolog"),
    LOG_ALL("logall"),
    DEBUG("debug"),
    WINDOWED("windowed"),
    FULLSCREEN("fullscreen");

    val option: String
        get() = "-$cliName"
}
