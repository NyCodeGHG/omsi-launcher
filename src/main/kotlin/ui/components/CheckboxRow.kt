package dev.nycode.omsilauncher.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun CheckboxRow(
    title: String,
    tooltip: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    val pressIndicator = Modifier.pointerInput(value) {
        detectTapGestures(onPress = {
            onValueChange(!value)
        })
    }
    TooltipWrapper(modifier = pressIndicator, tooltip = { TooltipText(tooltip) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(value, onCheckedChange = onValueChange)
            Text(title)
        }
    }
}
