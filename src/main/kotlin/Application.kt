package dev.nycode.omsilauncher

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import dev.nycode.omsilauncher.instance.createNewInstance
import dev.nycode.omsilauncher.instance.instanceViewModel
import dev.nycode.omsilauncher.omsi.OmsiProcessUpdate
import dev.nycode.omsilauncher.omsi.receiveOmsiProcessUpdates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random

@Composable
fun Application() {
    val lyricist = rememberStrings()
    val instances by remember { instanceViewModel }
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
                                        UUID.randomUUID(),
                                        randomName,
                                        resolveAppDataPath(randomName),
                                        Instance.PatchVersion.BI_ARTICULATED_BUS_VERSION
                                    )
                                    createNewInstance(instance)
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
