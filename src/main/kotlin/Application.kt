package dev.nycode.omsilauncher

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Stack
import dev.nycode.omsilauncher.components.InstanceListEntry
import dev.nycode.omsilauncher.instance.Instance
import kotlin.io.path.Path

@Composable
fun Application() = Row(Modifier.fillMaxSize()) {
    NavigationRail {
        NavigationRailItem(true,
            onClick = {},
            icon = { Icon(TablerIcons.Stack, "Instances") },
            label = { Text("Instances") })
    }
    Box(modifier = Modifier.fillMaxSize()) {
        val stateVertical = rememberScrollState(0)
        Box(Modifier.fillMaxSize()
            .verticalScroll(stateVertical)
            .padding(end = 12.dp, bottom = 12.dp)) {
            Column(Modifier.fillMaxSize()) {
                InstanceListEntry(Instance("Hamburg", Path(""), Instance.PatchVersion.TRAM_VERSION))
                InstanceListEntry(Instance("Vanilla", Path(""), Instance.PatchVersion.TRAM_VERSION))
                InstanceListEntry(Instance("Cologne", Path(""), Instance.PatchVersion.TRAM_VERSION))
                InstanceListEntry(Instance("Munich", Path(""), Instance.PatchVersion.TRAM_VERSION))
                InstanceListEntry(Instance("Wuppertal", Path(""), Instance.PatchVersion.TRAM_VERSION))
                InstanceListEntry(Instance("Lucerne", Path(""), Instance.PatchVersion.TRAM_VERSION))
                InstanceListEntry(Instance("Bremen", Path(""), Instance.PatchVersion.TRAM_VERSION))
                InstanceListEntry(Instance("Aachen", Path(""), Instance.PatchVersion.TRAM_VERSION))
            }
        }
        VerticalScrollbar(modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(stateVertical))
    }
}
