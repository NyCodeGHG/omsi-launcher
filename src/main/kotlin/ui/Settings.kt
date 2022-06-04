package dev.nycode.omsilauncher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.lyricist.Lyricist
import dev.nycode.omsilauncher.config.config
import dev.nycode.omsilauncher.config.saveConfig
import dev.nycode.omsilauncher.localization.Strings
import dev.nycode.omsilauncher.localization.StringsEn
import dev.nycode.omsilauncher.ui.components.CheckboxRow
import dev.nycode.omsilauncher.ui.components.DropdownInputField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SettingsPage(lyricist: Lyricist<Strings>) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var currentConfig by remember { mutableStateOf(config) }
    DisposableEffect(currentConfig) {
        scope.launch(Dispatchers.IO) {
            saveConfig(currentConfig)
        }
        onDispose {}
    }

    Column(Modifier.fillMaxWidth().padding(start = 8.dp)) {
        Text(strings.settings)
        CheckboxRow(strings.useHardLinks, strings.useHardLinksDescription, currentConfig.useHardLinks) {
            currentConfig = currentConfig.copy(useHardLinks = it)
        }
        Spacer(Modifier.height(8.dp))
        val title = if (strings != StringsEn) "${strings.language} (${StringsEn.language})" else strings.language
        Text(title)
        DropdownInputField(Locale.forLanguageTag(lyricist.languageTag), {
            lyricist.languageTag = it.toLanguageTag()
            currentConfig = currentConfig.copy(locale = it.toLanguageTag())
        }, values = listOf(Locale.ENGLISH, Locale.GERMAN), valueToMenuItem = { Text(it.displayName) })
    }
}
