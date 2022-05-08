import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev679"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
    id("local-properties")
}

group = "dev.nycode"
version = "0.1.0-alpha"

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

    task<Copy>("copyIntoBinFolder") {
        dependsOn(compileRust)
        from("fs-util/target/release")
        into("bin")
        include("*.exe")
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
    }
}

buildConfig {
    packageName("dev.nycode.omsilauncher.build")
    buildConfigField("String", "APP_VERSION", "\"${project.version}\"")
    buildConfigField("String", "APP_BRANCH", "\"${project.getGitBranch()}\"")
    buildConfigField("String", "APP_COMMIT", "\"${project.getGitCommit()}\"")
}
