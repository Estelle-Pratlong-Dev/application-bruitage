package com.bruitage.app.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bruitage.app.SoundBoardViewModel
import com.bruitage.app.model.SoundButtonConfig

private val UnconfiguredColor = Color(0xFF2A2A3D)
private val DefaultTileColor = Color(0xFF2E86AB)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SoundBoardScreen(viewModel: SoundBoardViewModel) {
    val buttons by viewModel.buttons.collectAsState()
    val playing by viewModel.playingIndex.collectAsState()
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.stopAll() },
                icon = { Icon(Icons.Filled.Stop, contentDescription = null) },
                text = { Text("Tout arrêter") }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(buttons, key = { it.index }) { config ->
                SoundTile(
                    config = config,
                    isPlaying = playing.contains(config.index),
                    onTap = { viewModel.onTap(config) },
                    onLongPress = { editingIndex = config.index }
                )
            }
        }
    }

    editingIndex?.let { index ->
        val config = buttons.first { it.index == index }
        EditButtonDialog(
            config = config,
            availableSounds = viewModel.availableSounds,
            onDismiss = { editingIndex = null },
            onSave = { updated ->
                viewModel.updateButton(updated)
                editingIndex = null
            },
            onClear = {
                viewModel.clearButton(index)
                editingIndex = null
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SoundTile(
    config: SoundButtonConfig,
    isPlaying: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    val baseColor = if (config.isConfigured) {
        runCatching { Color(android.graphics.Color.parseColor(config.colorHex)) }.getOrDefault(DefaultTileColor)
    } else {
        UnconfiguredColor
    }
    val tileColor = if (isPlaying) baseColor.copy(alpha = 0.6f) else baseColor

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(tileColor)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap,
                onLongClick = onLongPress
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (config.isConfigured) config.name.ifBlank { "Sans nom" } else "Vide\n(appui long)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
