package dev.nycode.omsilauncher.ui.components

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.LocalStrings
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.InstanceState
import dev.nycode.omsilauncher.localization.Strings
import dev.nycode.omsilauncher.omsi.OmsiProcessState
import dev.nycode.omsilauncher.omsi.UserAccessControlCancelledException
import dev.nycode.omsilauncher.ui.CustomColors
import dev.nycode.omsilauncher.ui.instance.ApplicationInstanceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.skia.Image.Companion as SkiaImage

@Composable
fun InstanceListEntry(
    modifier: Modifier,
    instanceState: ApplicationInstanceState,
    instance: Instance,
    scope: CoroutineScope,
    omsiState: OmsiProcessState,
    instanceActive: Boolean,
    showUACDialog: () -> Unit,
) {
    val image = remember { imageFromResource("ecitaro.jpg") }
    var deleteDialog by remember { mutableStateOf(false) }
    val strings = LocalStrings.current
    val interactable =
        instance.state == InstanceState.READY && omsiState == OmsiProcessState.NOT_RUNNING

    val startInstance: (Boolean) -> Unit = { editor: Boolean ->
        scope.launch(Dispatchers.IO) {
            instance.start(editor)
        }
    }

    val editInstance = {} // not yet implemented

    val deleteInstance = {
        deleteDialog = true
    }

    val activateInstance: () -> Unit = {
        scope.launch(Dispatchers.IO) {
            try {
                instance.activate()
            } catch (exception: UserAccessControlCancelledException) {
                showUACDialog()
            }
        }
    }

    Card(modifier, elevation = 3.dp) {
        InstanceContextMenuArea(
            onStartInstance = startInstance,
            onEditInstance = editInstance,
            onDeleteInstance = deleteInstance,
            onActivateInstance = activateInstance
        ) {
            Row(Modifier.fillMaxWidth().height(125.dp)) {
                Image(image, strings.eCitaro)
                Spacer(Modifier.padding(5.dp))
                Box(Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.align(Alignment.TopStart).padding(10.dp)) {
                        Text(instance.name, fontSize = 30.sp)
                    }
                    InstanceButtonRow(
                        modifier = Modifier.align(Alignment.BottomStart).padding(10.dp),
                        instance = instance,
                        interactable = interactable,
                        instanceActive = instanceActive,
                        onStartInstance = startInstance,
                        onActivateInstance = activateInstance
                    )
                    Box(
                        modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).fillMaxHeight()
                    ) {
                        InstanceEditButton(
                            modifier = Modifier.align(Alignment.TopEnd),
                            interactable = interactable,
                            onEditInstance = editInstance
                        )
                        InstanceDeleteButton(
                            modifier = Modifier.align(Alignment.BottomEnd),
                            interactable = interactable,
                            onDeleteInstance = deleteInstance
                        )
                    }
                }
            }
        }
    }
    if (deleteDialog) {
        ConfirmationDialog(
            strings.confirmDeletion(instance.name),
            { delete ->
                deleteDialog = false
                if (delete) {
                    scope.launch(Dispatchers.IO) {
                        instanceState.deleteInstance(instance)
                    }
                }
            },
            yesText = strings.yes,
            noText = strings.no,
            title = strings.confirmDeletion(instance.name)
        )
    }
}

@Composable
private fun InstanceDeleteButton(
    modifier: Modifier = Modifier,
    onDeleteInstance: () -> Unit,
    interactable: Boolean,
) {
    val strings = LocalStrings.current
    TooltipWrapper(modifier = modifier, tooltip = {
        TooltipText(strings.deleteInstance)
    }) {
        IconButton(
            onClick = onDeleteInstance,
            enabled = interactable
        ) {
            Icon(TablerIcons.Trash, strings.delete, tint = MaterialTheme.colors.error)
        }
    }
}

@Composable
private fun InstanceEditButton(
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") interactable: Boolean,
    onEditInstance: () -> Unit,
) {
    val strings = LocalStrings.current
    TooltipWrapper(
        tooltip = {
            TooltipText(strings.editInstance)
        },
        modifier = modifier
    ) {
        IconButton(
            onClick = onEditInstance,
            enabled = false // interactable NOT YET IMPLEMENTED
        ) {
            Icon(TablerIcons.Pencil, strings.edit)
        }
    }
}

