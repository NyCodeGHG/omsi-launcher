package dev.nycode.omsilauncher.ui.setup

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import dev.nycode.omsilauncher.config.Configuration
import dev.nycode.omsilauncher.ui.setup.screens.GameDirectoryScreen
import dev.nycode.omsilauncher.ui.setup.screens.StartSetupScreen
import dev.nycode.omsilauncher.ui.setup.screens.SteamProcessScreen
import java.nio.file.Path

data class SetupState(val launcherPath: Path?) {
    fun toConfiguration(): Configuration {
        return Configuration(
            launcherPath
                ?: error("Cannot convert to Configuration when launcherPath is null")
        )
    }
}

@Composable
fun Setup(closeSetup: () -> Unit, configuration: Configuration?) {
    var currentSetupScreen by remember { mutableStateOf(SetupScreen.GAME_DIRECTORY) }
    val config = remember { mutableStateOf(SetupState(configuration?.rootInstallation)) }
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
