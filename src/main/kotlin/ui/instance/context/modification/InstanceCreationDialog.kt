package dev.nycode.omsilauncher.ui.instance.context.modification

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.instance.Instance
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.notExists

@Composable
fun InstanceCreationDialog(
    mainInstance: Instance,
    instances: List<Instance>,
    onCloseRequest: () -> Unit,
    createInstance: (InstanceModificationState) -> Unit,
) {
    val strings = LocalStrings.current
    fun InstanceModificationState.isValid(): Boolean {
        return name.isNotBlank() && (
            path?.isDirectory() == true && path?.listDirectoryEntries()
                ?.isEmpty() == true
            ) || path?.notExists() == true
    }

    InstanceDialog(
        strings.createInstance,
        strings.createANewInstance,
        mainInstance,
        disableFolderInput = false,
        disableNameInput = false,
        disableParentInput = false,
        instances = instances,
        isValid = { isValid() },
        saveButtonLabel = { Text(strings.createInstance) },
        onCloseRequest = onCloseRequest,
        onUpdate = createInstance
    )
}
