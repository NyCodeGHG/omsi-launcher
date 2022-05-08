package dev.nycode.omsilauncher.components

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
import compose.icons.TablerIcons
import compose.icons.tablericons.Pencil
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.Tools
import compose.icons.tablericons.Trash
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.deleteInstance
import dev.nycode.omsilauncher.omsi.OmsiProcessUpdate
import dev.nycode.omsilauncher.ui.CustomColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.skia.Image.Companion as SkiaImage

@Composable
fun InstanceListEntry(instance: Instance, scope: CoroutineScope, omsiState: OmsiProcessUpdate) {
    val image = remember { imageFromResource("ecitaro.jpg") }
    var deleteDialog by remember { mutableStateOf(false) }
    Card(Modifier.padding(5.dp), elevation = 3.dp) {
        Row(Modifier.fillMaxWidth().height(125.dp)) {
            Image(image, "ecitaro")
            Spacer(Modifier.padding(5.dp))
            Box(Modifier.fillMaxSize()) {
                Row(modifier = Modifier.align(Alignment.TopStart).padding(10.dp)) {
                    Text(instance.name, fontSize = 30.sp)
                }
                Row(modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)) {
                    Button(
                        {
                            scope.launch(Dispatchers.IO) {
                                instance.start()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = CustomColors.success,
                            contentColor = Color.White
                        ),
                        enabled = omsiState == OmsiProcessUpdate.NOT_RUNNING
                    ) {
                        Icon(TablerIcons.PlayerPlay, "Run Instance")
                        Spacer(Modifier.padding(5.dp))
                        Text("Launch")
                    }
                    Spacer(Modifier.padding(5.dp))
                    Button(
                        {
                            scope.launch(Dispatchers.IO) {
                                instance.start(true)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = Color.White
                        ),
                        enabled = omsiState == OmsiProcessUpdate.NOT_RUNNING
                    ) {
                        Icon(TablerIcons.Tools, "Run Editor")
                        Spacer(Modifier.padding(5.dp))
                        Text("Launch Editor")
                    }
                    Spacer(Modifier.padding(5.dp))
                    Icon(instance.patchVersion.icon,
                        "instance patch version",
                        modifier = Modifier.align(Alignment.Bottom).size(50.dp))
                }
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).fillMaxHeight()) {
                    IconButton({},
                        modifier = Modifier.align(Alignment.TopEnd),
                        enabled = omsiState == OmsiProcessUpdate.NOT_RUNNING) {
                        Icon(TablerIcons.Pencil, "Edit")
                    }
                    IconButton({
                        deleteDialog = true
                    },
                        modifier = Modifier.align(Alignment.BottomEnd),
                        enabled = omsiState == OmsiProcessUpdate.NOT_RUNNING) {
                        Icon(TablerIcons.Trash, "Delete", tint = MaterialTheme.colors.error)
                    }
                }
            }
        }
    }
    if (deleteDialog) {
        ConfirmationDialog("Are you sure you want to delete ${instance.name}?", { delete ->
            deleteDialog = false
            if (delete) {
                scope.launch(Dispatchers.IO) {
                    deleteInstance(instance)
                }
            }
        })
    }
}

fun imageFromResource(path: String) =
    SkiaImage.makeFromEncoded(ClassLoader.getSystemResourceAsStream(path)!!.readAllBytes())
        .toComposeImageBitmap()
