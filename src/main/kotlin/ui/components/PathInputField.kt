package dev.nycode.omsilauncher.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.LocalStrings
import compose.icons.TablerIcons
import compose.icons.tablericons.Folder
import dev.nycode.omsilauncher.util.chooseDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

@Composable
fun PathInputField(
    value: Path?,
    onValueChange: (Path?) -> Unit,
    defaultDirectory: Path? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
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
        enabled = enabled,
        trailingIcon = {
            IconButton({
                chooseDirectory()
            }, enabled = enabled) {
                Icon(TablerIcons.Folder, "Folder")
            }
        },
        modifier = modifier,
        isError = isError,
        label = label,
        placeholder = placeholder
    )
}

@Composable
fun EmptyDirectoryPathField(
    value: Path?,
    onValueChange: (Path?) -> Unit,
    defaultDirectory: Path? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
) {
    val isError = value != null && value.isDirectory() && value.listDirectoryEntries().isNotEmpty()
    PathInputField(
        value = value,
        onValueChange = onValueChange,
        defaultDirectory = defaultDirectory,
        modifier = modifier,
        isError = isError,
        enabled = enabled,
        label = {
            if (isError && enabled) {
                Text(LocalStrings.current.directoryNeedsToBeEmpty)
            } else {
                label?.invoke()
            }
        },
        placeholder = placeholder
    )
}
