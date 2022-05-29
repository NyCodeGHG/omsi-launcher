package dev.nycode.omsilauncher.instance

enum class InstanceState(val showLoadingIndicator: Boolean = false, val isReady: Boolean = false) {
    READY(isReady = true),
    CREATING(showLoadingIndicator = true),
    DELETING(showLoadingIndicator = true),
    UPDATING(showLoadingIndicator = true)
}
