package dev.nycode.omsilauncher.ui.setup.screens

import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.ui.routing.RouterState
import dev.nycode.omsilauncher.ui.setup.StartSetupRoute
import dev.nycode.omsilauncher.ui.components.SteamProcessScreen as BaseSteamProcessScreen

@Composable
fun SteamProcessScreen(routerState: RouterState) = BaseSteamProcessScreen(LocalStrings.current.closeSteamInfo) {
    routerState.currentRoute = StartSetupRoute
}
