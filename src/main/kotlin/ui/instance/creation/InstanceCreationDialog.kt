package dev.nycode.omsilauncher.ui.instance.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.InstanceOptions
import dev.nycode.omsilauncher.localization.Strings
import dev.nycode.omsilauncher.localization.Translatable
import dev.nycode.omsilauncher.ui.components.DropdownInputField
import dev.nycode.omsilauncher.ui.components.PathInputField
import dev.nycode.omsilauncher.ui.components.TooltipText
import dev.nycode.omsilauncher.ui.components.TooltipWrapper
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.listDirectoryEntries

@Composable
fun InstanceCreationDialog(
    onCloseRequest: () -> Unit,
    createInstance: (name: String, path: Path, patchVersion: Instance.PatchVersion, use4gbPatch: Boolean, options: InstanceOptions) -> Unit,
) {
    val strings = LocalStrings.current
    val (nameField, setNameField) = remember { mutableStateOf("") }
    val (path, setPath) = remember { mutableStateOf<Path?>(null) }
    val (patchVersion, setPatchVersion) = remember { mutableStateOf(Instance.PatchVersion.BI_ARTICULATED_BUS_VERSION) }
    val (use4gbPatch, setUse4gbPatch) = remember { mutableStateOf(true) }
    val (saveLogs, setSaveLogs) = remember { mutableStateOf(false) }
    val (useDebugMode, setUseDebugMode) = remember { mutableStateOf(false) }
    val (logLevel, setLogLevel) = remember { mutableStateOf(InstanceOptions.LogLevel.DEFAULT) }
    val (screenMode, setScreenMode) = remember { mutableStateOf(InstanceOptions.ScreenMode.DEFAULT) }
    val dialogState = rememberDialogState(height = 620.dp, width = 700.dp)
    fun isValid(): Boolean {
        return nameField.isNotBlank() && path != null && path.listDirectoryEntries().isEmpty()
    }
    Dialog(
        onCloseRequest = onCloseRequest,
        title = strings.createANewInstance,
        state = dialogState
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            InstanceCreationForm(
                name = nameField,
                setName = setNameField,
                path = path,
                setPath = setPath,
                patchVersion = patchVersion,
                setPatchVersion = setPatchVersion,
                use4gbPatch = use4gbPatch,
                setUse4gbPatch = setUse4gbPatch,
                saveLogs = saveLogs,
                setSaveLogs = setSaveLogs,
                useDebugMode = useDebugMode,
                setUseDebugMode = setUseDebugMode,
                logLevel = logLevel,
                setLogLevel = setLogLevel,
                screenMode = screenMode,
                setScreenMode = setScreenMode
            )
            Button({
                onCloseRequest()
                createInstance(
                    nameField,
                    path!!,
                    patchVersion,
                    use4gbPatch,
                    InstanceOptions(saveLogs, useDebugMode, logLevel, screenMode)
                )
            }, modifier = Modifier.align(Alignment.BottomCenter), enabled = isValid()) {
                    Text(strings.createInstance)
                }
                }
            }
        }

        @Composable
        private fun InstanceCreationForm(
            name: String,
            setName: (String) -> Unit,
            path: Path?,
            setPath: (Path?) -> Unit,
            patchVersion: Instance.PatchVersion,
            setPatchVersion: (Instance.PatchVersion) -> Unit,
            use4gbPatch: Boolean,
            setUse4gbPatch: (Boolean) -> Unit,
            saveLogs: Boolean,
            setSaveLogs: (Boolean) -> Unit,
            useDebugMode: Boolean,
            setUseDebugMode: (Boolean) -> Unit,
            logLevel: InstanceOptions.LogLevel,
            setLogLevel: (InstanceOptions.LogLevel) -> Unit,
            screenMode: InstanceOptions.ScreenMode,
            setScreenMode: (InstanceOptions.ScreenMode) -> Unit
        ) {
            val strings = LocalStrings.current
            Column(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = strings.createANewInstance,
                    style = MaterialTheme.typography.h3,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.padding(top = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = setName,
                            placeholder = {
                                Text(strings.newInstance)
                            },
                            label = {
                                Text(strings.instanceName)
                            },
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                        PathInputField(
                            value = path,
                            onValueChange = setPath,
                            label = {
                                Text(strings.instanceDirectory)
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                        DropDownColumn(
                            strings.patchVersion,
                            strings,
                            patchVersion,
                            setPatchVersion,
                            Instance.PatchVersion.VALUES
                        )
                        Spacer(Modifier.height(8.dp))
                        DropDownColumn(strings.logLevel, strings, logLevel, setLogLevel, InstanceOptions.LogLevel.VALUES)
                        Spacer(Modifier.height(8.dp))
                        DropDownColumn(
                            strings.screenMode,
                            strings,
                            screenMode,
                            setScreenMode,
                            InstanceOptions.ScreenMode.VALUES
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    Column(Modifier.padding(top = 8.dp)) {
                        CheckboxRow(strings.use4gbPatch, strings.use4gbPatchTooltip, use4gbPatch, setUse4gbPatch)
                        CheckboxRow(strings.saveLogs, strings.saveLogsTooltip(path?.absolutePathString()), saveLogs, setSaveLogs)
                        CheckboxRow(strings.debugMode, strings.debugModeTooltip, useDebugMode, setUseDebugMode)
                    }
                }
            }
        }

        @Composable
        private fun <E : Translatable> DropDownColumn(
            title: String,
            strings: Strings,
            get: E,
            set: (E) -> Unit,
            values: Collection<E>
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    title,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 8.dp)
                )
                Spacer(Modifier.height(8.dp))
                DropdownInputField(
                    value = get,
                    onValueChange = set,
                    modifier = Modifier.width(280.dp),
                    values = values
                ) {
                    Text(it.translation(strings))
                }
            }
        }

        @Composable
        private fun CheckboxRow(
            title: String,
            tooltip: String,
            get: Boolean,
            set: (Boolean) -> Unit
        ) {
            TooltipWrapper(tooltip = { TooltipText(tooltip) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(get, set)
                    Text(title)
                }
            }
        }
        