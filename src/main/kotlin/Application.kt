package dev.nycode.omsilauncher

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import compose.icons.TablerIcons
import compose.icons.tablericons.Stack

@Composable
fun Application() = Row(Modifier.fillMaxSize()) {
    NavigationRail {
        NavigationRailItem(true,
            onClick = {},
            icon = { Icon(TablerIcons.Stack, "Instances") },
            label = { Text("Instances") })
    }
}
