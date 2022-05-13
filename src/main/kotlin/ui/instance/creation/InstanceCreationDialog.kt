package dev.nycode.omsilauncher.ui.instance.creation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import dev.nycode.omsilauncher.ui.components.DropdownInputField
import dev.nycode.omsilauncher.ui.components.PathInputField
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries

@Composable
fun InstanceCreationDialog(
    onCloseRequest: () -> Unit,
    createInstance: (name: String, path: Path, patchVersion: Instance.PatchVersion, use4gbPatch: Boolean) -> Unit,
) {
    val strings = LocalStrings.current
    val (nameField, setNameField) = remember { mutableStateOf("") }
    val (path, setPath) = remember { mutableStateOf<Path?>(null) }
    val (patchVersion, setPatchVersion) = remember { mutableStateOf(Instance.PatchVersion.BI_ARTICULATED_BUS_VERSION) }
    val (use4gbPatch, setUse4gbPatch) = remember { mutableStateOf(true) }
    val dialogState = rememberDialogState(height = 600.dp)
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
                setUse4gbPatch = setUse4gbPatch
            )
            Button({
                onCloseRequest()
                createInstance(nameField, path!!, patchVersion, use4gbPatch)
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
        ) {
            val strings = LocalStrings.current
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.createANewInstance,
                    style = MaterialTheme.typography.h3,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(30.dp))
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        strings.patchVersion,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    DropdownInputField(
                        value = patchVersion,
                        onValueChange = setPatchVersion,
                        modifier = Modifier.width(280.dp),
                        values = Instance.PatchVersion.VALUES
                    ) {
                        Text(it.translation(strings))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(use4gbPatch, setUse4gbPatch)
                    Text(strings.use4gbPatch)
                }
            }
        }
        