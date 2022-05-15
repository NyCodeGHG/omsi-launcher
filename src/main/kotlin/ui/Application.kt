package dev.nycode.omsilauncher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.LocalStrings
import compose.icons.TablerIcons
import compose.icons.tablericons.Settings
import compose.icons.tablericons.Stack
import dev.nycode.omsilauncher.ui.instance.InstanceScreen
import dev.nycode.omsilauncher.ui.routing.Router
import dev.nycode.omsilauncher.ui.routing.RouterKey
import dev.nycode.omsilauncher.ui.routing.RouterState
import dev.nycode.omsilauncher.ui.routing.rememberRouterState

private val InstanceRoute = RouterKey(name = "Instance")
private val SettingsRoute = RouterKey(name = "Settings")

private class ApplicationRoute(
    val routerKey: RouterKey,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun Application() {
    val routerState = rememberRouterState(InstanceRoute)
    val strings = LocalStrings.current
    val routes = remember {
        listOf(
            ApplicationRoute(InstanceRoute, strings.instances, TablerIcons.Stack),
            ApplicationRoute(SettingsRoute, strings.settings, TablerIcons.Settings)
        )
    }
    Row(modifier = Modifier.fillMaxSize()) {
        ApplicationRouteNavigation(routerState, routes)
        Box(modifier = Modifier.fillMaxSize()) {
            Router(routerState) {
                route(InstanceRoute) {
                    InstanceScreen()
                }
                route(SettingsRoute) {
                    Text(strings.settings, fontSize = 40.sp)
                }
            }
        }
    }
}

@Composable
private fun ApplicationRouteNavigation(
    routerState: RouterState,
    routes: List<ApplicationRoute>,
) {
    NavigationRail {
        for (route in routes) {
            NavigationRailItem(
                selected = routerState.currentRoute == route.routerKey,
                onClick = {
                    routerState.currentRoute = route.routerKey
                },
                icon = {
                    Icon(route.icon, route.label)
                },
                label = {
                    Text(route.label)
                }
            )
        }
    }
}
