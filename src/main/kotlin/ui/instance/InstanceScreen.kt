package dev.nycode.omsilauncher.ui.instance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Plus
import dev.nycode.omsilauncher.instance.getCurrentInstancePath
import dev.nycode.omsilauncher.instance.receiveCurrentInstancePath
import dev.nycode.omsilauncher.omsi.OmsiProcessState
import dev.nycode.omsilauncher.omsi.receiveOmsiProcessUpdates
import dev.nycode.omsilauncher.ui.components.ErrorDialog
import dev.nycode.omsilauncher.ui.components.InstanceListEntry
import dev.nycode.omsilauncher.ui.instance.creation.InstanceCreationDialog
import dev.nycode.omsilauncher.util.isOmsiRunning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun InstanceScreen() {
    val scope = rememberCoroutineScope()
    val instanceState = rememberApplicationInstanceState()
    val instances = instanceState.instances
    val omsiState by receiveOmsiProcessUpdates().collectAsState(
        if (isOmsiRunning()) OmsiProcessState.RUNNING else OmsiProcessState.NOT_RUNNING,
        Dispatchers.IO
    )
    var showCreationDialog by remember { mutableStateOf(false) }
    val currentInstancePath by receiveCurrentInstancePath().collectAsState(getCurrentInstancePath())
    var showUACError by remember { mutableStateOf(false) }
    if (showUACError) {
        ErrorDialog(
            true,
            {
                showUACError = false
            },
            "you have to click yes. idiot.",
            "don't buy apple products."
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(475.dp),
            contentPadding = PaddingValues(12.dp),
            content = {
                items(instances) { instance ->
                    InstanceListEntry(
                        modifier = Modifier.padding(6.dp),
                        instanceState = instanceState,
                        instance = instance,
                        scope = scope,
                        omsiState = omsiState,
                        instance.directory == currentInstancePath
                    ) {
                        showUACError = true
                    }
                }
            }
        )
        FloatingActionButton(
            {
                showCreationDialog = true
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 16.dp, end = 16.dp)
        ) {
            Icon(TablerIcons.Plus, null)
        }
    }
    if (showCreationDialog) {
        InstanceCreationDialog(
            {
                showCreationDialog = false
            },
            { name, path, patchVersion, use4gbPatch ->
                scope.launch(Dispatchers.IO) {
                    instanceState.createNewInstance(
                        UUID.randomUUID(),
                        name,
                        path,
                        patchVersion,
                        uses4GBPatch = use4gbPatch
                    )
                }
            }
        )
    }
}
