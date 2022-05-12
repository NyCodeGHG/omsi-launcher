package dev.nycode.omsilauncher.rust.tasks

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import dev.nycode.omsilauncher.rust.BuildType

abstract class CargoTask : Exec() {
    @get:Input
    abstract val subCommand: ListProperty<String>

    @get:Input
    abstract val buildType: Property<BuildType>

    @get:InputDirectory
    val srcDirectory = project.projectDir.resolve("src")

    @get:OutputDirectory
    val targetDirectory = project.projectDir.resolve("target")

    init {
        group = "cargo"
        buildType.convention(BuildType.RELEASE)
    }

    @TaskAction
    override fun exec() {
        workingDir = project.projectDir
        commandLine = listOf("cargo") + (subCommand.orNull ?: emptyList()) + listOfNotNull(buildType.get().flag)
        super.exec()
    }
}
