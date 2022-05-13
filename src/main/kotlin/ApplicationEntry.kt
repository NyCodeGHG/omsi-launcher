package dev.nycode.omsilauncher

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings
import dev.nycode.omsilauncher.config.Configuration
import dev.nycode.omsilauncher.ui.Application
import dev.nycode.omsilauncher.ui.routing.Router
import dev.nycode.omsilauncher.ui.routing.RouterKey
import dev.nycode.omsilauncher.ui.routing.rememberRouterState
import dev.nycode.omsilauncher.ui.setup.Setup
import dev.nycode.omsilauncher.util.getApplicationTitle

private val SetupRoute = RouterKey(name = "Setup")
private val MainApplicationRoute = RouterKey(name = "MainApplication")

private const val MIN_WIDTH = 1200
private const val MIN_HEIGHT = 800

fun runLauncher(
    runSetup: Boolean,
    configuration: Configuration?,
) =
    application {
        val routerState = rememberRouterState(if (runSetup) SetupRoute else MainApplicationRoute)
        val lyricist = rememberStrings()
        val closeSetup = {
            routerState.currentRoute = MainApplicationRoute
        }
        val windowState = rememberWindowState(size = DpSize(MIN_WIDTH.dp, MIN_HEIGHT.dp))
        ProvideStrings(lyricist) {
            MaterialTheme {
                Window(
                    onCloseRequest = ::exitApplication,
                    title = getApplicationTitle(),
                    state = windowState
                ) {
                    Router(routerState) {
                        route(SetupRoute) {
                            Setup(closeSetup, configuration)
                        }
                        route(MainApplicationRoute) {
                            Application()
                        }
                    }
                }
            }
        }
    }
