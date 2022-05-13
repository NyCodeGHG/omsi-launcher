package dev.nycode.omsilauncher.ui.components

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
                    IconButton(
                        {},
                        modifier = Modifier.align(Alignment.TopEnd),
                        enabled = interactable
                    ) {
                        Icon(TablerIcons.Pencil, strings.edit)
                    }
                    IconButton(
                        {
                            deleteDialog = true
                        },
                        modifier = Modifier.align(Alignment.BottomEnd),
                        enabled = interactable
                    ) {
                        Icon(TablerIcons.Trash, strings.delete, tint = MaterialTheme.colors.error)
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
        suspend fun Instance.withUACAction(action: suspend Instance.() -> Unit) = try {
            action()
        } catch (exception: UserAccessControlCancelledException) {
            showUACDialog()
        }
        Button(
            {
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
        Spacer(Modifier.width(5.dp))
        Button(
            {
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
        Spacer(Modifier.width(5.dp))
        Button(
            {
                scope.launch(Dispatchers.IO) {
                    instance.withUACAction {
                        activate()
                    }
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
        Spacer(Modifier.width(5.dp))
        Icon(
            instance.patchVersion.icon,
            strings.instancePatchVersion,
            modifier = Modifier.align(Alignment.Bottom).size(50.dp)
        )
    }
}

fun imageFromResource(path: String) =
    SkiaImage.makeFromEncoded(ClassLoader.getSystemResourceAsStream(path)!!.readAllBytes())
        .toComposeImageBitmap()
