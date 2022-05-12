package dev.nycode.omsilauncher.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog

@Composable
fun ErrorDialog(
    visible: Boolean,
    onCloseRequest: () -> Unit,
    text: String?,
    title: String?,
) {
    Dialog(
        onCloseRequest = onCloseRequest,
        visible = visible,
        title = title ?: "Error"
    ) {
        Box(Modifier.fillMaxSize()) {
            if (text != null) {
                Text(text, modifier = Modifier.align(Alignment.Center))
            }
            Button(onCloseRequest, modifier = Modifier.align(Alignment.BottomCenter)) {
                Text("Close")
            }
        }
    }
}
