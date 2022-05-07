package dev.nycode.omsilauncher.setup.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nycode.omsilauncher.components.ClickablePath
import dev.nycode.omsilauncher.config.Configuration
import dev.nycode.omsilauncher.config.saveConfig
import dev.nycode.omsilauncher.util.getOmsiInstallPath

@Composable
fun StartSetupScreen(config: MutableState<Configuration>, closeSetup: () -> Unit) =
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.padding(20.dp).align(Alignment.TopStart)) {
            SizedText("Launcher Directory:", FontWeight.Bold)
            LittleSpacer()
            ClickablePath(config.value.rootInstallation!!)
            BigSpacer()
            SizedText("OMSI Installation:", FontWeight.Bold)
            LittleSpacer()
            ClickablePath(getOmsiInstallPath())
        }
        fun startSetup() {
            saveConfig(config.value)
            closeSetup()
        }
        Button(::startSetup, modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)) {
            Text("Setup")
        }
    }

@Composable
private fun SizedText(text: String, fontWeight: FontWeight? = null) =
    Text(text, fontSize = 25.sp, fontWeight = fontWeight)

@Composable
private fun BigSpacer() = Spacer(Modifier.padding(10.dp))

@Composable
private fun LittleSpacer() = Spacer(Modifier.padding(5.dp))
