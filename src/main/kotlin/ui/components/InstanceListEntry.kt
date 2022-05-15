package dev.nycode.omsilauncher.ui.components

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.LocalStrings
import compose.icons.TablerIcons
import compose.icons.tablericons.Click
import compose.icons.tablericons.Pencil
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.Tools
import compose.icons.tablericons.Trash
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.InstanceState
import dev.nycode.omsilauncher.omsi.OmsiProcessState
import dev.nycode.omsilauncher.omsi.UserAccessControlCancelledException
import dev.nycode.omsilauncher.ui.CustomColors
import dev.nycode.omsilauncher.ui.instance.ApplicationInstanceState
import dev.nycode.omsilauncher.ui.instance.context.InstanceActionContext
import dev.nycode.omsilauncher.ui.instance.context.InstanceContextMenuAction
import dev.nycode.omsilauncher.ui.instance.context.impl.ActivateInstanceContextMenuAction
import dev.nycode.omsilauncher.ui.instance.context.impl.DeleteInstanceContextMenuAction
import dev.nycode.omsilauncher.ui.instance.context.impl.EditInstanceContextMenuAction
import dev.nycode.omsilauncher.ui.instance.context.impl.start.EditorStartInstanceContextMenuAction
import dev.nycode.omsilauncher.ui.instance.context.impl.start.RegularStartInstanceContextMenuAction
import dev.nycode.omsilauncher.ui.instance.context.instanceContextMenus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.Desktop
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

    val context = remember(strings, omsiState, instance, instanceActive, scope) {
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

        val showInstanceFiles: () -> Unit = {
            Desktop.getDesktop().browse(instance.directory.toUri())
        }

        InstanceActionContext(
            strings = strings,
            omsiState = omsiState,
            instance = instance,
            instanceActive = instanceActive,
            onStartInstance = startInstance,
            onEditInstance = editInstance,
            onDeleteInstance = deleteInstance,
            onActivateInstance = activateInstance,
            onShowInstanceFiles = showInstanceFiles
        )
    }

    Card(modifier, elevation = 3.dp) {
        InstanceContextMenuArea(context = context) {
            Row(Modifier.fillMaxWidth().height(125.dp)) {
                Image(image, strings.eCitaro)
                Spacer(Modifier.padding(5.dp))
                Box(Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.align(Alignment.TopStart).padding(10.dp)) {
                        Text(instance.name, fontSize = 30.sp)
                    }
                    InstanceButtonRow(
                        modifier = Modifier.align(Alignment.BottomStart).padding(10.dp),
                        context = context
                    )
                    Box(
                        modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).fillMaxHeight()
                    ) {
                        InstanceActionIconButton(
                            modifier = Modifier.align(Alignment.TopEnd),
                            context = context,
                            EditInstanceContextMenuAction,
                            strings.editInstance
                        ) {
                            Icon(TablerIcons.Pencil, strings.edit)
                        }
                        InstanceActionIconButton(
                            modifier = Modifier.align(Alignment.BottomEnd),
                            context = context,
                            DeleteInstanceContextMenuAction,
                            strings.deleteInstance
                        ) {
                            Icon(
                                TablerIcons.Trash,
                                strings.delete,
                                tint = MaterialTheme.colors.error
                            )
                        }
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
private fun InstanceActionIconButton(
    modifier: Modifier = Modifier,
    context: InstanceActionContext,
    action: InstanceContextMenuAction,
    tooltip: String,
    icon: @Composable () -> Unit,
) {
    InstanceAction(
        modifier = modifier,
        tooltip = tooltip
    ) {
        IconButton(
            onClick = { action.action(context) },
            enabled = action.isAvailable(context)
        ) {
            icon()
        }
    }
}

@Composable
private fun InstanceActionButton(
    modifier: Modifier = Modifier,
    context: InstanceActionContext,
    action: InstanceContextMenuAction,
    tooltip: String,
    colors: ButtonColors,
    content: @Composable () -> Unit,
) {
    InstanceAction(
        tooltip = tooltip,
        modifier = modifier
    ) {
        Button(
            onClick = { action.action(context) },
            colors = colors,
            enabled = action.isAvailable(context)
        ) {
            content()
        }
    }
}

@Composable
private fun InstanceAction(
    modifier: Modifier = Modifier,
    tooltip: String,
    content: @Composable () -> Unit,
) {
    TooltipWrapper(
        modifier = modifier,
        tooltip = {
            TooltipText(tooltip)
        }
    ) {
        content()
    }
}

@Composable
private fun InstanceButtonRow(
    modifier: Modifier = Modifier,
    context: InstanceActionContext,
) = Row(modifier) {
    val instance = context.instance
    if (instance.state == InstanceState.CREATING || instance.state == InstanceState.DELETING) {
        LinearProgressIndicator(modifier = Modifier.padding(bottom = 5.dp))
    } else {
        InstanceStartButton(context)
        Spacer(Modifier.width(5.dp))
        InstanceStartEditorButton(context)
        Spacer(Modifier.width(5.dp))
        InstanceActivateButton(context)
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
    context: InstanceActionContext,
) {
    val strings = LocalStrings.current
    InstanceActionButton(
        context = context,
        action = ActivateInstanceContextMenuAction,
        tooltip = strings.activateInstance,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = Color.White
        )
    ) {
        Icon(TablerIcons.Click, strings.activateInstance)
    }
}

@Composable
private fun InstanceStartEditorButton(
    context: InstanceActionContext,
) {
    val strings = LocalStrings.current
    InstanceActionButton(
        context = context,
        action = EditorStartInstanceContextMenuAction,
        tooltip = strings.startEditorInstance,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White
        )
    ) {
        Icon(TablerIcons.Tools, strings.runEditor)
    }
}

@Composable
private fun InstanceStartButton(
    context: InstanceActionContext,
) {
    val strings = LocalStrings.current
    InstanceActionButton(
        context = context,
        action = RegularStartInstanceContextMenuAction,
        tooltip = strings.startInstance,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = CustomColors.success,
            contentColor = Color.White
        )
    ) {
        Icon(TablerIcons.PlayerPlay, strings.runInstance)
    }
}

@Composable
private fun InstanceContextMenuArea(
    context: InstanceActionContext,
    content: @Composable () -> Unit,
) {
    ContextMenuArea(items = {
        instanceContextMenus
            .asSequence()
            .filter { it.isAvailable(context) }
            .map { it.buildMenuItem(context) }
            .toList()
    }, content = content)
}

fun imageFromResource(path: String) =
    SkiaImage.makeFromEncoded(ClassLoader.getSystemResourceAsStream(path)!!.readAllBytes())
        .toComposeImageBitmap()
