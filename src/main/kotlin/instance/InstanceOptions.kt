package dev.nycode.omsilauncher.instance

import kotlinx.serialization.Serializable

@Serializable
data class InstanceOptions(
    val saveLogs: Boolean = false,
    val debugMode: Boolean = false,
    val logLevel: LogLevel = LogLevel.DEFAULT,
    val screenMode: ScreenMode = ScreenMode.DEFAULT,
) {

    @Serializable
    enum class LogLevel(val launchFlag: LaunchFlag? = null) {
        DEFAULT,
        NO_LOG(LaunchFlag.NO_LOG),
        FULL_LOG(LaunchFlag.LOG_ALL)
    }

    @Serializable
    enum class ScreenMode(val launchFlag: LaunchFlag? = null) {
        DEFAULT,
        WINDOWED(LaunchFlag.WINDOWED),
        FULL_SCREEN(LaunchFlag.FULLSCREEN)
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
