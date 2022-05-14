package dev.nycode.omsilauncher.ui.instance.context

import androidx.compose.foundation.ContextMenuItem

interface InstanceContextMenuAction {
    fun isAvailable(context: InstanceActionContext): Boolean

    fun buildItemLabel(context: InstanceActionContext): String

    fun buildMenuItem(context: InstanceActionContext): ContextMenuItem {
        return ContextMenuItem(buildItemLabel(context)) {
            action(context)
        }
    }

    fun action(context: InstanceActionContext)
}
