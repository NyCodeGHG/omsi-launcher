import dev.nycode.omsilauncher.rust.BuildType

plugins {
    id("dev.nycode.omsilauncher.rust")
}

tasks {
    compileRust {
        if (project.findProperty("dev.nycode.omsilauncher.rust.debug_binary") != null) {
            buildType.set(BuildType.DEBUG)
        }
    }
    task<Copy>("copyRustBinaries") {
        dependsOn(compileRust, downloadElevateHelper)
        from("target/release", downloadElevateHelper)
        into(buildDir.resolve("binaries").resolve("windows"))
        include("*.exe")
    }
}
