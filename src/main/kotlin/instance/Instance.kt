package dev.nycode.omsilauncher.instance

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.TablerIcons
import compose.icons.tablericons.Bus
import compose.icons.tablericons.Train
import dev.nycode.omsilauncher.serialization.SerializablePath
import dev.nycode.omsilauncher.serialization.SerializableUUID
import dev.nycode.omsilauncher.util.activateAndStartInstallationSafe
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div

@Serializable
data class Instance(
    val id: SerializableUUID,
    val name: String,
    val directory: SerializablePath,
    val patchVersion: PatchVersion,
    val options: Options = Options(),
) {
    suspend fun start(editor: Boolean = false) {
        val flags = buildList {
            addAll(options.toLaunchFlags())
            if (editor) {
                add(LaunchFlag.EDITOR)
            }
        }
        activateAndStartInstallationSafe(directory, flags)
    }

    fun toViewModel(): InstanceViewModel {
        return InstanceViewModel(id, name, patchVersion, options)
    }

    fun applyFromViewModel(model: InstanceViewModel): Instance {
        return copy(id = model.id,
            name = model.name,
            patchVersion = model.patchVersion,
            options = model.options)
    }

    @Serializable
    data class Options(
        val saveLogs: Boolean = false,
        val debugMode: Boolean = false,
        val logLevel: LogLevel = LogLevel.DEFAULT,
        val screenMode: ScreenMode = ScreenMode.DEFAULT,
    ) {

        @Serializable
        enum class LogLevel(val launchFlag: LaunchFlag? = null) {
            DEFAULT,
            NO_LOG(LaunchFlag.NO_LOG),
            FULL_LOG(LaunchFlag.LOG_ALL)
        }

        @Serializable
        enum class ScreenMode(val launchFlag: LaunchFlag? = null) {
            DEFAULT,
            WINDOWED(LaunchFlag.WINDOWED),
            FULL_SCREEN(LaunchFlag.FULLSCREEN)
        }

        fun toLaunchFlags(): List<LaunchFlag> = buildList {
            if (saveLogs) {
                add(LaunchFlag.SAVE_LOGS)
            }
            if (debugMode) {
                add(LaunchFlag.DEBUG)
            }
            if (logLevel.launchFlag != null) {
                add(logLevel.launchFlag)
            }

            if (screenMode.launchFlag != null) {
                add(screenMode.launchFlag)
            }
        }
    }

    enum class PatchVersion(val executable: String, val icon: ImageVector) {
        BI_ARTICULATED_BUS_VERSION("Omsi_current.exe", TablerIcons.Bus),
        TRAM_VERSION("Omsi_older.exe", TablerIcons.Train);

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
