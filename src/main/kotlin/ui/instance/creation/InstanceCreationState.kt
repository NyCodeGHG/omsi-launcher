package dev.nycode.omsilauncher.ui.instance.creation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.InstanceOptions
import java.nio.file.Path

class InstanceCreationState(mainInstance: Instance? = null) {
    var name: String by mutableStateOf("")
    var path: Path? by mutableStateOf(null)
    var customPath: Path? by mutableStateOf(null)
    var patchVersion: Instance.PatchVersion by mutableStateOf(
        mainInstance?.patchVersion ?: Instance.PatchVersion.BI_ARTICULATED_BUS_VERSION
    )
    var use4gbPatch: Boolean by mutableStateOf(mainInstance?.uses4GBPatch ?: true)
    var saveLogs: Boolean by mutableStateOf(mainInstance?.options?.saveLogs ?: false)
    var useDebugMode: Boolean by mutableStateOf(mainInstance?.options?.debugMode ?: false)
    var logLevel: InstanceOptions.LogLevel by mutableStateOf(
        mainInstance?.options?.logLevel ?: InstanceOptions.LogLevel.DEFAULT
    )
    var screenMode: InstanceOptions.ScreenMode by mutableStateOf(
        mainInstance?.options?.screenMode ?: InstanceOptions.ScreenMode.DEFAULT
    )
}
