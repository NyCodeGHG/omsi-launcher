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

enum class OmsiProcessUpdate {
    RUNNING,
    NOT_RUNNING
}

fun receiveOmsiProcessUpdates(): Flow<OmsiProcessUpdate> = flow {
    while (currentCoroutineContext().isActive) {
        if (isOmsiRunning()) {
            emit(OmsiProcessUpdate.RUNNING)
            delay(200.milliseconds)
            omsiProcesses().map { it.onExit().asDeferred() }.toList().awaitAll()
            emit(OmsiProcessUpdate.NOT_RUNNING)
        } else {
            delay(200.milliseconds)
        }
    }
}
