package dev.nycode.omsilauncher.rust.tasks

import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class CargoTask : Exec() {
    @get:Input
    abstract val subCommand: ListProperty<String>

    @get:InputDirectory
    val srcDirectory = project.projectDir.resolve("src")

    @get:OutputDirectory
    val targetDirectory = project.projectDir.resolve("target")

    init {
        group = "cargo"
    }

    @TaskAction
    override fun exec() {
        workingDir = project.projectDir
        commandLine = listOf("cargo") + (subCommand.orNull ?: emptyList())
        super.exec()
    }
}
