package dev.nycode.omsilauncher.util

import dev.nycode.omsilauncher.localization.Strings
import dev.schlaubi.stdx.logging.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.nfd.NativeFileDialog
import java.io.File
import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileFilter
import kotlin.io.path.absolutePathString

private val logger = logger()

suspend fun chooseImage(basePath: Path?, strings: Strings): Path? = chooseFSItem(basePath, false, listOf("png", "jpg", "gif"), strings.onlyImages)
suspend fun chooseDirectory(basePath: Path?): Path? = chooseFSItem(basePath, true, emptyList(), null)
suspend fun chooseFSItem(
    basePath: Path?,
    chooseDirectory: Boolean,
    types: List<String>,
    filterDescription: String?
): Path? {
    return runCatching { chooseFSItemNative(basePath, chooseDirectory, types) }
        .onFailure { nativeException ->
            logger.error("A call to chooseDirectoryNative failed.", nativeException)

            return runCatching { chooseFSItemSwing(basePath, chooseDirectory, types, filterDescription) }
                .onFailure { swingException ->
                    logger.error("A call to chooseDirectorySwing failed.", swingException)
                }
                .getOrNull()
                ?.asPath()
        }
        .getOrNull()
        ?.asPath()
}

private suspend fun chooseFSItemNative(basePath: Path?, chooseDirectory: Boolean, types: List<String>) =
    withContext(Dispatchers.IO) {
        val pathPointer = MemoryUtil.memAllocPointer(1)
        try {
            val basePathString = basePath?.absolutePathString()
            return@withContext when (
                val code = if (chooseDirectory) {
                    NativeFileDialog.NFD_PickFolder(basePathString, pathPointer)
                } else {
                    NativeFileDialog.NFD_OpenDialog(types.joinToString(","), basePathString, pathPointer)
                }
            ) {
                NativeFileDialog.NFD_OKAY -> {
                    val path = pathPointer.stringUTF8
                    NativeFileDialog.nNFD_Free(pathPointer[0])

                    path
                }
                NativeFileDialog.NFD_CANCEL -> null
                NativeFileDialog.NFD_ERROR -> error("An error occurred while executing NativeFileDialog.NFD_PickFolder")
                else -> error("Unknown return code '$code' from NativeFileDialog.NFD_PickFolder")
            }
        } finally {
            MemoryUtil.memFree(pathPointer)
        }
    }

private suspend fun chooseFSItemSwing(
    basePath: Path?,
    chooseDirectory: Boolean,
    types: List<String>,
    filterDescription: String?
) = withContext(Dispatchers.IO) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    val chooser = JFileChooser().apply {
        fileSelectionMode = if (chooseDirectory) JFileChooser.DIRECTORIES_ONLY else JFileChooser.FILES_ONLY
        if (types.isNotEmpty()) {
            fileFilter = object : FileFilter() {
                override fun accept(f: File?): Boolean = f?.extension in types

                override fun getDescription(): String? = filterDescription
            }
        }
        isVisible = true
        currentDirectory = basePath?.toFile()
    }

    when (val code = chooser.showOpenDialog(null)) {
        JFileChooser.APPROVE_OPTION -> chooser.selectedFile.absolutePath
        JFileChooser.CANCEL_OPTION -> null
        JFileChooser.ERROR_OPTION -> error("An error occurred while executing JFileChooser::showOpenDialog")
        else -> error("Unknown return code '$code' from JFileChooser::showOpenDialog")
    }
}
