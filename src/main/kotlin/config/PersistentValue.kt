package dev.nycode.omsilauncher.config

interface PersistentValue<T> {
    fun toSavedData(): T
}
