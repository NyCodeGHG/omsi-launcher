package dev.nycode.omsilauncher.ui.setup.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.ui.components.ClickablePath
import dev.nycode.omsilauncher.ui.routing.RouterState
import dev.nycode.omsilauncher.ui.setup.ExplainSteamRoute
import dev.nycode.omsilauncher.ui.setup.SetupState
import dev.nycode.omsilauncher.util.chooseDirectory
import kotlinx.coroutines.launch

@Composable
fun GameDirectoryScreen(routerState: RouterState, config: MutableState<SetupState>) {
    val scope = rememberCoroutineScope()
    var configuration by config
    var gameFileDirectory by remember { mutableStateOf(configuration.launcherPath) }
    val strings = LocalStrings.current
    Box(Modifier.fillMaxSize()) {
        Text(strings.setup, fontSize = 35.sp, modifier = Modifier.align(Alignment.TopCenter))
        Column(Modifier.align(Alignment.Center)) {
            Text(
                strings.setupChooseDirectory,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Button({
                scope.launch {
                    gameFileDirectory = chooseDirectory(gameFileDirectory)
                }
            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(strings.chooseGameFileDirectory)
            }
                Spacer(Modifier.padding(5.dp))
                if (gameFileDirectory != null) {
                    ClickablePath(gameFileDirectory!!)
                } else {
                    Text(strings.noGameFileDirectory)
                }
            }
            Button(
                {
                    configuration = configuration.copy(launcherPath = gameFileDirectory)
                    routerState.currentRoute = ExplainSteamRoute
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp),
                enabled = gameFileDirectory != null
            ) {
                Text(strings.`continue`)
            }
        }
    }
    