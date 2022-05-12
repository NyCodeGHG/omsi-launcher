package dev.nycode.omsilauncher.ui.setup

import androidx.compose.runtime.*
import dev.nycode.omsilauncher.config.Configuration
import dev.nycode.omsilauncher.ui.routing.Router
import dev.nycode.omsilauncher.ui.routing.RouterKey
import dev.nycode.omsilauncher.ui.routing.rememberRouterState
import dev.nycode.omsilauncher.ui.setup.screens.GameDirectoryScreen
import dev.nycode.omsilauncher.ui.setup.screens.StartSetupScreen
import dev.nycode.omsilauncher.ui.setup.screens.SteamExplainerSetupScreen
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

val GameDirectoryRoute = RouterKey(name = "GameDirectory")
val ExplainSteamRoute = RouterKey(name = "ExplainSteamRoute")
val SteamRoute = RouterKey(name = "Steam")
val StartSetupRoute = RouterKey(name = "StartSetup")

@Composable
fun Setup(closeSetup: () -> Unit, configuration: Configuration?) {
    val config = remember { mutableStateOf(SetupState(configuration?.rootInstallation)) }
    val routerState = rememberRouterState(GameDirectoryRoute)
    Router(routerState) {
        route(GameDirectoryRoute) {
            GameDirectoryScreen(routerState, config)
        }
        route(ExplainSteamRoute) {
            SteamExplainerSetupScreen(routerState)
        }
        route(SteamRoute) {
            SteamProcessScreen(routerState)
        }
        route(StartSetupRoute) {
            StartSetupScreen(config, closeSetup)
        }
    }
}
