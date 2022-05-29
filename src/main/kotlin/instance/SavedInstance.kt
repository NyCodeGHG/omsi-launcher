package dev.nycode.omsilauncher.instance

import dev.nycode.omsilauncher.serialization.SerializablePath
import dev.nycode.omsilauncher.serialization.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class SavedInstance(
    val id: SerializableUUID,
    val name: String,
    val directory: SerializablePath,
    val patchVersion: Instance.PatchVersion,
    val options: InstanceOptions,
    val uses4GBPatch: Boolean,
    val isBaseInstance: Boolean = false,
    val manifestHash: String? = null
) {
    fun toInstance(): Instance {
        return Instance(
            id,
            name,
            directory,
            patchVersion,
            options,
            InstanceState.READY,
            uses4GBPatch,
            isBaseInstance
        )
    }
}
