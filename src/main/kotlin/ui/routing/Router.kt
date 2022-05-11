package dev.nycode.omsilauncher.ui.routing

import androidx.compose.runtime.*

typealias Route = @Composable () -> Unit

@Composable
inline fun Router(routerState: RouterState, builder: RouterBuilder.() -> Unit) {
    val routes: RouterBuilder = remember { RouterBuilder().apply(builder) }
    val route: Route? = routes.routes[routerState.currentRoute]
    // Compose compiler plugin does not like elvis operator here. Don't ask me why.
    @Suppress("FoldInitializerAndIfToElvis")
    if (route == null) {
        error("Invalid route ${routerState.currentRoute}")
    }
    route()
}

@Composable
fun rememberRouterState(defaultRoute: RouterKey) = remember { RouterState(defaultRoute) }
