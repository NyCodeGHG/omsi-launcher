package dev.nycode.omsilauncher.ui.instance.context.modification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.InstanceOptions
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.exists

class InstanceModificationState(parentInstance: Instance? = null, isEdit: Boolean = false) {
    var icon: Path? by mutableStateOf(if (parentInstance?.icon?.exists() == true) parentInstance.icon else null)
    var name: String by mutableStateOf(parentInstance?.name.takeIf { parentInstance?.isBaseInstance == false || isEdit } ?: "")
    var path: Path? by mutableStateOf(parentInstance?.directory.takeIf { parentInstance?.isBaseInstance == false || isEdit })
    var customPath: Path? by mutableStateOf(null)
    var patchVersion: Instance.PatchVersion by mutableStateOf(
        parentInstance?.patchVersion ?: Instance.PatchVersion.BI_ARTICULATED_BUS_VERSION
    )
    var use4gbPatch: Boolean by mutableStateOf(parentInstance?.uses4GBPatch ?: true)
    var saveLogs: Boolean by mutableStateOf(parentInstance?.options?.saveLogs ?: false)
    var useDebugMode: Boolean by mutableStateOf(parentInstance?.options?.debugMode ?: false)
    var logLevel: InstanceOptions.LogLevel by mutableStateOf(
        parentInstance?.options?.logLevel ?: InstanceOptions.LogLevel.DEFAULT
    )
    var screenMode: InstanceOptions.ScreenMode by mutableStateOf(
        parentInstance?.options?.screenMode ?: InstanceOptions.ScreenMode.DEFAULT
    )
    var parentInstance: UUID? by mutableStateOf(parentInstance?.id)
}
