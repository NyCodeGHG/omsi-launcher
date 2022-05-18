package dev.nycode.omsilauncher.util

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

private val unsafePathRegex = """\W""".toRegex()

val appDataDir: Path
    get() = Path(System.getenv("APPDATA"))

fun String.asPath(): Path = Path(this)

fun Path.parent(n: Int): Path = root.resolve(subpath(0, nameCount - n))

operator fun Path.contains(other: Path?): Boolean {
    if (other == null) return false

    return other.absolutePathString().startsWith(absolutePathString())
}
fun String.sanitize(): String = replace(unsafePathRegex, "_")
