package dev.nycode.omsilauncher.config

import dev.nycode.omsilauncher.util.appDataDir
import dev.nycode.omsilauncher.util.logger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.div
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

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

    val readConfig = configJson.decodeFromString<Configuration>(launcherConfig.readText())
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
