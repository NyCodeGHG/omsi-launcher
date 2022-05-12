package dev.nycode.omsilauncher.instance

import dev.nycode.omsilauncher.util.getOmsiInstallPath
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.nio.file.Path
import kotlin.io.path.isSymbolicLink
import kotlin.io.path.readSymbolicLink
import kotlin.time.Duration.Companion.milliseconds

fun receiveCurrentInstancePath(): Flow<Path> = flow {
    var lastPath: Path? = null
    while (currentCoroutineContext().isActive) {
        val currentInstancePath = getCurrentInstancePath()
        if (lastPath != currentInstancePath) {
            emit(currentInstancePath)
        }
        lastPath = currentInstancePath
        delay(200.milliseconds)
    }
}

fun getCurrentInstancePath(): Path {
    val omsiPath = getOmsiInstallPath()
    return if (omsiPath.isSymbolicLink()) omsiPath.readSymbolicLink() else omsiPath
}
