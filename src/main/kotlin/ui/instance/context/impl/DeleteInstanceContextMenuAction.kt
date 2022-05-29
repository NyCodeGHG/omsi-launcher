package dev.nycode.omsilauncher.ui.instance.context.impl

import dev.nycode.omsilauncher.omsi.OmsiProcessState
import dev.nycode.omsilauncher.ui.instance.context.InstanceActionContext
import dev.nycode.omsilauncher.ui.instance.context.InstanceContextMenuAction

object DeleteInstanceContextMenuAction : InstanceContextMenuAction {
    override fun isAvailable(context: InstanceActionContext): Boolean {
        return context.omsiState == OmsiProcessState.NOT_RUNNING &&
            context.instance.state.isReady && !context.instance.isBaseInstance
    }

    override fun buildItemLabel(context: InstanceActionContext): String {
        return if (context.instance.isBaseInstance) {
            context.strings.cannotDeleteMainInstance
        } else {
            context.strings.deleteInstance
        }
    }

    override fun action(context: InstanceActionContext) = with(context) {
        onDeleteInstance()
    }
}
