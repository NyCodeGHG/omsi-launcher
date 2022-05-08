package dev.nycode.omsilauncher.util

import dev.nycode.omsilauncher.config.config
import dev.nycode.omsilauncher.config.gameDirectory
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.LaunchFlag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.net.URI
import java.nio.file.LinkOption
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createSymbolicLinkPointingTo
import kotlin.io.path.div
import kotlin.io.path.isDirectory
import kotlin.io.path.moveTo

private val logger = logger()

const val OMSI_STEAM_ID = 252530

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
private suspend fun doNativeCall(name: String, vararg parameters: String) = withContext(Dispatchers.IO) {
    val absoluteExecutable = (Path("bin") / name).absolutePathString()
    val process = Runtime.getRuntime().exec(arrayOf(absoluteExecutable, *parameters))

    val logReaders = coroutineScope {
        launch {
            process.inputStream.bufferedReader().lines().forEach {
                logger.debug(it)
            }
        }
        launch {
            process.errorStream.bufferedReader().lines().forEach {
                logger.error(it)
            }
        }
    }


    process.onExit().await()
    logReaders.cancel()
    if (process.exitValue() != 0) {
        error("Unexpected exit code from ${name}: ${process.exitValue()}")
    }
}

fun activateInstallationSafe(path: Path) {
    val omsi = getOmsiInstallPath()
    // we want to detect, whether the installation is a link to a launcher installation
    // If it is we can just safely overwrite it
    if (omsi.isDirectory(LinkOption.NOFOLLOW_LINKS)) {
        logger.warn { "Existing OMSI installation found, backing it up" }
        omsi.moveTo(getOmsiSteamLibrary() / "OMSI 2 - backup - ${System.currentTimeMillis()}")
    }
    logger.debug { """Creating global "OMSI 2" symlink to $path""" }
    omsi.createSymbolicLinkPointingTo(path)
}

fun activateAndStartInstallationSafe(path: Path) {
    activateInstallationSafe(path)
    startOmsi()
}

fun startOmsi(flags: List<LaunchFlag> = emptyList()) {
    val flagsString = flags.joinToString(separator = " ", prefix = "// ")

    Desktop.getDesktop().browse(URI("steam://rungameid/$OMSI_STEAM_ID$flagsString"))
}
