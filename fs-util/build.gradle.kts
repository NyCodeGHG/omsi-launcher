plugins {
    id("dev.nycode.omsilauncher.rust")
}

tasks {
    val binFolder = task<Copy>("copyIntoBinFolder") {
        dependsOn(compileRust, downloadElevateHelper)
        from("fs-util/target/release")
        into("bin")
        include("*.exe")
    }

    task<Copy>("copyBinariesIntoBuildFolder") {
        dependsOn(compileRust, downloadElevateHelper)
        from(binFolder)
        into(buildDir.resolve("binaries").resolve("windows"))
    }
}
