package dev.nycode.omsilauncher

import dev.nycode.omsilauncher.config.readConfig
import dev.nycode.omsilauncher.util.getApplicationTitle
import dev.nycode.omsilauncher.util.logger
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

private val logger = logger()

fun main(args: Array<String>) {
    logger.debug("Starting ${getApplicationTitle()}.")
    val arguments = parseArgs(args)
    val configuration = readConfig()
    val setup = configuration == null || arguments.forceSetup
    when {
        setup && !arguments.forceSetup -> {
            logger.info("Root Installation Directory is not set. Starting setup.")
        }
        setup -> {
            logger.info("Force Setup flag is set. Forcing setup.")
        }
        else -> {
            logger.debug("Root Installation Directory is set. Skipping setup.")
        }
    }
    runLauncher(setup, configuration)
}

fun parseArgs(args: Array<String>): ApplicationArgs {
    logger.debug("Parsing application arguments.")
    val parser = ArgParser("omsi-launcher")
    val forceSetup by parser.option(
        ArgType.Boolean,
        shortName = "f",
        description = "Force Setup.",
        fullName = "force-setup"
    )
        .default(false)
    parser.parse(args)
    return ApplicationArgs(forceSetup)
}

class ApplicationArgs(val forceSetup: Boolean)
