package dev.nycode.omsilauncher.instance

import dev.nycode.omsilauncher.config.config
import dev.nycode.omsilauncher.config.configJson
import dev.nycode.omsilauncher.config.resolveAppDataPath
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.util.UUID
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

private val instancesJson get() = resolveAppDataPath("instances.json")

private val newBaseInstance: Instance
    get() = Instance(
        UUID.randomUUID(),
        "<base instance>",
        config.gameDirectory,
        Instance.PatchVersion.BI_ARTICULATED_BUS_VERSION,
        state = InstanceState.READY,
        uses4GBPatch = true,
        isBaseInstance = true
    )

fun loadInstances(): List<Instance> {
    return if (instancesJson.exists()) {
        val instances = configJson.decodeFromStream<List<SavedInstance>>(instancesJson.inputStream())
            .map(SavedInstance::toInstance)
        return try {
            instances.baseInstance
            instances
        } catch (e: IllegalStateException) {
            val newInstances = instances + newBaseInstance
            saveInstances(newInstances)
            newInstances
        }
    } else {
        saveInstances(listOf(newBaseInstance))
    }
}

fun saveInstances(instances: List<Instance>): List<Instance> {
    instancesJson.outputStream().use {
        configJson.encodeToStream(instances.toSet().map(Instance::toSavedData), it)
    }
    return instances
}
