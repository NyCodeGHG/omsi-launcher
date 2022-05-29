package dev.nycode.omsilauncher.util

import mu.KLogger
import mu.KotlinLogging

fun logger(): KLogger =
    KotlinLogging.logger(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass.name)
