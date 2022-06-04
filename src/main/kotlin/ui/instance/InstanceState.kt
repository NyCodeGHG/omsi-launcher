package dev.nycode.omsilauncher.ui.instance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.nycode.omsilauncher.config.config
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.InstanceOptions
import dev.nycode.omsilauncher.instance.InstanceState
import dev.nycode.omsilauncher.instance.loadInstances
import dev.nycode.omsilauncher.instance.saveInstances
import dev.nycode.omsilauncher.omsi.createInstance
import dev.nycode.omsilauncher.omsi.reLinkOmsiExecutable
import dev.nycode.omsilauncher.ui.instance.context.modification.InstanceModificationState
import dev.nycode.omsilauncher.util.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.deleteExisting
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.notExists
import kotlin.io.path.readBytes
import kotlin.streams.asSequence

class ApplicationInstanceState {

    private val logger = logger()

    private var internalInstances: List<Instance> by mutableStateOf(loadInstances())
    val instances: List<Instance> get() = internalInstances.sortedByDescending { it.id }

    suspend fun deleteInstance(instance: Instance) = withContext(Dispatchers.IO) {
        internalInstances = instances - instance + instance.copy(state = InstanceState.DELETING)
        Files.walk(instance.directory).use {
            it.asSequence()
                .sortedDescending()
                .forEach(Path::deleteExisting)
        }
        internalInstances = internalInstances.filter { it.id != instance.id }
        persistInstances(internalInstances)
    }

    suspend fun createNewInstance(
        id: UUID,
        name: String,
        directory: Path,
        patchVersion: Instance.PatchVersion,
        options: InstanceOptions = InstanceOptions(),
        uses4GBPatch: Boolean,
        icon: Path?
    ) = withContext(Dispatchers.IO) {
        val iconPath = checkImageInIconStore(icon)
        val instance = Instance(
            id,
            name,
            directory,
            patchVersion,
            options,
            InstanceState.CREATING,
            uses4GBPatch,
            icon = iconPath
        )
        internalInstances = internalInstances + instance
        if (directory.notExists()) {
            directory.createDirectories()
        }
        directory.resolve("launcher.lock").createFile()
        createInstance(instance)
        internalInstances = internalInstances - instance + instance.copy(state = InstanceState.READY)
        persistInstances(internalInstances)
    }

    private fun checkImageInIconStore(icon: Path?): Path? {
        val iconPath = if (icon != null) {
            config.imagesDirectory.createDirectories()
            val extension = icon.extension
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update(icon.readBytes())
            val hash = Base64.getEncoder().encodeToString(digest.digest())
            logger.info("Hash of $icon is $hash.")
            val fileName = "$hash.$extension"
            val path = config.imagesDirectory / fileName
            if (path.exists()) {
                logger.info("Image is already in image store.")
            } else {
                logger.info("Copying $icon to $path.")
                icon.copyTo(path)
            }
            path
        } else null
        return iconPath
    }

    suspend fun updateInstance(id: UUID, state: InstanceModificationState) = withContext(Dispatchers.IO) {
        val oldInstance = internalInstances.first { it.id == id }
        val oldInstances = internalInstances - oldInstance
        val icon = checkImageInIconStore(state.icon)
        val newInstance = oldInstance.copy(
            name = state.name,
            options = InstanceOptions(state.saveLogs, state.useDebugMode, state.logLevel, state.screenMode),
            uses4GBPatch = state.use4gbPatch,
            patchVersion = state.patchVersion,
            state = InstanceState.UPDATING,
            icon = icon
        )
        internalInstances = oldInstances + newInstance

        if (oldInstance.patchVersion != newInstance.patchVersion || oldInstance.uses4GBPatch != newInstance.uses4GBPatch) {
            reLinkOmsiExecutable(newInstance)
        }

        internalInstances = persistInstances(oldInstances + newInstance.copy(state = InstanceState.READY))
    }

    fun persistInstances(instances: List<Instance>): List<Instance> {
        return saveInstances(instances)
    }
}

@Composable
fun rememberApplicationInstanceState() = remember { ApplicationInstanceState() }
