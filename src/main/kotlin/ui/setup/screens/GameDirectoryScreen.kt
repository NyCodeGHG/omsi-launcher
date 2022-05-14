package dev.nycode.omsilauncher.ui.setup.screens

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.ui.components.PathInputField
import dev.nycode.omsilauncher.ui.routing.RouterState
import dev.nycode.omsilauncher.ui.setup.ExplainSteamRoute
import dev.nycode.omsilauncher.ui.setup.SetupState
import dev.nycode.omsilauncher.ui.setup.components.SetupContinueButton

@Composable
fun GameDirectoryScreen(routerState: RouterState, config: MutableState<SetupState>) {
    var configuration by config
    var gameFileDirectory by remember { mutableStateOf(configuration.launcherPath) }
    val strings = LocalStrings.current
    val buttonFocusRequester = remember { FocusRequester() }
    val textFieldFocusRequester = remember { FocusRequester() }
    Box(Modifier.fillMaxSize()) {
        Text(
            text = strings.setup,
            fontSize = 35.sp,
            modifier = Modifier.align(Alignment.TopCenter),
            style = MaterialTheme.typography.h1
        )
        Column(
            modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = strings.setupChooseDirectory
            )
            Text(
                text = strings.setupChooseDirectoryWarning,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))
            PathInputField(
                value = gameFileDirectory,
                onValueChange = { directory ->
                    gameFileDirectory = directory
                    buttonFocusRequester.requestFocus()
                },
                modifier = Modifier
                    .focusRequester(textFieldFocusRequester)
                    .focusable()
            ) {
                Text(strings.chooseGameFileDirectory)
            }
        }
        SetupContinueButton(
            onClick = {
                configuration = configuration.copy(launcherPath = gameFileDirectory)
                routerState.currentRoute = ExplainSteamRoute
            },
            buttonModifier = Modifier.focusRequester(buttonFocusRequester)
                .focusable(),
            areaModifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
            enabled = gameFileDirectory != null,
        )
    }
}
