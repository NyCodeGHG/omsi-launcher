package dev.nycode.omsilauncher.instance

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.TablerIcons
import compose.icons.tablericons.Bus
import compose.icons.tablericons.Train
import dev.nycode.omsilauncher.config.PersistentValue
import dev.nycode.omsilauncher.serialization.SerializablePath
import dev.nycode.omsilauncher.serialization.SerializableUUID
import dev.nycode.omsilauncher.util.activateAndStartInstallationSafe
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div

data class Instance(
    val id: SerializableUUID,
    val name: String,
    val directory: SerializablePath,
    val patchVersion: PatchVersion,
    val options: InstanceOptions = InstanceOptions(),
    val state: InstanceState,
) : PersistentValue<SavedInstance> {
    suspend fun start(editor: Boolean = false) {
        val flags = buildList {
            addAll(options.toLaunchFlags())
            if (editor) {
                add(LaunchFlag.EDITOR)
            }
        }
        activateAndStartInstallationSafe(directory, flags)
    }

    enum class PatchVersion(val executable: String, val icon: ImageVector) {
        BI_ARTICULATED_BUS_VERSION("Omsi_current.exe", TablerIcons.Bus),
        TRAM_VERSION("Omsi_older.exe", TablerIcons.Train);

        val relativePath: Path
            get() = Path("_Straßenbahn") / executable
    }

    override fun toSavedData(): SavedInstance {
        return SavedInstance(id, name, directory, patchVersion, options)
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
