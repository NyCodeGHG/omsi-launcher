package dev.nycode.omsilauncher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val (deleteDialog, setDeleteDialog) = remember { mutableStateOf(false) }
    val strings = LocalStrings.current
    val interactable =
        instance.state == InstanceState.READY && omsiState == OmsiProcessState.NOT_RUNNING
    Card(modifier, elevation = 3.dp) {
        Row(Modifier.fillMaxWidth().height(125.dp)) {
            Image(image, strings.eCitaro)
            Spacer(Modifier.padding(5.dp))
            Box(Modifier.fillMaxSize()) {
                Row(modifier = Modifier.align(Alignment.TopStart).padding(10.dp)) {
                    Text(instance.name, fontSize = 30.sp)
                }
                InstanceButtonRow(
                    modifier = Modifier.align(Alignment.BottomStart).padding(10.dp),
                    instance,
                    interactable,
                    scope,
                    instanceActive,
                    showUACDialog
                )
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).fillMaxHeight()) {
                    InstanceEditButton(Modifier.align(Alignment.TopEnd), interactable)
                    InstanceDeleteButton(
                        Modifier.align(Alignment.BottomEnd),
                        setDeleteDialog,
                        interactable
                    )
                }
            }
        }
    }
    if (deleteDialog) {
        ConfirmationDialog(
            strings.confirmDeletion(instance.name),
            { delete ->
                setDeleteDialog(false)
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
    setDeleteDialog: (Boolean) -> Unit,
    interactable: Boolean,
) {
    val strings = LocalStrings.current
    TooltipWrapper(modifier = modifier, tooltip = {
        TooltipText(strings.deleteInstance)
    }) {
        IconButton(
            onClick = {
                setDeleteDialog(true)
            },
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
) {
    val strings = LocalStrings.current
    TooltipWrapper(
        tooltip = {
            TooltipText(strings.editInstance)
        },
        modifier = modifier
    ) {
        IconButton(
            {},
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
    scope: CoroutineScope,
    instanceActive: Boolean,
    showUACDialog: () -> Unit,
) = Row(modifier) {
    val strings = LocalStrings.current
    if (instance.state == InstanceState.CREATING || instance.state == InstanceState.DELETING) {
        LinearProgressIndicator(modifier = Modifier.padding(bottom = 5.dp))
    } else {
        val withUACAction: suspend Instance.(suspend Instance.() -> Unit) -> Unit = { action ->
            try {
                action()
            } catch (exception: UserAccessControlCancelledException) {
                showUACDialog()
            }
        }
        InstanceStartButton(scope, instance, interactable, strings, withUACAction)
        Spacer(Modifier.width(5.dp))
        InstanceStartEditorButton(scope, instance, withUACAction, interactable, strings)
        Spacer(Modifier.width(5.dp))
        InstanceActivateButton(
            scope,
            instance,
            withUACAction,
            instanceActive,
            interactable,
            strings
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

private typealias UACActionWrapper = suspend Instance.(suspend Instance.() -> Unit) -> Unit

@Composable
private fun InstanceActivateButton(
    scope: CoroutineScope,
    instance: Instance,
    withUACAction: UACActionWrapper,
    instanceActive: Boolean,
    interactable: Boolean,
    strings: Strings,
) {
    TooltipWrapper(tooltip = {
        TooltipText(strings.activateInstance)
    }) {
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    instance.withUACAction(Instance::activate)
                }
            },
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
    scope: CoroutineScope,
    instance: Instance,
    withUACAction: UACActionWrapper,
    interactable: Boolean,
    strings: Strings,
) {
    TooltipWrapper(tooltip = {
        TooltipText(text = strings.startEditorInstance)
    }) {
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    instance.withUACAction {
                        start(true)
                    }
                }
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
    scope: CoroutineScope,
    instance: Instance,
    interactable: Boolean,
    strings: Strings,
    withUACAction: UACActionWrapper,
) {
    TooltipWrapper(tooltip = {
        TooltipText(text = strings.startInstance)
    }) {
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    instance.withUACAction(Instance::start)
                }
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

fun imageFromResource(path: String) =
    SkiaImage.makeFromEncoded(ClassLoader.getSystemResourceAsStream(path)!!.readAllBytes())
        .toComposeImageBitmap()
