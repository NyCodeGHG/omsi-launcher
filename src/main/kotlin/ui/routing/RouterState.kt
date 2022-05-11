package dev.nycode.omsilauncher.ui.routing

import androidx.compose.runtime.*

class RouterState(defaultRoute: RouterKey) {
    var currentRoute: RouterKey by mutableStateOf(defaultRoute)
}
