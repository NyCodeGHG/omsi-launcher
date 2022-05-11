package dev.nycode.omsilauncher.ui.routing

import java.util.*

class RouterKey(val id: UUID = UUID.randomUUID(), val name: String) {
    override fun equals(other: Any?): Boolean {
        return if (other is RouterKey) {
            other.id == id
        } else false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
