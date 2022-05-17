package dev.nycode.omsilauncher.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    requiresEmptyDirectory: Boolean = false,
    onValueChange: (Path?) -> Unit,
    defaultDirectory: Path? = null,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    var showIsEmptyError by remember { mutableStateOf(false) }

    fun changeDirectory(directory: Path?) {
        if (requiresEmptyDirectory) {
            showIsEmptyError =
                directory != null && directory.isDirectory() && directory.listDirectoryEntries()
                    .isNotEmpty()
        }
        onValueChange(directory)
    }

    val chooseDirectory = {
        scope.launch(Dispatchers.IO) {
            changeDirectory(chooseDirectory(defaultDirectory))
        }
    }
    OutlinedTextField(
        value = value?.absolutePathString() ?: "",
        onValueChange = {
            changeDirectory(Path(it))
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
        isError = isError || showIsEmptyError,
        label = if (showIsEmptyError) {
            {
                Text(LocalStrings.current.directoryNeedsToBeEmpty)
            }
        } else {
            label
        },
        placeholder = placeholder
    )
}
