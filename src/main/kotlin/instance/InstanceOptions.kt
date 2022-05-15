package dev.nycode.omsilauncher.instance

import dev.nycode.omsilauncher.localization.Strings
import dev.nycode.omsilauncher.localization.Translatable
import kotlinx.serialization.Serializable

@Serializable
data class InstanceOptions(
    val saveLogs: Boolean = false,
    val debugMode: Boolean = false,
    val logLevel: LogLevel = LogLevel.DEFAULT,
    val screenMode: ScreenMode = ScreenMode.DEFAULT,
) {

    @Serializable
    enum class LogLevel(val launchFlag: LaunchFlag? = null, override val translation: Strings.() -> String) :
        Translatable {
        DEFAULT(translation = { defaultLogLevel }),
        NO_LOG(LaunchFlag.NO_LOG, { noLogLogLevel }),
        FULL_LOG(LaunchFlag.LOG_ALL, { logAllLogLevel });

        companion object {
            val VALUES = values().asList()
        }
    }

    @Serializable
    enum class ScreenMode(val launchFlag: LaunchFlag? = null, override val translation: Strings.() -> String) :
        Translatable {
        DEFAULT(translation = { defaultWindowMode }),
        WINDOWED(LaunchFlag.WINDOWED, { windowedWindowMode }),
        FULL_SCREEN(LaunchFlag.FULLSCREEN, { fullScreenWindowMode });

        companion object {
            val VALUES = values().asList()
        }
    }

    fun toLaunchFlags(): List<LaunchFlag> = buildList {
        if (saveLogs) {
            add(LaunchFlag.SAVE_LOGS)
        }
        if (debugMode) {
            add(LaunchFlag.DEBUG)
        }
        if (logLevel.launchFlag != null) {
            add(logLevel.launchFlag)
        }

        if (screenMode.launchFlag != null) {
            add(screenMode.launchFlag)
        }
    }
}
