package dev.nycode.omsilauncher.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
    defaultDirectory: Path? = null,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
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
        singleLine = true,
        trailingIcon = {
            IconButton({
                chooseDirectory()
            }) {
                Icon(TablerIcons.Folder, "Folder")
            }
        },
        modifier = modifier,
        isError = isError,
        label = label,
        placeholder = placeholder
    )
}
