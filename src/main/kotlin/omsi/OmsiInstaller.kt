package dev.nycode.omsilauncher.omsi

import dev.nycode.omsilauncher.config.config
import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.instance.LaunchFlag
import dev.nycode.omsilauncher.util.getOmsiInstallPath
import dev.nycode.omsilauncher.util.getOmsiSteamManifest
import dev.nycode.omsilauncher.util.isSteamRunning
import dev.schlaubi.stdx.logging.logger
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
const val OMSI_STEAM_MANIFEST_NAME = "appmanifest_$OMSI_STEAM_ID.acf"
private const val UAC_CANCELLED = 1223

private val releaseMode = System.getProperty("dev.nycode.omsi_launcher.release") != null

private val linkingFlag
    get() = if (config.useHardLinks) {
        "--hard-link-binary"
    } else {
        null
    }


suspend fun createInstance(instance: Instance, basePath: Path? = null, doMultiLink: Boolean = false) {
    doNativeCall(
        "clone-omsi.exe",
        (basePath ?: config.gameDirectory).absolutePathString(),
        instance.directory.absolutePathString(),
        getOmsiBinary(instance).absolutePathString(),
        linkingFlag,
        "--do-multi-symlink".takeIf { doMultiLink }
    )
}

suspend fun reLinkOmsiExecutable(instance: Instance) {
    doNativeCall(
        "clone-omsi.exe",
        config.gameDirectory.absolutePathString(),
        instance.directory.absolutePathString(),
        getOmsiBinary(instance).absolutePathString(),
        "--only-link-binary",
        linkingFlag
    )
}

suspend fun relinkBaseGame(instances: List<Instance>, baseInstance: Path) {
    val instanceCliArguments = instances.flatMap {
        listOf("--omsi-instance-folder", it.directory.absolutePathString())
    }

    doNativeCall(
        "relink-omsi.exe",
        baseInstance.absolutePathString(),
        *instanceCliArguments.toTypedArray()
    )
}

/**
 * Creates a new identical [Path] in [to], which will have the same directories and symlinks to the original items.
 */
private suspend fun doNativeCall(name: String, vararg parameters: String?) =
    withContext(Dispatchers.IO) {
        val realParameters = parameters.filterNotNull().toTypedArray()
        logger.debug { "Attempting native call: $name ${realParameters.joinToString(" ")}" }
        val basePath =
            if (releaseMode) Path("app") / "resources" else Path("fs-util") / "build" / "binaries" / "windows"
        val absoluteExecutable = (basePath / name).absolutePathString()
        val process = ProcessBuilder().command(absoluteExecutable, *realParameters)
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

suspend fun activateInstallationSafe(
    instance: Instance,
    startSteam: Boolean = false,
    awaitSteamDeath: suspend () -> Boolean
): Boolean {
    val omsi = getOmsiInstallPath()
    val binary = getOmsiBinary(instance)
    if (omsi.isSymbolicLink() && omsi.readSymbolicLink() == instance.directory) {
        val currentManifest = getOmsiSteamManifest()
        if (currentManifest.exists()) {
            logger.debug { "Backing up $currentManifest to current installation ${instance.directory}" }
            currentManifest.copyTo(instance.directory / "manifest.acf", true)
            val currentBinary = instance.directory / "Omsi.exe"
            if (currentBinary.exists() && (currentBinary.isSymbolicLink() && currentBinary.readSymbolicLink() == binary)
                || (config.useHardLinks xor currentBinary.isSymbolicLink())
            ) {
                return true
            }
        }
    }
    val isSteamRunning = isSteamRunning()

    if (!isSteamRunning || awaitSteamDeath()) {
        doNativeCall(
            "activate-omsi.exe",
            omsi.absolutePathString(),
            instance.directory.absolutePathString(),
            binary.absolutePathString(),
            linkingFlag
        )
        if (isSteamRunning && startSteam) {
            withContext(Dispatchers.IO) {
                runSteam()
            }
        }
        return true
    }

    return false
}

suspend fun activateAndStartInstallationSafe(
    instance: Instance,
    flags: List<LaunchFlag>,
    awaitSteamDeath: suspend () -> Boolean = { true }
) {
    if (activateInstallationSafe(instance, awaitSteamDeath = awaitSteamDeath)) {
        startOmsi(flags)
    }
}

fun startOmsi(flags: List<LaunchFlag> = emptyList()) {
    val flagsString =
        if (flags.isEmpty()) "" else "// " + flags.joinToString(separator = " ") { it.option }

    runSteam("rungameid/$OMSI_STEAM_ID$flagsString")
}

private fun runSteam(arguments: String = "") {
    Runtime.getRuntime()
        .exec(
            arrayOf(
                "rundll32",
                "url.dll,FileProtocolHandler",
                "steam://$arguments"
            )
        )
}

class UserAccessControlCancelledException : RuntimeException()
