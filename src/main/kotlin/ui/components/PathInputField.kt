package dev.nycode.omsilauncher.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import compose.icons.TablerIcons
import compose.icons.tablericons.Folder
import dev.nycode.omsilauncher.util.chooseDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

@Composable
fun PathInputField(
    value: Path?,
    onValueChange: (Path?) -> Unit,
    defaultDirectory: Path,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    label: (@Composable () -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    OutlinedTextField(
        value = value?.absolutePathString() ?: "",
        onValueChange = {
            onValueChange(Path(it))
        },
        readOnly = true,
        trailingIcon = {
            Icon(TablerIcons.Folder, "Folder")
        },
        modifier = modifier.onFocusChanged { state ->
            if (state.isFocused) {
                scope.launch(Dispatchers.IO) {
                    onValueChange(chooseDirectory(defaultDirectory))
                }
            }
        },
        isError = isError,
        label = label
    )
}
