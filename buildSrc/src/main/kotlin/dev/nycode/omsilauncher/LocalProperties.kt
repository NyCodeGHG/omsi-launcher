package dev.nycode.omsilauncher

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByName
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

private class LocalPropertiesPlugin : Plugin<Project> {
    private val properties: Properties = Properties()

    override fun apply(target: Project) {
        target.extensions.create<LocalProperties>("localProperties", properties)

        val file = target.rootDir.toPath().resolve("local.properties")

        if (!Files.exists(file)) {
            Files.createFile(file)
        }

        Files.newBufferedReader(file).use { properties.load(it) }

    }
}
val Project.localProperties: LocalProperties
    get() = extensions.getByName<LocalProperties>("localProperties")

abstract class LocalProperties(private val properties: Properties) {
    val rustSdk: String
        get() = this["rust.sdk"]

    operator fun get(name: String) = System.getenv("RUST_SDK") ?: properties.getProperty(name) ?: notFound(name)

    private fun notFound(name: String): Nothing = error("Please specify $name in local.properties")
}

private val properties = Properties().apply {
    val properties = Path.of("local.properties")
    if (!Files.exists(properties)) {
        Files.createFile(properties)
    }

    load(Files.newBufferedReader(properties))
}
