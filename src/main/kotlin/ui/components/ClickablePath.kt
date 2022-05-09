package dev.nycode.omsilauncher.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import java.nio.file.Path
import kotlin.io.path.absolutePathString

@Composable
fun ClickablePath(path: Path, fontWeight: FontWeight? = null, modifier: Modifier = Modifier) {
    Text(
        path.absolutePathString(), fontWeight = fontWeight,
        modifier = modifier.clickable {
            Runtime.getRuntime().exec(arrayOf("explorer", path.absolutePathString()))
        }
    )
}
