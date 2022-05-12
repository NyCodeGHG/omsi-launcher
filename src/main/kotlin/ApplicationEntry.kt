package dev.nycode.omsilauncher

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
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
        ProvideStrings(lyricist) {
            MaterialTheme {
                Window(onCloseRequest = ::exitApplication, title = getApplicationTitle()) {
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
