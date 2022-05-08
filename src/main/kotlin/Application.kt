package dev.nycode.omsilauncher

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings
import compose.icons.TablerIcons
import compose.icons.tablericons.Stack
import dev.nycode.omsilauncher.components.InstanceListEntry
import dev.nycode.omsilauncher.config.resolveAppDataPath
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.instances
import dev.nycode.omsilauncher.instance.saveInstances
import dev.nycode.omsilauncher.omsi.OmsiProcessUpdate
import dev.nycode.omsilauncher.omsi.receiveOmsiProcessUpdates
import dev.nycode.omsilauncher.util.createInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun Application() {
    val lyricist = rememberStrings()
    val instances by remember { instances }
    val scope = rememberCoroutineScope()
    val omsiState by receiveOmsiProcessUpdates().collectAsState(
        OmsiProcessUpdate.NOT_RUNNING,
        Dispatchers.IO
    )

    ProvideStrings(lyricist, LocalStrings) {
        val strings = LocalStrings.current
        Row(Modifier.fillMaxSize()) {
            NavigationRail {
                NavigationRailItem(
                    true,
                    onClick = {},
                    icon = { Icon(TablerIcons.Stack, strings.instances) },
                    label = { Text(strings.instances) }
                )
            }
            Box(modifier = Modifier.fillMaxSize()) {
                val stateVertical = rememberScrollState(0)
                Box(
                    Modifier.fillMaxSize()
                        .verticalScroll(stateVertical)
                        .padding(end = 12.dp, bottom = 12.dp)
                ) {
                    Column(Modifier.fillMaxSize()) {
                        instances.forEach {
                            InstanceListEntry(it, scope, omsiState)
                        }
                        // TODO: Make this look similar to a card
                        Button(
                            {
                                scope.launch(Dispatchers.IO) {
                                    val randomName = "Omsi" + Random.nextInt()
                                    val instance = Instance(
                                        randomName,
                                        resolveAppDataPath(randomName),
                                        Instance.PatchVersion.BI_ARTICULATED_BUS_VERSION
                                    )
                                    createInstance(instance)

                                    saveInstances(instances + instance) // TODO: improve update logic
                                }
                            },
                            enabled = omsiState != OmsiProcessUpdate.RUNNING
                        ) {
                            Text(strings.addNewInstance)
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(stateVertical)
                )
            }
        }
    }
}
