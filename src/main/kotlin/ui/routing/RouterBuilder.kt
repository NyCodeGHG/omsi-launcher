package dev.nycode.omsilauncher.ui.routing

class RouterBuilder(val routes: MutableMap<RouterKey, Route> = mutableMapOf()) {
    fun route(key: RouterKey, route: Route) {
        routes[key] = route
    }
}
