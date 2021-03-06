package dev.nycode.omsilauncher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import java.nio.file.Path
import kotlin.io.path.exists

private val defaultImage by lazy {
    imageFromResource("ecitaro.jpg")
}

@Composable
fun SafeInstanceIcon(modifier: Modifier = Modifier, path: Path?, contentDescription: String) {
    if (path != null && path.exists()) {
        val image = remember(path) { imageFromPath(path) }
        InstanceIcon(modifier, image, contentDescription)
    } else {
        InstanceIcon(modifier, defaultImage, contentDescription)
    }
}

@Composable
fun InstanceIcon(modifier: Modifier = Modifier, image: ImageBitmap, contentDescription: String) {
    Image(image, contentDescription, modifier.size(128.dp))
}
