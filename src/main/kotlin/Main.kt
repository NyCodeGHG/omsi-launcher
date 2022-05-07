package dev.nycode.omsilauncher

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.application
import dev.nycode.omsilauncher.config.readConfig
import dev.nycode.omsilauncher.setup.Setup
import dev.nycode.omsilauncher.util.logger


suspend fun main() {
    val logger = logger()
    logger.debug("Starting omsi-launcher.")
    val configuration = readConfig()
    application {
        MaterialTheme {
            if (configuration.rootInstallation == null) {
                Setup()
            }
        }
    }
}
