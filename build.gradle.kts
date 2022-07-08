import org.jetbrains.changelog.date
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev686"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("com.google.devtools.ksp") version "1.6.21-1.0.5"
    id("org.jetbrains.changelog") version "1.3.1"
}

group = "dev.nycode"
version = "0.6.4"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.3.3")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.6.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-cli", "0.3.5")
    implementation("io.github.microutils", "kotlin-logging", "2.1.23")
    runtimeOnly("ch.qos.logback", "logback-classic", "1.2.11")
    implementation("net.java.dev.jna", "jna-platform", "5.12.1")
    implementation("org.lwjgl", "lwjgl", "3.3.1")
    implementation("org.lwjgl", "lwjgl", "3.3.1", classifier = "natives-windows")
    implementation("org.lwjgl", "lwjgl-nfd", "3.3.1")
    implementation("org.lwjgl", "lwjgl-nfd", "3.3.1", classifier = "natives-windows")
    implementation("br.com.devsrsouza.compose.icons.jetbrains", "tabler-icons", "1.0.0")

    implementation("cafe.adriel.lyricist", "lyricist", "1.2.2")

    ksp("cafe.adriel.lyricist", "lyricist-processor", "1.2.2")
    implementation(platform("dev.schlaubi:stdx-bom:1.1.0"))
    implementation("dev.schlaubi", "stdx-serialization")

    implementation("io.sigpipe", "jbsdiff", "1.0")
}

sourceSets {
    main {
        java {
            srcDir(file("$buildDir/generated/ksp/main/kotlin/"))
        }
    }
}

ktlint {
    disabledRules.add("no-wildcard-imports")
    version.set("0.45.2")

    filter {
        exclude { element ->
            val path = element.file.absolutePath
            path.contains("build") || path.contains("generated") || element.isDirectory
        }
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs =
                freeCompilerArgs + "-opt-in=kotlinx.serialization.ExperimentalSerializationApi" +
                    "-opt-in=kotlinx.serialization.InternalSerializationApi"
            jvmTarget = "18"
        }
    }
    afterEvaluate {
        "packageMsi" {
            dependsOn(":fs-util:copyRustBinaries")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}

changelog {
    version.set(project.version.toString())
    path.set("${project.projectDir}/CHANGELOG.md")
    header.set(provider { "[${version.get()}] - ${date()}" })
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
}

compose.desktop {
    application {
        mainClass = "dev.nycode.omsilauncher.MainKt"
        jvmArgs += listOf("-Ddev.nycode.omsi_launcher.release")

        nativeDistributions {
            modules(
                "java.naming", // Logback
                "jdk.unsupported" // LWJGL NFD
            )
            targetFormats(TargetFormat.Msi)

            appResourcesRootDir.set(project("fs-util").buildDir.resolve("binaries"))
            licenseFile.set(project.file("LICENSE"))
            vendor = "omsi-launcher Contributors"
            windows {
                /// Generate icon file with: convert -resize x128 src/main/resources/ecitaro.jpg src/main/resources/drawables/windows.ico
                iconFile.set(project.file("src/main/resources/drawables/windows.ico"))
                menu = true
                dirChooser = true
            }
        }
    }
}

buildConfig {
    packageName("dev.nycode.omsilauncher.build")
    buildConfigField("String", "APP_VERSION", "\"${project.version}\"")
    buildConfigField("String", "APP_BRANCH", "\"${project.getGitBranch()}\"")
    buildConfigField("String", "APP_COMMIT", "\"${project.getGitCommit()}\"")
}
