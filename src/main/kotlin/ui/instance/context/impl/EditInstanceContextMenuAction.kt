package dev.nycode.omsilauncher.ui.instance.context.impl

import dev.nycode.omsilauncher.ui.instance.context.InstanceActionContext
import dev.nycode.omsilauncher.ui.instance.context.InstanceContextMenuAction

object EditInstanceContextMenuAction : InstanceContextMenuAction {
    override fun isAvailable(context: InstanceActionContext): Boolean {
        return false
    }

    override fun buildItemLabel(context: InstanceActionContext): String {
        return context.strings.editInstance
    }

    override fun action(context: InstanceActionContext) = with(context) {
        onEditInstance()
    }
}
