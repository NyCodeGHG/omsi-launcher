package dev.nycode.omsilauncher.instance

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.TablerIcons
import compose.icons.tablericons.Bus
import compose.icons.tablericons.Train
import dev.nycode.omsilauncher.config.PersistentValue
import dev.nycode.omsilauncher.localization.Strings
import dev.nycode.omsilauncher.localization.Translatable
import dev.nycode.omsilauncher.omsi.activateAndStartInstallationSafe
import dev.nycode.omsilauncher.omsi.activateInstallationSafe
import dev.nycode.omsilauncher.serialization.SerializablePath
import dev.nycode.omsilauncher.serialization.SerializableUUID
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
    val uses4GBPatch: Boolean,
    val isBaseInstance: Boolean = false,
    val icon: Path? = null,
    val baseInstance: SerializableUUID? = null
) : PersistentValue<SavedInstance>, Translatable {
    val manifest: Path get() = directory / "manifest.acf"
    val manifestBackup: Path get() = directory / "manifest.backup.acf"

    override val translation: Strings.() -> String
        get() = { if (isBaseInstance) baseInstance else name }

    suspend fun start(editor: Boolean = false, awaitSteamDeath: suspend () -> Boolean = { true }) {
        val flags = buildList {
            addAll(options.toLaunchFlags())
            if (editor) {
                add(LaunchFlag.EDITOR)
            }
        }
        activateAndStartInstallationSafe(this, flags, awaitSteamDeath)
    }

    suspend fun activate(awaitSteamDeath: suspend () -> Boolean = { true }) {
        activateInstallationSafe(this, true, awaitSteamDeath)
    }

    enum class PatchVersion(
        val type: String,
        val icon: ImageVector,
        override val translation: Strings.() -> String,
    ) : Translatable {
        BI_ARTICULATED_BUS_VERSION("current", TablerIcons.Bus, { biArticulatedBusPatch }),
        TRAM_VERSION("older", TablerIcons.Train, { tramPatch });

        val executable: String
            get() = "Omsi_$type.exe"

        val relativePath: Path
            get() = Path("_Straßenbahn") / executable

        companion object {
            val VALUES = values().toList()
        }
    }

    override fun toSavedData(): SavedInstance {
        return SavedInstance(
            id,
            name,
            directory,
            patchVersion,
            options,
            uses4GBPatch,
            isBaseInstance,
            icon = icon,
            baseInstance = baseInstance
        )
    }
}

val List<Instance>.baseInstance: Instance
    get() = firstOrNull { it.isBaseInstance } ?: error("Base instance not found")

fun List<Instance>.calculateReLinkRoundsFor(instance: Instance): Map<Instance, List<Instance>> {
    val directDependencies =
        filter { it.baseInstance == instance.id || (instance.isBaseInstance && it.baseInstance == null && !it.isBaseInstance) }
    val childDependencies = directDependencies.map {
        it to calculateReLinkRoundsFor(it).values.flatten()
    }.filterNot { (_, children) -> children.isEmpty() }

    return buildMap {
        this[instance] = directDependencies
        putAll(childDependencies.toMap())
    }
}

fun List<Instance>.findDependenciesFor(instance: Instance) = filter {
    it.hasDependencyTo(instance, this)
}

fun Instance.hasDependencyTo(instance: Instance, instances: List<Instance>): Boolean {
    if (baseInstance == null) return instance.isBaseInstance && !isBaseInstance
    val parent = instances.first { it.id == baseInstance }
    return if (parent.id == instance.id) {
        true
    } else {
        parent.hasDependencyTo(instance, instances)
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
