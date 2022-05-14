package dev.nycode.omsilauncher.steam

import dev.nycode.omsilauncher.vdf.EmptyVDFObject
import dev.nycode.omsilauncher.vdf.VDFElement
import dev.nycode.omsilauncher.vdf.VDFObject
import dev.nycode.omsilauncher.vdf.VDFPrimitive
import dev.nycode.omsilauncher.vdf.vdfObject
import dev.nycode.omsilauncher.vdf.vdfString

private const val APP_STATE = "AppState"
private const val INSTALLED_DEPOTS = "InstalledDepots"
private const val USER_CONFIG = "UserConfig"
private const val MOUNTED_CONFIG = "MountedConfig"
private const val DISABLED_DLC = "DisabledDLC"

val VDFObject.appState: VDFObject
    get() = this[APP_STATE]!!.vdfObject

val VDFObject.depots
    get() = (this[INSTALLED_DEPOTS]?.vdfObject ?: EmptyVDFObject)

fun VDFObject.mergeManifestWith(main: VDFObject, previousMain: VDFObject): VDFObject {
    val newState = appState.mergeStateWith(main.appState, previousMain.appState)

    return VDFObject(mapOf(APP_STATE to newState))
}

private fun VDFObject.mergeStateWith(main: VDFObject, previousMain: VDFObject): VDFElement {
    val currentDepots = depots
    val newDepots = main.depots
    val previousMainDepot = previousMain.depots

    val removedDepots = currentDepots.filterKeys { key ->
        previousMainDepot.containsKey(key) && !newDepots.containsKey(key)
    }.map { it.key }
    val addedDepots = newDepots.filterKeys { key ->
        !previousMainDepot.containsKey(key)
    }

    return update {
        val depots = VDFObject(currentDepots - removedDepots.toSet() + addedDepots)
        this[INSTALLED_DEPOTS] = depots

        adaptConfig(depots, removedDepots, this, USER_CONFIG)
        adaptConfig(depots, removedDepots, this, MOUNTED_CONFIG)
    }
}

private fun VDFObject.adaptConfig(
    depots: Map<String, VDFElement>,
    removedDepots: List<String>,
    mutableState: MutableMap<String, VDFElement>,
    configName: String
) {
    if (containsKey(configName)) {
        val config = this[configName]!!.vdfObject
        val disabledDLC = config[DISABLED_DLC]?.vdfString
        if (disabledDLC != null) {
            val newConfig = config.update {
                val newDlc = disabledDLC.content.split(",").filter { !depots.containsKey(it) } + removedDepots
                this[DISABLED_DLC] = VDFPrimitive(newDlc.joinToString(","))
            }
            mutableState[configName] = VDFObject(newConfig)
        }
    }
}

private fun VDFObject.update(block: MutableMap<String, VDFElement>.() -> Unit): VDFObject =
    VDFObject(toMutableMap().apply(block).toMap())
