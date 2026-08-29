package com.bruitage.app.ui

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bruitage.app.model.AppSettings

@Composable
fun SettingsDialog(
    settings: AppSettings,
    onDismiss: () -> Unit,
    onSave: (AppSettings) -> Unit
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }

    var darkTheme by remember { mutableStateOf(settings.darkTheme) }
    var keepScreenOn by remember { mutableStateOf(settings.keepScreenOn) }
    var tileSize by remember { mutableFloatStateOf(settings.tileMinSizeDp.toFloat()) }
    var brightness by remember { mutableFloatStateOf(settings.brightness) }
    var volume by remember {
        mutableFloatStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Paramètres") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Thème sombre", modifier = Modifier.weight(1f))
                    Switch(checked = darkTheme, onCheckedChange = { darkTheme = it })
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Garder l'écran allumé", modifier = Modifier.weight(1f))
                    Switch(checked = keepScreenOn, onCheckedChange = { keepScreenOn = it })
                }

                Spacer(Modifier.height(16.dp))

                Text("Luminosité de l'écran", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = brightness,
                    onValueChange = { brightness = it },
                    valueRange = 0.05f..1f
                )

                Spacer(Modifier.height(8.dp))

                Text("Volume", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = volume,
                    onValueChange = {
                        volume = it
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, it.toInt(), 0)
                    },
                    valueRange = 0f..maxVolume.toFloat()
                )

                Spacer(Modifier.height(16.dp))

                Text("Taille des carrés", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = when {
                        tileSize <= 90f -> "Petit (plus de carrés par ligne)"
                        tileSize >= 160f -> "Grand (moins de carrés par ligne)"
                        else -> "Moyen"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = tileSize,
                    onValueChange = { tileSize = it },
                    valueRange = 70f..200f
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    settings.copy(
                        darkTheme = darkTheme,
                        keepScreenOn = keepScreenOn,
                        tileMinSizeDp = tileSize.toInt(),
                        brightness = brightness
                    )
                )
            }) { Text("Enregistrer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
