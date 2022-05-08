import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import java.nio.file.Files

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev679"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
    id("local-properties")
}

group = "dev.nycode"
version = "0.1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.3.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.6.1")
    implementation("org.jetbrains.kotlinx", "kotlinx-cli", "0.3.4")
    implementation("io.github.microutils", "kotlin-logging", "2.1.21")
    runtimeOnly("ch.qos.logback", "logback-classic", "1.2.11")
    implementation("net.java.dev.jna", "jna-platform", "5.11.0")
    implementation("org.lwjgl", "lwjgl", "3.3.1")
    implementation("org.lwjgl", "lwjgl", "3.3.1", classifier = "natives-windows")
    implementation("org.lwjgl", "lwjgl-nfd", "3.3.1")
    implementation("org.lwjgl", "lwjgl-nfd", "3.3.1", classifier = "natives-windows")
    implementation("br.com.devsrsouza.compose.icons.jetbrains", "tabler-icons", "1.0.0")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
        }
    }

    val compileRust = task<Exec>("compileRust") {
        rust("cargo", "build", "--release")
    }

    val downloadElevate = task("downloadElevateExe") {
        val path = projectDir.toPath().resolve("bin").resolve("omsi-elevate.exe")
        outputs.files(path)
        doLast {
            if (Files.exists(path) && !gradle.startParameter.isRerunTasks) {
                didWork = false
                return@doLast
            }
            val readChannel = Channels.newChannel(
                URL("https://github.com/NyCodeGHG/omsi-elevate/releases/download/Continuous/omsi-elevate.exe")
                    .openStream()
            )
            val outputChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)

            outputChannel.transferFrom(readChannel, 0, Long.MAX_VALUE)
        }
    }

    val binFolder = task<Copy>("copyIntoBinFolder") {
        dependsOn(compileRust, downloadElevate)
        from("fs-util/target/release")
        into("bin")
        include("*.exe")
    }

    val copyResources = task<Copy>("copyBinariesIntoBuildFolder") {
        dependsOn(compileRust, downloadElevate)
        from(binFolder)
        into(buildDir.resolve("binaries").resolve("windows"))
    }

    afterEvaluate {
        "packageMsi" {
            dependsOn(copyResources)
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

compose.desktop {
    application {
        mainClass = "dev.nycode.omsilauncher.MainKt"
        jvmArgs += listOf("-Ddev.nycode.omsi_launcher.release")

        nativeDistributions {
            modules("java.naming")
            targetFormats(TargetFormat.Msi)

            appResourcesRootDir.set(buildDir.resolve("binaries"))
            licenseFile.set(project.file("LICENSE"))
        }
    }
}

buildConfig {
    packageName("dev.nycode.omsilauncher.build")
    buildConfigField("String", "APP_VERSION", "\"${project.version}\"")
    buildConfigField("String", "APP_BRANCH", "\"${project.getGitBranch()}\"")
    buildConfigField("String", "APP_COMMIT", "\"${project.getGitCommit()}\"")
}
