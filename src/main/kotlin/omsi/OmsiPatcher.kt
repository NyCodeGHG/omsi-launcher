package dev.nycode.omsilauncher.omsi

import dev.nycode.omsilauncher.config.config
import dev.nycode.omsilauncher.instance.Instance
import io.sigpipe.jbsdiff.Patch
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.outputStream
import kotlin.io.path.readBytes

fun getOmsiBinary(instance: Instance) = getOmsiBinary(instance.patchVersion, instance.uses4GBPatch)

/**
 * This retrieves (and creates if necessary) the desired [patched] binary for [edition].
 *
 * Binaries are stored in [gameDirectory] the following way
 *
 * Base version: _Straßenbahn/[Instance.PatchVersion.executable]
 *
 * Patched version: \_Straßenbahn/Omsi\_[Instance.PatchVersion.type].4gbpatch.exe
 */
fun getOmsiBinary(edition: Instance.PatchVersion, patched: Boolean): Path {
    return if (!patched) {
        config.gameDirectory / edition.relativePath
    } else {
        getPatchedBinary(edition)
    }
}

private fun getPatchedBinary(edition: Instance.PatchVersion): Path {
    val path = config.gameDirectory / edition.relativePath.parent /
        ("Omsi_" + edition.type + ".4gbpatch.exe")
    if (path.exists()) {
        return path
    }

    patch(config.gameDirectory / edition.relativePath, edition.executable, path)
    return path
}

private fun patch(originalFile: Path, patchName: String, outputFile: Path) {
    val patch = readPatchFile(patchName)
    val originalFileBytes = originalFile.readBytes()
    outputFile.outputStream().use { outputStream ->
        Patch.patch(originalFileBytes, patch, outputStream)
    }
}

private fun readPatchFile(name: String): ByteArray =
    ClassLoader.getSystemResourceAsStream("4gb_patch/$name.gz")!!.use { it.readAllBytes() }
