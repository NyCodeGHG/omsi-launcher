package dev.nycode.omsilauncher.util

import dev.nycode.omsilauncher.instance.Instance
import java.awt.Desktop
import java.net.URI
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import kotlin.io.path.*

private val logger = logger()

const val OMSI_STEAM_ID = 252530

/**
 * Creates a new identical [Path] in [to], which will have the same directories and symlinks to the original items.
 */
fun Path.mirrorFolder(to: Path) {
    /**
     * Adapts current [Path] to target folder
     */
    fun Path.adapt() = to / relativize(this@mirrorFolder)

    Files.walk(this, 1).forEach {
        val newPath = it.adapt()
        if (it.isDirectory()) {
            logger.debug { "Creating directory $it in $newPath" }
            newPath.createDirectories()
            it.mirrorFolder(newPath) // mirror children of new directory
        } else {
            logger.debug { "Sym-linking $it to $newPath" }
            newPath.createSymbolicLinkPointingTo(it)
        }
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

fun startOmsi() = Desktop.getDesktop().browse(URI("steam://rungameid/$OMSI_STEAM_ID"))
