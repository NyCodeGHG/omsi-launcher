package dev.nycode.omsilauncher.ui.setup.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.LocalStrings
import dev.nycode.omsilauncher.config.saveConfig
import dev.nycode.omsilauncher.omsi.OMSI_STEAM_ID
import dev.nycode.omsilauncher.ui.components.ClickablePath
import dev.nycode.omsilauncher.ui.setup.SetupState
import dev.nycode.omsilauncher.ui.setup.components.SetupContinueButton
import dev.nycode.omsilauncher.util.getOmsiInstallPath
import dev.nycode.omsilauncher.util.logger
import dev.nycode.omsilauncher.util.parent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.io.path.*

private val logger = logger()

@Composable
fun StartSetupScreen(config: MutableState<SetupState>, closeSetup: () -> Unit) {
    var setupStep: String? by remember { mutableStateOf(null as String?) }
    var setupRunning: Boolean by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val strings = LocalStrings.current
    Box(Modifier.fillMaxSize()) {
        val omsiInstallPath = getOmsiInstallPath()
        Column(Modifier.padding(20.dp).align(Alignment.TopStart)) {
            SizedText(strings.setupLauncherDirectoryHeadline, FontWeight.Bold)
            LittleSpacer()
            ClickablePath(config.value.launcherPath!!)
            BigSpacer()
            SizedText(strings.setupOmsiDirectoryHeadline, FontWeight.Bold)
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
            setupStep = strings.setupStepCopyGameFiles
            omsiInstallPath.moveTo(gameDirectory)
            logger.info { "Successfully moved files from $omsiInstallPath to $gameDirectory." }
            logger.info { "Deleting Omsi.exe" }
            // Delete Omsi.exe as we want to symlink to "_Straßenbahn" patches
            gameDirectory.resolve("Omsi.exe").deleteIfExists()
            setupStep = strings.setupStepCopyManifest
            val manifest = omsiInstallPath.parent(2) / "appmanifest_$OMSI_STEAM_ID.acf"
            manifest.copyTo(gameDirectory / "manifest.acf")
            omsiInstallPath.subpath(0, omsiInstallPath.nameCount - 2)
            setupStep = strings.setupStepSavingConfiguration
            saveConfig(currentConfig)
            val instancesFile = rootInstallation.resolve("instances.json")
            if (instancesFile.notExists()) {
                logger.info { "Creating instances.json" }
                setupStep = strings.setupStepCreatingInstancesJson
                instancesFile
                    .createFile()
                    .writeText("[]")
            }
            closeSetup()
        }
        SetupContinueButton(
            onClick = ::startSetup,
            areaModifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
            enabled = !setupRunning
        ) {
            Text(it.setup)
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
