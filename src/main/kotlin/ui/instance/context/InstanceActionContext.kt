package dev.nycode.omsilauncher.ui.instance.context

import dev.nycode.omsilauncher.instance.Instance
import dev.nycode.omsilauncher.localization.Strings
import dev.nycode.omsilauncher.omsi.OmsiProcessState

data class InstanceActionContext(
    val strings: Strings,
    val omsiState: OmsiProcessState,
    val instance: Instance,
    val instanceActive: Boolean,
    val onStartInstance: (editor: Boolean) -> Unit,
    val onEditInstance: () -> Unit,
    val onDeleteInstance: () -> Unit,
    val onActivateInstance: () -> Unit,
)
