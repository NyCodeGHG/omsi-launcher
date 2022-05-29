package dev.nycode.omsilauncher.ui.instance.context.impl.start

import dev.nycode.omsilauncher.omsi.OmsiProcessState
import dev.nycode.omsilauncher.ui.instance.context.InstanceActionContext
import dev.nycode.omsilauncher.ui.instance.context.InstanceContextMenuAction

abstract class AbstractStartInstanceContextMenuAction : InstanceContextMenuAction {

    protected abstract val editorMode: Boolean

    override fun isAvailable(context: InstanceActionContext): Boolean {
        return context.omsiState == OmsiProcessState.NOT_RUNNING &&
            context.instance.state.isReady
    }

    override fun action(context: InstanceActionContext) = with(context) {
        onStartInstance(editorMode)
    }
}
