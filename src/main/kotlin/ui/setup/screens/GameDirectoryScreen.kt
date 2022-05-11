package dev.nycode.omsilauncher.ui.setup.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nycode.omsilauncher.ui.components.ClickablePath
import dev.nycode.omsilauncher.ui.routing.RouterState
import dev.nycode.omsilauncher.ui.setup.SetupState
import dev.nycode.omsilauncher.ui.setup.StartSetupRoute
import dev.nycode.omsilauncher.ui.setup.SteamRoute
import dev.nycode.omsilauncher.util.chooseDirectory
import dev.nycode.omsilauncher.util.isSteamRunning
import kotlinx.coroutines.launch

@Composable
fun GameDirectoryScreen(routerState: RouterState, config: MutableState<SetupState>) {
    val scope = rememberCoroutineScope()
    var configuration by config
    var gameFileDirectory by remember { mutableStateOf(configuration.launcherPath) }
    Box(Modifier.fillMaxSize()) {
        Text("Setup", fontSize = 35.sp, modifier = Modifier.align(Alignment.TopCenter))
        Column(Modifier.align(Alignment.Center)) {
            Text(
                "Choose a directory where your instances and the base game files should be stored.",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Button({
                scope.launch {
                    gameFileDirectory = chooseDirectory(gameFileDirectory)
                }
            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Select Game File Directory")
            }
                Spacer(Modifier.padding(5.dp))
                if (gameFileDirectory != null) {
                    ClickablePath(gameFileDirectory!!)
                } else {
                    Text("No Game Directory selected.")
                }
            }
            Button(
                {
                    configuration = configuration.copy(launcherPath = gameFileDirectory)
                    if (isSteamRunning()) {
                        routerState.currentRoute = SteamRoute
                    } else {
                        routerState.currentRoute = StartSetupRoute
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp),
                enabled = gameFileDirectory != null
            ) {
                Text("Continue")
            }
        }
    }
    