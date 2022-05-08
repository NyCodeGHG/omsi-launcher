package dev.nycode.omsilauncher.rust.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

abstract class DownloadElevateHelperTask : DefaultTask() {

    @get:Input
    abstract val url: Property<String>

    @get:Input
    abstract val fileName: Property<String>

    @get:OutputDirectory
    abstract val downloadDirectory: Property<File>

    init {
        downloadDirectory.convention(project.buildDir.resolve("download"))
        url.convention("https://github.com/NyCodeGHG/omsi-elevate/releases/latest/download/omsi-elevate.exe")
        fileName.convention("omsi-elevate.exe")
    }

    @TaskAction
    fun downloadElevateHelper() {
        val path = downloadDirectory.get().resolve(fileName.get()).toPath()
        Channels.newChannel(
            URL(url.get())
                .openStream()
        ).use { readChannel ->
            FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
                .use { outputChannel ->
                    outputChannel.transferFrom(readChannel, 0, Long.MAX_VALUE)
                }
        }
    }
}
