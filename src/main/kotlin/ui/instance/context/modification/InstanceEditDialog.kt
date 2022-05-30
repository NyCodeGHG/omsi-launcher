package dev.nycode.omsilauncher.ui.instance.context.modification

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.instance.Instance

@Composable
fun InstanceEditDialog(
    instance: Instance,
    onCloseRequest: () -> Unit,
    saveInstance: (InstanceModificationState) -> Unit,
) {
    val strings = LocalStrings.current
    val title = if (instance.isBaseInstance) {
        strings.editBaseInstance
    } else {
        strings.editInstanceTitle(instance.name)
    }
    InstanceDialog(
        title,
        title,
        instance,
        true,
        instance.isBaseInstance,
        onCloseRequest = onCloseRequest,
        saveButtonLabel = {
            Text(strings.save)
        },
        onUpdate = saveInstance
    )
}
