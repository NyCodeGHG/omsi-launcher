package dev.nycode.omsilauncher.omsi

import dev.nycode.omsilauncher.util.isOmsiRunning
import dev.nycode.omsilauncher.util.omsiProcesses
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

enum class OmsiProcessState {
    RUNNING,
    NOT_RUNNING
}

fun receiveOmsiProcessUpdates(): Flow<OmsiProcessState> = flow {
    while (currentCoroutineContext().isActive) {
        if (isOmsiRunning()) {
            emit(OmsiProcessState.RUNNING)
            delay(200.milliseconds)
            omsiProcesses().map { it.onExit().asDeferred() }.toList().awaitAll()
            emit(OmsiProcessState.NOT_RUNNING)
        } else {
            delay(200.milliseconds)
        }
    }
}
