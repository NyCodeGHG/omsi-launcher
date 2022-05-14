package dev.nycode.omsilauncher.omsi

import dev.nycode.omsilauncher.config.config
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.LaunchFlag
import dev.nycode.omsilauncher.util.getOmsiInstallPath
import dev.nycode.omsilauncher.util.logger
import dev.nycode.omsilauncher.util.parent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.isSymbolicLink
import kotlin.io.path.readSymbolicLink

private val logger = logger()

const val OMSI_STEAM_ID = 252530
private const val UAC_CANCELLED = 1223

private val releaseMode = System.getProperty("dev.nycode.omsi_launcher.release") != null

suspend fun createInstance(instance: Instance) {
    doNativeCall(
        "clone-omsi.exe",
        config.gameDirectory.absolutePathString(),
        instance.directory.absolutePathString(),
        getOmsiBinary(instance).absolutePathString()
    )
}

suspend fun reLinkOmsiExecutable(instance: Instance) {
    doNativeCall(
        "clone-omsi.exe",
        config.gameDirectory.absolutePathString(),
        instance.directory.absolutePathString(),
        getOmsiBinary(instance).absolutePathString(),
        "--only-link-binary"
    )
}

suspend fun relinkBaseGame(instances: List<Instance>) {
    val instanceCliArguments = instances.flatMap {
        listOf("--omsi-instance-folder", it.directory.absolutePathString())
    }

    doNativeCall(
        "relink-omsi.exe",
        config.gameDirectory.absolutePathString(),
        *instanceCliArguments.toTypedArray()
    )
}

/**
 * Creates a new identical [Path] in [to], which will have the same directories and symlinks to the original items.
 */
private suspend fun doNativeCall(name: String, vararg parameters: String) =
    withContext(Dispatchers.IO) {
        logger.debug { "Attempting native call: $name ${parameters.joinToString(" ")}" }
        val basePath =
            if (releaseMode) Path("app") / "resources" else Path("fs-util") / "build" / "binaries" / "windows"
        val absoluteExecutable = (basePath / name).absolutePathString()
        val process = ProcessBuilder().command(absoluteExecutable, *parameters)
            .redirectErrorStream(true)
            .start()

        launch {
            process.inputStream.bufferedReader().lineSequence().forEach {
                logger.info(it)
            }
        }

        process.onExit().await()
        when (process.exitValue()) {
            0 -> {}
            UAC_CANCELLED -> throw UserAccessControlCancelledException()
            else -> error("Unexpected exit code from $name: ${process.exitValue()}")
        }
        logger.debug { "Successfully finished native call." }
    }

suspend fun activateInstallationSafe(path: Path) {
    val omsi = getOmsiInstallPath()
    if (omsi.isSymbolicLink() && omsi.readSymbolicLink() == path) {
        val currentManifest = omsi.parent(2) / "appmanifest_$OMSI_STEAM_ID.acf"
        if (currentManifest.exists()) {
            logger.debug { "Backing up $currentManifest to current installation $path" }
            currentManifest.copyTo(path / "manifest.acf", true)
            return
        }
    }
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

class UserAccessControlCancelledException : RuntimeException()
