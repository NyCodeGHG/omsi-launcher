package dev.nycode.omsilauncher.ui.instance.creation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.InstanceOptions
import java.nio.file.Path

class InstanceCreationState {
    var name: String by mutableStateOf("")
    var path: Path? by mutableStateOf(null)
    var customPath: Path? by mutableStateOf(null)
    var patchVersion: Instance.PatchVersion by mutableStateOf(Instance.PatchVersion.BI_ARTICULATED_BUS_VERSION)
    var use4gbPatch: Boolean by mutableStateOf(true)
    var saveLogs: Boolean by mutableStateOf(false)
    var useDebugMode: Boolean by mutableStateOf(false)
    var logLevel: InstanceOptions.LogLevel by mutableStateOf(InstanceOptions.LogLevel.DEFAULT)
    var screenMode: InstanceOptions.ScreenMode by mutableStateOf(InstanceOptions.ScreenMode.DEFAULT)
}
