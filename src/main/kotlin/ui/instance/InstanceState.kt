package dev.nycode.omsilauncher.ui.instance

import androidx.compose.runtime.*
import dev.nycode.omsilauncher.instance.*
import dev.nycode.omsilauncher.omsi.createInstance
import dev.nycode.omsilauncher.omsi.reLinkOmsiExecutable
import dev.nycode.omsilauncher.ui.instance.context.modification.InstanceModificationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists
import kotlin.io.path.moveTo
import kotlin.io.path.notExists
import kotlin.streams.asSequence

class ApplicationInstanceState {

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
        val instance = Instance(
            id,
            name,
            directory,
            patchVersion,
            options,
            InstanceState.CREATING,
            uses4GBPatch
        )
        internalInstances = internalInstances + instance
        if (directory.notExists()) {
            directory.createDirectories()
        }
        directory.resolve("launcher.lock").createFile()
        icon?.moveTo(instance.icon)
        createInstance(instance)
        internalInstances = internalInstances - instance + instance.copy(state = InstanceState.READY)
        persistInstances(internalInstances)
    }

    suspend fun updateInstance(id: UUID, state: InstanceModificationState) = withContext(Dispatchers.IO) {
        val oldInstance = internalInstances.first { it.id == id }
        val oldInstances = internalInstances - oldInstance
        val newInstance = oldInstance.copy(
            name = state.name,
            options = InstanceOptions(state.saveLogs, state.useDebugMode, state.logLevel, state.screenMode),
            uses4GBPatch = state.use4gbPatch,
            patchVersion = state.patchVersion,
            state = InstanceState.UPDATING
        )
        internalInstances = oldInstances + newInstance

        if (oldInstance.patchVersion != newInstance.patchVersion || oldInstance.uses4GBPatch != newInstance.uses4GBPatch) {
            reLinkOmsiExecutable(newInstance)
        }
        if (state.icon == null) {
            newInstance.icon.deleteIfExists()
        } else {
            state.icon?.moveTo(newInstance.icon)
        }

        internalInstances = persistInstances(oldInstances + newInstance.copy(state = InstanceState.READY))
    }

    fun persistInstances(instances: List<Instance>): List<Instance> {
        return saveInstances(instances)
    }
}

@Composable
fun rememberApplicationInstanceState() = remember { ApplicationInstanceState() }
