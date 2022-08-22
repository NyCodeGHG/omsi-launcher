package dev.nycode.omsilauncher.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T : Any> DropdownInputField(
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    values: Collection<T>,
    valueToMenuItem: @Composable (T) -> Unit = {
        Text(it.toString())
    },
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier
            .clickable {
                expanded = !expanded
            }
            .border(
                1.dp,
                MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                MaterialTheme.shapes.small
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(20.dp)
        ) {
            DropdownMenu(expanded, {
                expanded = false
            }) {
                for (menuValue in values) {
                    DropdownMenuItem({
                        onValueChange(menuValue)
                        expanded = false
                    }, enabled = enabled || menuValue == value) {
                        valueToMenuItem(menuValue)
                    }
                }
            }
            valueToMenuItem(value)
        }
        Icon(
            Icons.Filled.ArrowDropDown,
            null,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterEnd)
        )
    }
}
