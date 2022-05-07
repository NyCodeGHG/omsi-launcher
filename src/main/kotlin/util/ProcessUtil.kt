package dev.nycode.omsilauncher.util

import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asDeferred
import kotlin.io.path.absolutePathString
import kotlin.streams.asSequence

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
