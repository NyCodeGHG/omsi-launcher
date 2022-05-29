package dev.nycode.omsilauncher.ui.instance.context.impl

import dev.nycode.omsilauncher.omsi.OmsiProcessState
import dev.nycode.omsilauncher.ui.instance.context.InstanceActionContext
import dev.nycode.omsilauncher.ui.instance.context.InstanceContextMenuAction

object ActivateInstanceContextMenuAction : InstanceContextMenuAction {
    override fun isAvailable(context: InstanceActionContext): Boolean {
        return context.omsiState == OmsiProcessState.NOT_RUNNING &&
            !context.instanceActive &&
            context.instance.state.isReady
    }

    override fun buildItemLabel(context: InstanceActionContext): String {
        return context.strings.activateInstance
    }

    override fun action(context: InstanceActionContext) = with(context) {
        onActivateInstance()
    }
}
