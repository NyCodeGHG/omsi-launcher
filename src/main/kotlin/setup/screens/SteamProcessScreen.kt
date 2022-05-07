package dev.nycode.omsilauncher.setup.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nycode.omsilauncher.setup.SetupScreen
import dev.nycode.omsilauncher.util.awaitSteamDeath
import kotlinx.coroutines.launch

private val steamAwaitKey = Any()

@Composable
fun SteamProcessScreen(switchScreen: (SetupScreen) -> Unit) = Box(Modifier.fillMaxSize()) {
    val scope = rememberCoroutineScope()
    DisposableEffect(steamAwaitKey) {
        scope.launch {
            awaitSteamDeath()
            switchScreen(SetupScreen.START_SETUP)
        }
        onDispose {}
    }
    Column(Modifier.align(Alignment.Center)) {
        Text("Please close Steam™ while setting up the launcher.",
            Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp)
        Spacer(Modifier.padding(5.dp))
        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
    }
}
