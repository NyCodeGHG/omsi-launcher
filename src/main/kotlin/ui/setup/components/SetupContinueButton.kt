package dev.nycode.omsilauncher.ui.setup.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.localization.Strings
import dev.nycode.omsilauncher.ui.components.TooltipText
import dev.nycode.omsilauncher.ui.components.TooltipWrapper

@Composable
fun SetupContinueButton(
    onClick: () -> Unit,
    buttonModifier: Modifier = Modifier,
    areaModifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.(strings: Strings) -> Unit = { strings ->
        Text(strings.`continue`)
    },
) {
    val strings = LocalStrings.current
    TooltipWrapper(modifier = areaModifier, tooltip = {
        TooltipText(text = strings.continueSetup)
    }) {
        Button(onClick, buttonModifier, enabled) {
            content(strings)
        }
    }
}
