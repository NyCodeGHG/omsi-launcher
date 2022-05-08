package dev.nycode.omsilauncher.instance

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import dev.nycode.omsilauncher.config.configJson
import dev.nycode.omsilauncher.config.resolveAppDataPath
import dev.nycode.omsilauncher.util.createInstance
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import kotlin.streams.asSequence

var instances: List<Instance> = emptyList()
    private set(value) {
        field = value
        instanceViewModel.value = field.map(Instance::toViewModel)
    }

val instanceViewModel: MutableState<List<InstanceViewModel>> = mutableStateOf(emptyList())

private val instancesJson get() = resolveAppDataPath("instances.json")

fun initializeInstances() {
    if (instancesJson.exists()) {
        instances = configJson.decodeFromStream(instancesJson.inputStream())
    } else {
        saveInstances(emptyList())
    }
}

fun saveInstances(instances: List<Instance>) {
    dev.nycode.omsilauncher.instance.instances = instances
    instancesJson.outputStream().use {
        configJson.encodeToStream(instances, it)
    }
}

fun addInstances(vararg instance: Instance) = saveInstances(instances + instance)

fun deleteInstance(instance: Instance) {
    Files.walk(instance.directory).use {
        it.asSequence()
            .sortedDescending()
            .forEach(Path::deleteExisting)
    }
    saveInstances(instances - instance)
}

fun getInstanceById(id: UUID): Instance? {
    return instances.find { it.id == id }
}

suspend fun createNewInstance(instance: Instance) {
    createInstance(instance)
    addInstances(instance)
}
