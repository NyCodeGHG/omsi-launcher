package dev.nycode.omsilauncher.ui.instance.context.modification

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.config.config
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.InstanceOptions
import dev.nycode.omsilauncher.instance.baseInstance
import dev.nycode.omsilauncher.localization.Strings
import dev.nycode.omsilauncher.localization.Translatable
import dev.nycode.omsilauncher.ui.components.CheckboxRow
import dev.nycode.omsilauncher.ui.components.DropdownInputField
import dev.nycode.omsilauncher.ui.components.EmptyDirectoryPathField
import dev.nycode.omsilauncher.ui.components.SafeInstanceIcon
import dev.nycode.omsilauncher.ui.components.TooltipText
import dev.nycode.omsilauncher.ui.components.TooltipWrapper
import dev.nycode.omsilauncher.util.chooseImage
import dev.nycode.omsilauncher.util.sanitize
import kotlinx.coroutines.launch
import kotlin.io.path.absolutePathString
import kotlin.io.path.div

@Composable
fun InstanceDialog(
    dialogTitle: String,
    formTitle: String,
    parentInstance: Instance,
    stateParent: Instance = parentInstance,
    disableFolderInput: Boolean = false,
    disableNameInput: Boolean = false,
    disableParentInput: Boolean = false,
    isEdit: Boolean = false,
    hideParentInput: Boolean = false,
    instances: List<Instance>,
    isValid: InstanceModificationState.() -> Boolean = { true },
    saveButtonLabel: @Composable () -> Unit,
    onCloseRequest: () -> Unit,
    onUpdate: (InstanceModificationState) -> Unit,
) {
    val dialogState = rememberDialogState(height = 780.dp, width = 700.dp)
    val instanceModificationState = remember { InstanceModificationState(stateParent, isEdit) }
    Dialog(
        onCloseRequest = onCloseRequest,
        title = dialogTitle,
        state = dialogState
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            InstanceForm(
                formTitle,
                instanceModificationState,
                disableFolderInput,
                disableNameInput,
                disableParentInput,
                hideParentInput,
                instances
            )
            Button({
                onCloseRequest()
                onUpdate(instanceModificationState)
            }, modifier = Modifier.align(Alignment.BottomCenter), enabled = isValid(instanceModificationState)) {
                saveButtonLabel()
            }
        }
    }
}

@Composable
private fun InstanceForm(
    title: String,
    instanceModificationState: InstanceModificationState,
    disableFolderInput: Boolean = false,
    disableNameInput: Boolean = false,
    disableParentInput: Boolean = false,
    hideParentInput: Boolean = false,
    instances: List<Instance>
) = with(instanceModificationState) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var isChoosing by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
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
                val changeImage = Modifier.pointerInput(icon) {
                    detectTapGestures(onTap = {
                        if (!isChoosing) {
                            isChoosing = true
                            scope.launch {
                                val image = chooseImage(null, strings)
                                isChoosing = false
                                icon = image
                            }
                        }
                    })
                }

                TooltipWrapper(tooltip = { TooltipText(strings.clickToChangeInstanceIcon) }) {
                    SafeInstanceIcon(changeImage, icon, strings.instanceIcon)
                }
                Column {
                    Button({ icon = null }) {
                        Text(strings.reset)
                    }
                }
                Row(Modifier.fillMaxWidth(.43f)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (!disableFolderInput) {
                                path = config.instancesDirectory / name.sanitize()
                            }
                        },
                        placeholder = {
                            Text(strings.newInstance)
                        },
                        label = {
                            Text(strings.instanceName)
                        },
                        singleLine = true,
                        enabled = !disableNameInput
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(.43f)) {
                    EmptyDirectoryPathField(
                        value = customPath ?: path,
                        onValueChange = { customPath = it },
                        enabled = !disableFolderInput,
                        label = {
                            Text(strings.instanceDirectory)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (!hideParentInput) {
                    DropDownColumn(
                        title = "Parent instance",
                        strings = strings,
                        enabled = !disableParentInput,
                        value = parentInstance?.let { id -> instances.firstOrNull { it.id == id } }
                            ?: instances.baseInstance,
                        onValueChange = { parentInstance = it.id },
                        values = instances
                    )
                }
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
    enabled: Boolean = true,
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
            values = values,
            enabled = enabled
        ) {
            Text(it.translation(strings))
        }
    }
}
