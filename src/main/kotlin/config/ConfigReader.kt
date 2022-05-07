package dev.nycode.omsilauncher.config

import dev.nycode.omsilauncher.util.appDataDir
import dev.nycode.omsilauncher.util.logger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.*

private val json = Json {
    prettyPrint = true
}

private val logger = logger()

fun readConfig(): Configuration {
    logger.debug("Reading configuration directory.")

    val launcherConfig = getLauncherConfigFile()
    if (launcherConfig.notExists()) {
        logger.debug("Launcher configuration file does not exist!")
        launcherConfig.createFile()
        val defaultConfig = Configuration(null)
        launcherConfig.writeText(json.encodeToString(defaultConfig))
    }

    return json.decodeFromString(launcherConfig.readText())
}

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
    configFile.writeText(json.encodeToString(configuration))
}
