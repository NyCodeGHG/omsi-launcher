package dev.nycode.omsilauncher.config

import dev.nycode.omsilauncher.util.appDataDir
import dev.schlaubi.stdx.logging.logger
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.nio.file.Path
import javax.swing.JOptionPane
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.deleteExisting
import kotlin.io.path.div
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.system.exitProcess

val configJson = Json {
    prettyPrint = true
    encodeDefaults = true
}

private val logger = logger()

lateinit var config: Configuration
    private set

fun readConfig(): Configuration? {
    logger.debug("Reading configuration directory.")

    val launcherConfig = getLauncherConfigFile()
    if (launcherConfig.notExists()) {
        logger.debug("Launcher configuration file does not exist!")
        launcherConfig.createFile()
        return null
    }

    val configText = try {
        launcherConfig.readText()
    } catch (e: IOException) {
        logger.error("Reading configuration file failed. ", e)
        return null
    }

    val readConfig = try {
        configJson.decodeFromString<Configuration>(configText)
    } catch (e: SerializationException) {
        val (text, exit) = if (configText.isBlank()) {
            launcherConfig.deleteExisting()
            "Your config file seems to be corrupted. We have reset your configuration." to false
        } else {
            "Your config file seems to be corrupted. Please check ${launcherConfig.absolutePathString()} and make sure it's valid JSON." to true
        }
        logger.error(text)
        if (exit) {
            JOptionPane.showConfirmDialog(
                null,
                text,
                "An error occurred",
                JOptionPane.OK_OPTION,
                JOptionPane.ERROR_MESSAGE
            )
            exitProcess(1)
        }
        return null
    }
    config = readConfig
    return readConfig
}

fun resolveAppDataPath(path: String) = config.rootInstallation / path

private fun getLauncherDirectory(): Path {
    val launcherDirectory = appDataDir.resolve("omsi-launcher")
    if (launcherDirectory.notExists()) {
        logger.debug("Launcher configuration directory does not exist!")
        launcherDirectory.createDirectory()
    }
    return launcherDirectory
}

private fun getLauncherConfigFile(): Path {
    return getLauncherDirectory().resolve("config.json")
}

fun saveConfig(configuration: Configuration) {
    logger.debug("Saving configuration.")

    val configFile = getLauncherConfigFile()
    if (configFile.notExists()) {
        configFile.createFile()
    }
    config = configuration
    configFile.writeText(configJson.encodeToString(configuration))
}
