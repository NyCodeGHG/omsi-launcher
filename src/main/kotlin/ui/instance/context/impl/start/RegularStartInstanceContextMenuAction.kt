package dev.nycode.omsilauncher.ui.instance.context.impl.start

import dev.nycode.omsilauncher.ui.instance.context.InstanceActionContext

object RegularStartInstanceContextMenuAction : AbstractStartInstanceContextMenuAction() {
    override val editorMode: Boolean = false

    override fun buildItemLabel(context: InstanceActionContext): String {
        return context.strings.startInstance
    }
}
