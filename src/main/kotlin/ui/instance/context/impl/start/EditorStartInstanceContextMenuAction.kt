package dev.nycode.omsilauncher.ui.instance.context.impl.start

import dev.nycode.omsilauncher.ui.instance.context.InstanceActionContext

object EditorStartInstanceContextMenuAction : AbstractStartInstanceContextMenuAction() {
    override val editorMode: Boolean = true

    override fun buildItemLabel(context: InstanceActionContext): String {
        return context.strings.startEditorInstance
    }
}
