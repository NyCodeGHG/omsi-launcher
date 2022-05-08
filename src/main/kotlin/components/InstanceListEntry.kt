package dev.nycode.omsilauncher.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import dev.nycode.omsilauncher.ui.CustomColors
import org.jetbrains.skia.Image.Companion as SkiaImage

@Composable
fun InstanceListEntry(instance: Instance) {
    val image = remember { imageFromResource("ecitaro.jpg") }
    Card(Modifier.padding(5.dp), elevation = 3.dp) {
        Row(Modifier.fillMaxWidth().height(125.dp)) {
            Image(image, "ecitaro")
            Spacer(Modifier.padding(5.dp))
            Box(Modifier.fillMaxSize()) {
                Row(modifier = Modifier.align(Alignment.TopStart).padding(10.dp)) {
                    Text(instance.name, fontSize = 30.sp)
                }
                Row(modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)) {
                    Button({},
                        colors = ButtonDefaults.buttonColors(backgroundColor = CustomColors.success,
                            contentColor = Color.White)) {
                        Icon(TablerIcons.PlayerPlay, "Run Instance")
                        Spacer(Modifier.padding(5.dp))
                        Text("Launch")
                    }
                    Spacer(Modifier.padding(5.dp))
                    Button({},
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary,
                            contentColor = Color.White)) {
                        Icon(TablerIcons.Tools, "Run Editor")
                        Spacer(Modifier.padding(5.dp))
                        Text("Launch Editor")
                    }
                }
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).fillMaxHeight()) {
                    IconButton({}, modifier = Modifier.align(Alignment.TopEnd)) {
                        Icon(TablerIcons.Pencil, "Edit")
                    }
                    IconButton({}, modifier = Modifier.align(Alignment.BottomEnd)) {
                        Icon(TablerIcons.Trash, "Delete", tint = MaterialTheme.colors.error)
                    }
                }
            }
        }
    }
}

fun imageFromResource(path: String) =
    SkiaImage.makeFromEncoded(ClassLoader.getSystemResourceAsStream(path)!!.readAllBytes())
        .toComposeImageBitmap()
