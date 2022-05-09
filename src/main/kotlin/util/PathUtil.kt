package dev.nycode.omsilauncher.util

import java.nio.file.Path
import kotlin.io.path.Path

val appDataDir: Path
    get() = Path(System.getenv("APPDATA"))

fun String.asPath(): Path = Path(this)

fun Path.parent(n: Int): Path = root.resolve(subpath(0, nameCount - n))
