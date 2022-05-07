package dev.nycode.omsilauncher.setup

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import dev.nycode.omsilauncher.config.Configuration
import dev.nycode.omsilauncher.setup.screens.GameDirectoryScreen
import dev.nycode.omsilauncher.setup.screens.StartSetupScreen
import dev.nycode.omsilauncher.setup.screens.SteamProcessScreen

@Composable
fun Setup(closeSetup: () -> Unit) {
    var currentSetupScreen by remember { mutableStateOf(SetupScreen.GAME_DIRECTORY) }
    val config = remember { mutableStateOf(Configuration(null)) }
    val switchScreen: (SetupScreen) -> Unit = {
        currentSetupScreen = it
    }
    Box {
        when (currentSetupScreen) {
            SetupScreen.GAME_DIRECTORY -> GameDirectoryScreen(switchScreen, config)
            SetupScreen.STEAM -> SteamProcessScreen(switchScreen)
            SetupScreen.START_SETUP -> StartSetupScreen(config, closeSetup)
        }
    }
}
