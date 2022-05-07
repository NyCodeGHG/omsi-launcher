package dev.nycode.omsilauncher.util

import mu.KotlinLogging
import org.slf4j.Logger

fun logger(): Logger {
    return KotlinLogging.logger(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass.name)
}