@Composable
private fun InstanceButtonRow(
    modifier: Modifier = Modifier,
    instance: Instance,
    interactable: Boolean,
    instanceActive: Boolean,
    onStartInstance: (editor: Boolean) -> Unit,
    onActivateInstance: () -> Unit,
) = Row(modifier) {
    val strings = LocalStrings.current
    if (instance.state == InstanceState.CREATING || instance.state == InstanceState.DELETING) {
        LinearProgressIndicator(modifier = Modifier.padding(bottom = 5.dp))
    } else {
        InstanceStartButton(interactable, strings, onStartInstance)
        Spacer(Modifier.width(5.dp))
        InstanceStartEditorButton(interactable, strings, onStartInstance)
        Spacer(Modifier.width(5.dp))
        InstanceActivateButton(
            instanceActive = instanceActive,
            interactable = interactable,
            onActivateInstance = onActivateInstance
        )
        Spacer(Modifier.width(5.dp))
        InstancePatchVersionIcon(
            modifier = Modifier.align(Alignment.Bottom),
            instance = instance
        )
    }
}

@Composable
private fun InstancePatchVersionIcon(
    modifier: Modifier = Modifier,
    instance: Instance,
) {
    val strings = LocalStrings.current
    TooltipWrapper(
        modifier = modifier,
        tooltip = {
            TooltipText(
                text = strings.selectedPatchVersion(
                    instance.patchVersion.translation(
                        strings
                    )
                )
            )
        }
    ) {
        Icon(
            instance.patchVersion.icon,
            strings.instancePatchVersion,
            modifier = modifier.size(50.dp)
        )
    }
}

@Composable
private fun InstanceActivateButton(
    instanceActive: Boolean,
    interactable: Boolean,
    onActivateInstance: () -> Unit,
) {
    val strings = LocalStrings.current
    TooltipWrapper(tooltip = {
        TooltipText(strings.activateInstance)
    }) {
        Button(
            onClick = onActivateInstance,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = Color.White
            ),
            enabled = !instanceActive && interactable
        ) {
            Icon(TablerIcons.Click, strings.activateInstance)
        }
    }
}

@Composable
private fun InstanceStartEditorButton(
    interactable: Boolean,
    strings: Strings,
    onStartInstance: (editor: Boolean) -> Unit,
) {
    TooltipWrapper(tooltip = {
        TooltipText(text = strings.startEditorInstance)
    }) {
        Button(
            onClick = {
                onStartInstance(true)
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White
            ),
            enabled = interactable
        ) {
            Icon(TablerIcons.Tools, strings.runEditor)
        }
    }
}

@Composable
private fun InstanceStartButton(
    interactable: Boolean,
    strings: Strings,
    onStartInstance: (editor: Boolean) -> Unit,
) {
    TooltipWrapper(tooltip = {
        TooltipText(text = strings.startInstance)
    }) {
        Button(
            onClick = {
                onStartInstance(false)
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = CustomColors.success,
                contentColor = Color.White
            ),
            enabled = interactable
        ) {
            Icon(TablerIcons.PlayerPlay, strings.runInstance)
        }
    }
}

@Composable
private fun InstanceContextMenuArea(
    onStartInstance: (editor: Boolean) -> Unit,
    onEditInstance: () -> Unit,
    onDeleteInstance: () -> Unit,
    onActivateInstance: () -> Unit,
    content: @Composable () -> Unit,
) {
    val strings = LocalStrings.current
    ContextMenuArea(items = {
        listOf(
            ContextMenuItem(strings.startInstance) {
                onStartInstance(false)
            },
            ContextMenuItem(strings.startEditorInstance) {
                onStartInstance(true)
            },
            ContextMenuItem(strings.activateInstance) {
                onActivateInstance()
            },
            ContextMenuItem(strings.editInstance, onEditInstance),
            ContextMenuItem(strings.deleteInstance, onDeleteInstance),
        )
    }, content = content)
}

fun imageFromResource(path: String) =
    SkiaImage.makeFromEncoded(ClassLoader.getSystemResourceAsStream(path)!!.readAllBytes())
        .toComposeImageBitmap()
