package dev.nycode.omsilauncher.ui.instance.context.impl

import dev.nycode.omsilauncher.ui.instance.context.InstanceActionContext
import dev.nycode.omsilauncher.ui.instance.context.InstanceContextMenuAction
import java.awt.Desktop

object ShowInstanceFilesAction : InstanceContextMenuAction {
    // This is not dependent on anything (selected instance, is OMSI running)
    override fun isAvailable(context: InstanceActionContext): Boolean = true

    override fun buildItemLabel(context: InstanceActionContext): String = context.strings.showInstanceFiles

    override fun action(context: InstanceActionContext) {
        Desktop.getDesktop().browse(context.instance.directory.toUri())
    }
}
