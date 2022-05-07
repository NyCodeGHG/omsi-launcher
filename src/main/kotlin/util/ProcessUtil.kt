package dev.nycode.omsilauncher.util

import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.io.path.absolutePathString
import kotlin.streams.asSequence
import kotlin.time.Duration.Companion.seconds

fun isSteamRunning(): Boolean = steamProcesses().any()

suspend fun awaitSteamDeath() {
    steamProcesses().map {
        it.onExit().asDeferred()
    }
        .toList()
        .awaitAll()
}

private fun steamProcesses(): Sequence<ProcessHandle> {
    val steamPath = getSteamInstallPath()
    return ProcessHandle.allProcesses()
        .asSequence()
        .filter {
            it.info().command().orElse(null)?.startsWith(steamPath.absolutePathString()) == true
        }
}

suspend fun tryCloseSteam(): Boolean {
    val steamProcesses = steamProcesses().toList()
    val processDeaths = steamProcesses.map { it.onExit().asDeferred() }
        .toList()

    for (process in steamProcesses) {
        process.destroy()
    }

    return withTimeoutOrNull(10.seconds) {
        processDeaths.awaitAll()
    } != null
}
