package dev.nycode.omsilauncher.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.nfd.NativeFileDialog
import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.UIManager
import kotlin.io.path.absolutePathString

private val logger = logger()

suspend fun chooseDirectory(basePath: Path? = null): Path? {
    return runCatching { chooseDirectoryNative(basePath) }
        .onFailure { nativeException ->
            logger.error("A call to chooseDirectoryNative failed.", nativeException)

            return runCatching { chooseDirectorySwing(basePath) }
                .onFailure { swingException ->
                    logger.error("A call to chooseDirectorySwing failed.", swingException)
                }
                .getOrNull()
                ?.asPath()
        }
        .getOrNull()
        ?.asPath()
}

private suspend fun chooseDirectoryNative(basePath: Path?) = withContext(Dispatchers.IO) {
    val pathPointer = MemoryUtil.memAllocPointer(1)
    try {
        return@withContext when (val code =
            NativeFileDialog.NFD_PickFolder(basePath?.absolutePathString(), pathPointer)) {
            NativeFileDialog.NFD_OKAY -> {
                val path = pathPointer.stringUTF8
                NativeFileDialog.nNFD_Free(pathPointer[0])

                path
            }
            NativeFileDialog.NFD_CANCEL -> null
            NativeFileDialog.NFD_ERROR -> error("An error occurred while executing NativeFileDialog.NFD_PickFolder")
            else -> error("Unknown return code '${code}' from NativeFileDialog.NFD_PickFolder")
        }
    } finally {
        MemoryUtil.memFree(pathPointer)
    }
}

private suspend fun chooseDirectorySwing(basePath: Path?) = withContext(Dispatchers.IO) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    val chooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        isVisible = true
        currentDirectory = basePath?.toFile()
    }

    when (val code = chooser.showOpenDialog(null)) {
        JFileChooser.APPROVE_OPTION -> chooser.selectedFile.absolutePath
        JFileChooser.CANCEL_OPTION -> null
        JFileChooser.ERROR_OPTION -> error("An error occurred while executing JFileChooser::showOpenDialog")
        else -> error("Unknown return code '${code}' from JFileChooser::showOpenDialog")
    }
}
