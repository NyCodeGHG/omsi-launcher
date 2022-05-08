package dev.nycode.omsilauncher.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ConfirmationDialog(
    text: String,
    onCloseRequest: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) =
    Dialog({ onCloseRequest(false) }) {
        Box(modifier.fillMaxSize()) {
            Text(
                text,
                Modifier.align(Alignment.Center).padding(20.dp),
                textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
            Row(Modifier.align(Alignment.BottomCenter).padding(10.dp)) {
                Button({
                    onCloseRequest(true)
                }) {
                    Text("Yes")
                }
                Spacer(Modifier.width(20.dp))
                Button({
                    onCloseRequest(false)
                }) {
                    Text("No")
                }
            }
        }
    }
