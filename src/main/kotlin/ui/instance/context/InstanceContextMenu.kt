package dev.nycode.omsilauncher.ui.instance.context

import dev.nycode.omsilauncher.ui.instance.context.impl.ActivateInstanceContextMenuAction
import dev.nycode.omsilauncher.ui.instance.context.impl.DeleteInstanceContextMenuAction
import dev.nycode.omsilauncher.ui.instance.context.impl.EditInstanceContextMenuAction
import dev.nycode.omsilauncher.ui.instance.context.impl.ReSyncInstanceContextMenuAction
import dev.nycode.omsilauncher.ui.instance.context.impl.ShowInstanceFilesAction
import dev.nycode.omsilauncher.ui.instance.context.impl.start.EditorStartInstanceContextMenuAction
import dev.nycode.omsilauncher.ui.instance.context.impl.start.RegularStartInstanceContextMenuAction

val instanceContextMenus: List<InstanceContextMenuAction> =
    listOf(
        RegularStartInstanceContextMenuAction,
        EditorStartInstanceContextMenuAction,
        ActivateInstanceContextMenuAction,
        EditInstanceContextMenuAction,
        DeleteInstanceContextMenuAction,
        ShowInstanceFilesAction,
        ReSyncInstanceContextMenuAction
    )
