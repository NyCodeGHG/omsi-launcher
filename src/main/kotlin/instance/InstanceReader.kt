package dev.nycode.omsilauncher.instance

import dev.nycode.omsilauncher.config.configJson
import dev.nycode.omsilauncher.config.resolveAppDataPath
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

lateinit var instances: List<Instance>
    private set

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
