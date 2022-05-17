package dev.nycode.omsilauncher.ui.instance.creation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import kotlin.io.path.absolutePathString
import kotlin.io.path.listDirectoryEntries

@Composable
fun InstanceCreationDialog(
    onCloseRequest: () -> Unit,
    createInstance: (InstanceCreationState) -> Unit,
) {
    val strings = LocalStrings.current
    val dialogState = rememberDialogState(height = 620.dp, width = 700.dp)
    val instanceCreationState = remember { InstanceCreationState() }
    fun isValid(): Boolean = with(instanceCreationState) {
        name.isNotBlank() && path?.listDirectoryEntries()?.isEmpty() == true
    }
    Dialog(
        onCloseRequest = onCloseRequest,
        title = strings.createANewInstance,
        state = dialogState
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            InstanceCreationForm(instanceCreationState)
            Button({
                onCloseRequest()
                createInstance(instanceCreationState)
            }, modifier = Modifier.align(Alignment.BottomCenter), enabled = isValid()) {
                    Text(strings.createInstance)
                }
                }
            }
        }

        @Composable
        private fun InstanceCreationForm(
            instanceCreationState: InstanceCreationState,
        ) = with(instanceCreationState) {
            val strings = LocalStrings.current
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = strings.createANewInstance,
                    style = MaterialTheme.typography.h3,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = {
                                Text(strings.newInstance)
                            },
                            label = {
                                Text(strings.instanceName)
                            },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PathInputField(
                            value = path,
                            requiresEmptyDirectory = true,
                            onValueChange = { path = it },
                            label = {
                                Text(strings.instanceDirectory)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DropDownColumn(
                            title = strings.patchVersion,
                            strings = strings,
                            value = patchVersion,
                            onValueChange = { patchVersion = it },
                            values = Instance.PatchVersion.VALUES
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DropDownColumn(
                            title = strings.logLevel,
                            strings = strings,
                            value = logLevel,
                            onValueChange = { logLevel = it },
                            values = InstanceOptions.LogLevel.VALUES
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DropDownColumn(
                            title = strings.screenMode,
                            strings = strings,
                            value = screenMode,
                            onValueChange = { screenMode = it },
                            values = InstanceOptions.ScreenMode.VALUES
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Column(Modifier.padding(top = 8.dp)) {
                        CheckboxRow(
                            title = strings.use4gbPatch,
                            tooltip = strings.use4gbPatchTooltip,
                            value = use4gbPatch
                        ) { use4gbPatch = it }
                        CheckboxRow(
                            title = strings.saveLogs,
                            tooltip = strings.saveLogsTooltip(path?.absolutePathString()),
                            value = saveLogs
                        ) { saveLogs = it }
                        CheckboxRow(
                            title = strings.debugMode,
                            tooltip = strings.debugModeTooltip,
                            value = useDebugMode
                        ) { useDebugMode = it }
                    }
                }
            }
        }

        @Composable
        private fun <E : Translatable> DropDownColumn(
            title: String,
            strings: Strings,
            value: E,
            onValueChange: (E) -> Unit,
            values: Collection<E>,
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
                    value = value,
                    onValueChange = onValueChange,
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
            value: Boolean,
            onValueChange: (Boolean) -> Unit,
        ) {
            TooltipWrapper(tooltip = { TooltipText(tooltip) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(value, onValueChange)
                    Text(title)
                }
            }
        }
