package dev.nycode.omsilauncher.ui.instance.context.modification

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import dev.nycode.omsilauncher.instance.Instance

@Composable
fun InstanceEditDialog(
    instance: Instance,
    onCloseRequest: () -> Unit,
    saveInstance: (InstanceModificationState) -> Unit,
) {
    InstanceDialog(
        "Instanz ${instance.name} bearbeiten",
        "Instanz ${instance.name} bearbeiten",
        instance,
        true,
        onCloseRequest = onCloseRequest,
        saveButtonLabel = {
            Text("Speichern")
        },
        onUpdate = saveInstance
    )
}
