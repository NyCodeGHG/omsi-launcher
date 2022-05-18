package dev.nycode.omsilauncher.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.util.awaitSteamDeath
import dev.nycode.omsilauncher.util.tryCloseSteam
import kotlinx.coroutines.launch

@Composable
fun SteamProcessScreen(description: String, onDeath: () -> Unit) = Box(Modifier.fillMaxSize()) {
    val scope = rememberCoroutineScope()
    var closingSteam by remember { mutableStateOf(false) }
    var steamErrorDialog by remember { mutableStateOf(false) }
    val strings = LocalStrings.current
    DisposableEffect(Unit) {
        scope.launch {
            awaitSteamDeath()
            onDeath()
        }
        onDispose {}
    }
    Column(Modifier.align(Alignment.Center)) {
        Text(
            description,
            Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally).padding(10.dp))
        Button(
            {
                closingSteam = true
                scope.launch {
                    if (!tryCloseSteam()) {
                        steamErrorDialog = true
                        closingSteam = false
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = !closingSteam
        ) {
            Text(strings.closeSteam)
        }
        if (steamErrorDialog) {
            ErrorDialog(
                strings.unableToCloseSteam,
                {
                    steamErrorDialog = false
                },
                strings.unableToCloseSteamTitle
            )
        }
    }
}

@Composable
private fun ErrorDialog(text: String, onCloseRequest: () -> Unit, title: String) =
    Dialog(onCloseRequest, title = title, resizable = false) {
        Box(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth()) {
                Text(text, Modifier.align(Alignment.CenterVertically))
            }
            Row(Modifier.fillMaxWidth().align(Alignment.BottomCenter)) {
                Button(
                    onCloseRequest,
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error)
                ) {
                    Text(LocalStrings.current.ok)
                }
            }
        }
    }
