package com.bruitage.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bruitage.app.model.SoundButtonConfig

private val PRESET_COLORS = listOf(
    "#2E86AB", "#A23B72", "#F18F01", "#C73E1D",
    "#3B1F2B", "#5B8C5A", "#1B998B", "#6247AA"
)

@Composable
fun EditButtonDialog(
    config: SoundButtonConfig,
    availableSounds: List<String>,
    onDismiss: () -> Unit,
    onSave: (SoundButtonConfig) -> Unit,
    onClear: () -> Unit
) {
    var name by remember(config.index) { mutableStateOf(config.name) }
    var selectedSound by remember(config.index) { mutableStateOf(config.soundFile) }
    var loop by remember(config.index) { mutableStateOf(config.loop) }
    var fade by remember(config.index) { mutableStateOf(config.fade) }
    var volume by remember(config.index) { mutableFloatStateOf(config.volume) }
    var colorHex by remember(config.index) { mutableStateOf(config.colorHex) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurer le carré") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du carré") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))
                Text("Son", style = MaterialTheme.typography.labelLarge)

                if (availableSounds.isEmpty()) {
                    Text(
                        "Aucun son trouvé dans app/src/main/assets/sounds",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 160.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        availableSounds.forEach { sound ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSound = sound }
                            ) {
                                RadioButton(
                                    selected = selectedSound == sound,
                                    onClick = { selectedSound = sound }
                                )
                                Text(sound)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = loop, onCheckedChange = { loop = it })
                    Text("Lecture en boucle (repeat)")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = fade, onCheckedChange = { fade = it })
                    Text("Fondu à l'entrée (fade in)")
                }

                Spacer(Modifier.height(8.dp))
                Text("Volume : ${(volume * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
                Slider(value = volume, onValueChange = { volume = it })

                Spacer(Modifier.height(8.dp))
                Text("Couleur du carré", style = MaterialTheme.typography.labelLarge)
                Row {
                    PRESET_COLORS.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        ColorSwatch(
                            color = color,
                            selected = colorHex == hex,
                            onClick = { colorHex = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        config.copy(
                            name = name,
                            soundFile = selectedSound,
                            loop = loop,
                            fade = fade,
                            volume = volume,
                            colorHex = colorHex
                        )
                    )
                },
                enabled = selectedSound != null
            ) { Text("Enregistrer") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onClear) { Text("Vider") }
                TextButton(onClick = onDismiss) { Text("Annuler") }
            }
        }
    )
}

@Composable
private fun ColorSwatch(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(32.dp)
            .background(color, shape = CircleShape)
            .then(
                if (selected) Modifier.border(2.dp, Color.White, CircleShape) else Modifier
            )
            .clickable(onClick = onClick)
    )
}
