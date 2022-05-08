package dev.nycode.omsilauncher.util

import dev.nycode.omsilauncher.config.config
import dev.nycode.omsilauncher.config.gameDirectory
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.LaunchFlag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.div
import kotlin.io.path.isSymbolicLink
import kotlin.io.path.readSymbolicLink

private val logger = logger()

const val OMSI_STEAM_ID = 252530

private val releaseMode = System.getProperty("dev.nycode.omsi_launcher.release") != null

suspend fun createInstance(instance: Instance) {
    doNativeCall(
        "clone-omsi.exe",
        config.gameDirectory.absolutePathString(),
        instance.directory.absolutePathString(),
        config.gameDirectory.resolve(instance.patchVersion.relativePath).absolutePathString()
    )
}

/**
 * Creates a new identical [Path] in [to], which will have the same directories and symlinks to the original items.
 */
private suspend fun doNativeCall(name: String, vararg parameters: String) =
    withContext(Dispatchers.IO) {
        logger.debug { "Attempting native call: $name ${parameters.joinToString(" ")}" }
        val basePath = if (releaseMode) Path("app") / "resources" else Path("bin")
        val absoluteExecutable = (basePath / name).absolutePathString()
        val process = ProcessBuilder().command(absoluteExecutable, *parameters)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .start()

        process.onExit().await()
        if (process.exitValue() != 0) {
            error("Unexpected exit code from $name: ${process.exitValue()}")
        }
        logger.debug { "Successfully finished native call." }
    }

suspend fun activateInstallationSafe(path: Path) {
    val omsi = getOmsiInstallPath()
    if (omsi.isSymbolicLink() && omsi.readSymbolicLink() == path) return
    doNativeCall("activate-omsi.exe", omsi.absolutePathString(), path.absolutePathString())
}

suspend fun activateAndStartInstallationSafe(path: Path, flags: List<LaunchFlag>) {
    activateInstallationSafe(path)
    startOmsi(flags)
}

fun startOmsi(flags: List<LaunchFlag> = emptyList()) {
    val flagsString =
        if (flags.isEmpty()) "" else "// " + flags.joinToString(separator = " ") { it.option }

    Runtime.getRuntime()
        .exec(
            arrayOf(
                "rundll32",
                "url.dll,FileProtocolHandler",
                "steam://rungameid/$OMSI_STEAM_ID$flagsString"
            )
        )
}
