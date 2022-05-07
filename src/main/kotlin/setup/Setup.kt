package dev.nycode.omsilauncher.setup

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dev.nycode.omsilauncher.config.Configuration
import dev.nycode.omsilauncher.setup.screens.GameDirectoryScreen
import dev.nycode.omsilauncher.setup.screens.StartSetupScreen
import dev.nycode.omsilauncher.setup.screens.SteamProcessScreen

@Composable
fun ApplicationScope.Setup() = Window(onCloseRequest = ::exitApplication) {
    var currentSetupScreen by remember { mutableStateOf(SetupScreen.GAME_DIRECTORY) }
    val config = remember { mutableStateOf(Configuration(null)) }
    val switchScreen: (SetupScreen) -> Unit = {
        currentSetupScreen = it
    }
    Box {
        when (currentSetupScreen) {
            SetupScreen.GAME_DIRECTORY -> GameDirectoryScreen(switchScreen, config)
            SetupScreen.STEAM -> SteamProcessScreen(switchScreen)
            SetupScreen.START_SETUP -> StartSetupScreen(config)
        }
    }
}
