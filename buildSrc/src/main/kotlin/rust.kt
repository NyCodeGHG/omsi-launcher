import dev.nycode.omsilauncher.LocalProperties
import dev.nycode.omsilauncher.localProperties
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.getByName

fun Exec.rust(binary: String, vararg args: String, fileEnding: String = "exe") {
    group = "rust"
    workingDir = project.rootProject.projectDir.resolve("fs-util")
    commandLine =
        listOf(
            path(
                fileEnding,
                project.localProperties.rustSdk,
                "bin",
                binary
            ), *args
        )
}
