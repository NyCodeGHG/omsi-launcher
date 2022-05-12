package dev.nycode.omsilauncher.rust

enum class BuildType(val flag: String?) {
    RELEASE("--release"),
    DEBUG(null)
}
