package dev.nycode.omsilauncher.app

import androidx.compose.runtime.*
import dev.nycode.omsilauncher.instance.*
import dev.nycode.omsilauncher.util.createInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.deleteExisting
import kotlin.streams.asSequence

class ApplicationState {

    var instances: List<Instance> by mutableStateOf(loadInstances())
        private set

    suspend fun deleteInstance(instance: Instance) = withContext(Dispatchers.IO) {
        instances = instances - instance + instance.copy(state = InstanceState.DELETING)
        Files.walk(instance.directory).use {
            it.asSequence()
                .sortedDescending()
                .forEach(Path::deleteExisting)
        }
        instances = instances.filter { it.id != instance.id }
        persistInstances(instances)
    }

    suspend fun createNewInstance(
        id: UUID,
        name: String,
        directory: Path,
        patchVersion: Instance.PatchVersion,
        options: InstanceOptions = InstanceOptions(),
    ) = withContext(Dispatchers.IO) {
        val instance = Instance(id, name, directory, patchVersion, options, InstanceState.CREATING)
        instances = instances + instance
        createInstance(instance)
        instances = instances - instance + instance.copy(state = InstanceState.READY)
        persistInstances(instances)
    }

    fun persistInstances(instances: List<Instance>): List<Instance> {
        return saveInstances(instances)
    }

    operator fun component1() = instances
}

@Composable
fun rememberApplicationState() = remember { ApplicationState() }
