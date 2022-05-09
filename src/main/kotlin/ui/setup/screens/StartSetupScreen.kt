package dev.nycode.omsilauncher.ui.setup.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nycode.omsilauncher.config.saveConfig
import dev.nycode.omsilauncher.ui.components.ClickablePath
import dev.nycode.omsilauncher.ui.setup.SetupState
import dev.nycode.omsilauncher.util.getOmsiInstallPath
import dev.nycode.omsilauncher.util.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.io.path.*

private val logger = logger()

@Composable
fun StartSetupScreen(config: MutableState<SetupState>, closeSetup: () -> Unit) {
    var setupStep: String? by remember { mutableStateOf(null as String?) }
    var setupRunning: Boolean by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Box(Modifier.fillMaxSize()) {
        val omsiInstallPath = getOmsiInstallPath()
        Column(Modifier.padding(20.dp).align(Alignment.TopStart)) {
            SizedText("Launcher Directory:", FontWeight.Bold)
            LittleSpacer()
            ClickablePath(config.value.launcherPath!!)
            BigSpacer()
            SizedText("OMSI Installation:", FontWeight.Bold)
            LittleSpacer()
            ClickablePath(omsiInstallPath)
        }
        if (setupStep != null) {
            Row(Modifier.padding(20.dp).align(Alignment.BottomCenter)) {
                CircularProgressIndicator()
                Text(setupStep!!, fontSize = 25.sp)
            }
        }
        fun startSetup() = scope.launch(Dispatchers.IO) {
            val currentConfig = config.value.toConfiguration()
            val rootInstallation = currentConfig.rootInstallation
            val gameDirectory = currentConfig.gameDirectory
            setupRunning = true
            if (gameDirectory.exists()) {
                TODO("Show dialog")
            }
            logger.info { "Copying game files from $omsiInstallPath to $gameDirectory." }
            setupStep = "Copying game files."
            omsiInstallPath.moveTo(gameDirectory)
            logger.info { "Successfully moved files from $omsiInstallPath to $gameDirectory." }
            logger.info { "Deleting Omsi.exe" }
            // Delete Omsi.exe as we want to symlink to "_Straßenbahn" patches
            gameDirectory.resolve("Omsi.exe").deleteExisting()
            setupStep = "Saving configuration."
            saveConfig(currentConfig)
            val instancesFile = rootInstallation.resolve("instances.json")
            if (instancesFile.notExists()) {
                logger.info { "Creating instances.json" }
                setupStep = "Creating instances.json"
                instancesFile
                    .createFile()
                    .writeText("[]")
            }
            closeSetup()
        }
        Button(
            ::startSetup,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            enabled = !setupRunning
        ) {
            Text("Setup")
        }
    }
}

@Composable
private fun SizedText(text: String, fontWeight: FontWeight? = null) =
    Text(text, fontSize = 25.sp, fontWeight = fontWeight)

@Composable
private fun BigSpacer() = Spacer(Modifier.padding(10.dp))

@Composable
private fun LittleSpacer() = Spacer(Modifier.padding(5.dp))
