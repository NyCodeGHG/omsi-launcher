package dev.nycode.omsilauncher.setup.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.nycode.omsilauncher.setup.SetupScreen
import dev.nycode.omsilauncher.util.awaitSteamDeath
import dev.nycode.omsilauncher.util.tryCloseSteam
import kotlinx.coroutines.launch

private val steamAwaitKey = Any()

@Composable
fun SteamProcessScreen(switchScreen: (SetupScreen) -> Unit) = Box(Modifier.fillMaxSize()) {
    val scope = rememberCoroutineScope()
    var closingSteam by remember { mutableStateOf(false) }
    var steamErrorDialog by remember { mutableStateOf(false) }
    DisposableEffect(steamAwaitKey) {
        scope.launch {
            awaitSteamDeath()
            switchScreen(SetupScreen.START_SETUP)
        }
        onDispose {}
    }
    Column(Modifier.align(Alignment.Center)) {
        Text(
            "Please close Steam™ while setting up the launcher.",
            Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp
        )
        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally).padding(10.dp))
        Button(
            {
                closingSteam = true
                scope.launch {
                    if (!tryCloseSteam()) {
                        steamErrorDialog = true
                        closingSteam = false
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = !closingSteam
        ) {
            Text("Close Steam")
        }
        if (steamErrorDialog) {
            ErrorDialog(
                "We were not able to close Steam gracefully. Please close it manually. The setup will continue automatically.",
                {
                    steamErrorDialog = false
                },
                "Cannot close Steam."
            )
        }
    }
}

@Composable
fun ErrorDialog(text: String, onCloseRequest: () -> Unit, title: String) =
    Dialog(onCloseRequest, title = title, resizable = false) {
        Box(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth()) {
                Text(text, Modifier.align(Alignment.CenterVertically))
            }
            Row(Modifier.fillMaxWidth().align(Alignment.BottomCenter)) {
                Button(
                    onCloseRequest,
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error)
                ) {
                    Text("Ok")
                }
            }
        }
    }
