package dev.nycode.omsilauncher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TooltipWrapper(
    modifier: Modifier = Modifier,
    tooltipColors: Colors = MaterialTheme.colors,
    tooltipShape: Shape = MaterialTheme.shapes.small,
    delay: Duration = 500.milliseconds,
    tooltip: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.shadow(4.dp),
                color = tooltipColors.background,
                shape = tooltipShape
            ) {
                tooltip()
            }
        },
        delayMillis = delay.inWholeMilliseconds.toInt(),
        modifier = modifier
    ) {
        content()
    }
}
