package dev.nycode.omsilauncher.rust

import dev.nycode.omsilauncher.rust.tasks.CargoTask
import dev.nycode.omsilauncher.rust.tasks.DownloadElevateHelperTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class RustPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.create("compileRust", CargoTask::class) {
            subCommand.set(listOf("build"))
        }
        target.tasks.create("downloadElevateHelper", DownloadElevateHelperTask::class) {
            group = "other"
        }
    }
}
