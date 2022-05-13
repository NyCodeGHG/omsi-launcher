package dev.nycode.omsilauncher.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import compose.icons.TablerIcons
import compose.icons.tablericons.Folder
import dev.nycode.omsilauncher.util.chooseDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PathInputField(
    value: Path?,
    onValueChange: (Path?) -> Unit,
    defaultDirectory: Path? = null,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    label: (@Composable () -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val chooseDirectory = {
        scope.launch(Dispatchers.IO) {
            onValueChange(chooseDirectory(defaultDirectory))
        }
    }
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
                chooseDirectory()
            }
        }.onKeyEvent {
            if (it.key == Key.Enter) {
                chooseDirectory()
                return@onKeyEvent true
            } else {
                return@onKeyEvent false
            }
        },
        isError = isError,
        label = label
    )
}
