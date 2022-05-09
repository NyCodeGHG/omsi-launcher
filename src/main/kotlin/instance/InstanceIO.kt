package dev.nycode.omsilauncher.instance

import dev.nycode.omsilauncher.config.configJson
import dev.nycode.omsilauncher.config.resolveAppDataPath
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

private val instancesJson get() = resolveAppDataPath("instances.json")

fun loadInstances(): List<Instance> {
    return if (instancesJson.exists()) {
        configJson.decodeFromStream<List<SavedInstance>>(instancesJson.inputStream())
            .map(SavedInstance::toInstance)
    } else {
        saveInstances(emptyList())
    }
}

fun saveInstances(instances: List<Instance>): List<Instance> {
    instancesJson.outputStream().use {
        configJson.encodeToStream(instances.map(Instance::toSavedData), it)
    }
    return instances
}
