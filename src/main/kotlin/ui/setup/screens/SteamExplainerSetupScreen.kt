package dev.nycode.omsilauncher.ui.setup.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.ui.routing.RouterState
import dev.nycode.omsilauncher.ui.setup.StartSetupRoute
import dev.nycode.omsilauncher.ui.setup.SteamRoute
import dev.nycode.omsilauncher.ui.setup.components.SetupContinueButton
import dev.nycode.omsilauncher.util.isSteamRunning

@Composable
fun SteamExplainerSetupScreen(routerState: RouterState) {
    val strings = LocalStrings.current
    Box(Modifier.fillMaxSize()) {
        Text(
            strings.createBaseGame,
            fontSize = 35.sp,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        Column(Modifier.align(Alignment.Center)) {
            Text(
                strings.baseGameExplanation,
                modifier = Modifier.padding(50.dp),
                textAlign = TextAlign.Justify,
                fontSize = 35.sp
            )
        }
        SetupContinueButton(
            onClick = {
                if (isSteamRunning()) {
                    routerState.currentRoute = SteamRoute
                } else {
                    routerState.currentRoute = StartSetupRoute
                }
            },
            areaModifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
        )
    }
}
