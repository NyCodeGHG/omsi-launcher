package dev.nycode.omsilauncher.ui.instance.context.impl

import dev.nycode.omsilauncher.ui.instance.context.InstanceActionContext
import dev.nycode.omsilauncher.ui.instance.context.InstanceContextMenuAction

object ReSyncInstanceContextMenuAction : InstanceContextMenuAction {
    override fun isAvailable(context: InstanceActionContext): Boolean =
        context.instance.isBaseInstance && context.instanceActive

    override fun buildItemLabel(context: InstanceActionContext): String = context.strings.reSyncInstance

    override fun action(context: InstanceActionContext) = context.onReSyncInstance()
}
