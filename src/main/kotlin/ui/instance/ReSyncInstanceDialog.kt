package dev.nycode.omsilauncher.ui.instance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.omsi.relinkBaseGame
import dev.nycode.omsilauncher.steam.mergeManifestWith
import dev.nycode.omsilauncher.ui.routing.Router
import dev.nycode.omsilauncher.ui.routing.RouterKey
import dev.nycode.omsilauncher.ui.routing.RouterState
import dev.nycode.omsilauncher.ui.routing.rememberRouterState
import dev.nycode.omsilauncher.util.getOmsiSteamManifest
import dev.schlaubi.stdx.logging.logger
import dev.nycode.omsilauncher.vdf.VDF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.StandardCopyOption
import kotlin.io.path.copyTo
import kotlin.io.path.readText
import kotlin.io.path.writeText

private val logger = logger()
private val PreparingRoute = RouterKey(name = "Preparing")
private val WaitingForChangesRoute = RouterKey(name = "WaitingForChangesScreen")
private val ActionRoute = RouterKey(name = "Action")

@Composable
fun ReSyncInstanceDialog(instances: List<Instance>, instance: Instance, onCloseRequest: () -> Unit) {
    val routerState = rememberRouterState(PreparingRoute)
    Dialog(onCloseRequest, title = "Resynchronising Wizard") {
        Router(routerState) {
            route(PreparingRoute) {
                PreparingSynchronisationScreen(instance, routerState)
            }
            route(WaitingForChangesRoute) {
                WaitingForChangesScreen(routerState)
            }
            route(ActionRoute) {
                ActionScreen(instances, instance, onCloseRequest)
            }
        }
    }
}

@Composable
private fun PreparingSynchronisationScreen(instance: Instance, routerState: RouterState) =
    Box(Modifier.fillMaxSize()) {
        val scope = rememberCoroutineScope()
        val strings = LocalStrings.current

        DisposableEffect(Unit) {
            val backup = instance.manifestBackup
            scope.launch {
                val currentManifest = getOmsiSteamManifest()
                currentManifest.copyTo(backup, StandardCopyOption.REPLACE_EXISTING)
                routerState.currentRoute = WaitingForChangesRoute
            }
            onDispose {}
        }
        Column(Modifier.align(Alignment.Center)) {
            Text(
                strings.preparingReSynchronisation,
                Modifier.align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally).padding(10.dp))
        }
    }

@Composable
private fun WaitingForChangesScreen(routerState: RouterState) =
    Box(Modifier.fillMaxSize()) {
        val strings = LocalStrings.current
        Column(Modifier.align(Alignment.Center)) {
            Text(
                strings.waitingForChanges,
                Modifier.align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally).padding(10.dp))
            Spacer(Modifier.width(5.dp))
            Button(
                { routerState.currentRoute = ActionRoute },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(strings.iAmDone)
            }
        }
    }

@Composable
private fun ActionScreen(instances: List<Instance>, instance: Instance, onCloseRequest: () -> Unit) {
    val scope = rememberCoroutineScope()
    var step by remember { mutableStateOf("Initializing") }
    val strings = LocalStrings.current

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center)) {
            Text(step)
            LinearProgressIndicator(Modifier.align(Alignment.CenterHorizontally).padding(10.dp))
        }
    }

    DisposableEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            val vdf = VDF()
            val backupMainManifest = vdf.decodeToVDFObject(instance.manifestBackup.readText())
            val newMainManifest = vdf.decodeToVDFObject(getOmsiSteamManifest().readText())

            instances.forEach {
                step = strings.mergingSteamManifest(it.name)
                val instanceManifest = vdf.decodeToVDFObject(it.manifest.readText())
                val merge = instanceManifest.mergeManifestWith(newMainManifest, backupMainManifest)
                logger.debug { "Merging manifest of ${it.name}, diff: ${merge == instanceManifest}" }
                val encodedManifest = vdf.encodeFromVDFElement(merge)
                it.manifest.writeText(encodedManifest)
            }

            step = strings.reLinkingInstances
            logger.info { "Re-linking all OMSI child instances" }
            relinkBaseGame(instances.filterNot(Instance::isBaseInstance))
            onCloseRequest()
        }

        onDispose {}
    }
}
