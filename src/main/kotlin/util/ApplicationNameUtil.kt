package dev.nycode.omsilauncher.util

import dev.nycode.omsilauncher.build.BuildConfig.APP_BRANCH
import dev.nycode.omsilauncher.build.BuildConfig.APP_COMMIT
import dev.nycode.omsilauncher.build.BuildConfig.APP_VERSION

fun getApplicationTitle(): String {
    return "omsi-launcher v$APP_VERSION-$APP_COMMIT@$APP_BRANCH"
}
