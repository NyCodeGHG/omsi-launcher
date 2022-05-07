package dev.nycode.omsilauncher.instance

import dev.nycode.omsilauncher.serialization.SerializablePath
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.div

@Serializable
data class Instance(val name: String, val directory: SerializablePath, val patchVersion: PatchVersion) {

    val executable: Path
        get() = directory / "_Straßenbahn" / patchVersion.executable

    @Serializable
    enum class PatchVersion(val executable: String) {
        BI_ARTICULATED_BUS_VERSION("Omsi_current.exe"),
        TRAM_VERSION("Omsi_older.exe")
    }
}
