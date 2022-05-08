plugins {
    id("dev.nycode.omsilauncher.rust")
}

tasks {
    task<Copy>("copyRustBinaries") {
        dependsOn(compileRust, downloadElevateHelper)
        from("target/release")
        into(buildDir.resolve("binaries").resolve("windows"))
        include("*.exe")
    }
}
