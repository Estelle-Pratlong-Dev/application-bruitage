package com.bruitage.app.data

import com.bruitage.app.GRID_SIZE
import com.bruitage.app.model.SoundBoardProfile
import com.bruitage.app.model.SoundButtonConfig
import java.util.UUID

private data class PresetButton(val name: String, val soundFile: String, val colorHex: String)

/**
 * Palette utilisée pour les profils fournis par défaut. Réutilisée telle quelle dans
 * EditButtonDialog pour que la couleur d'un bouton apparaisse bien sélectionnée.
 */
val DEFAULT_PALETTE = listOf(
    "#2E86AB", "#E63946", "#F4A300", "#588157",
    "#6247AA", "#1B998B", "#A23B72", "#8D6A9F",
    "#C1440E", "#2A6F97", "#B5838D", "#4A4E69"
)

private val MANEGE_1_BUTTONS = listOf(
    PresetButton("Attention au départ", "aabdepart1.wav", DEFAULT_PALETTE[0]),
    PresetButton("Restez assis", "aacassis1.wav", DEFAULT_PALETTE[1]),
    PresetButton("Arrivée", "aaharrivee1.wav", DEFAULT_PALETTE[2]),
    PresetButton("Attendre l'arrêt", "aamattendez2court.wav", DEFAULT_PALETTE[3]),
    PresetButton("Prenez place", "allez2.wav", DEFAULT_PALETTE[4]),
    PresetButton("Pompon", "aadpompon1.wav", DEFAULT_PALETTE[5]),
    PresetButton("Tarif réduit", "aarreduit1.wav", DEFAULT_PALETTE[6]),
    PresetButton("Ceinture", "aafceinture.wav", DEFAULT_PALETTE[7]),
    PresetButton("Bouton avions", "aajboutons1.wav", DEFAULT_PALETTE[8]),
    PresetButton("Tickets", "facilitefantillusion.wav", DEFAULT_PALETTE[9]),
    PresetButton("Achetez avant", "achetezavant.wav", DEFAULT_PALETTE[10])
)

private val ECHANGEUR_BUTTONS = listOf(
    PresetButton("Attention au départ", "degagez.wav", DEFAULT_PALETTE[0]),
    PresetButton("Restez assis", "aacassis1.wav", DEFAULT_PALETTE[1]),
    PresetButton("Arrivée", "arrivee3.wav", DEFAULT_PALETTE[2]),
    PresetButton("Attendre l'arrêt", "aamattendez2court.wav", DEFAULT_PALETTE[3]),
    PresetButton("Prenez place", "aagcaroule1.wav", DEFAULT_PALETTE[4]),
    PresetButton("Pompon", "pompon2.wav", DEFAULT_PALETTE[5]),
    PresetButton("Tarif réduit", "aarreduit1.wav", DEFAULT_PALETTE[6]),
    PresetButton("Ceinture", "aafceinture.wav", DEFAULT_PALETTE[7]),
    PresetButton("Bouton avions", "aajboutons1.wav", DEFAULT_PALETTE[8]),
    PresetButton("Tickets", "aaifacilite.wav", DEFAULT_PALETTE[9]),
    PresetButton("Achetez avant", "achetezavant.wav", DEFAULT_PALETTE[10])
)

private fun buildProfile(name: String, presets: List<PresetButton>): SoundBoardProfile {
    val buttons = (0 until GRID_SIZE).map { index ->
        val preset = presets.getOrNull(index)
        if (preset != null) {
            SoundButtonConfig(
                index = index,
                name = preset.name,
                soundFile = preset.soundFile,
                colorHex = preset.colorHex
            )
        } else {
            SoundButtonConfig(index = index)
        }
    }
    return SoundBoardProfile(id = UUID.randomUUID().toString(), name = name, buttons = buttons)
}

fun defaultProfiles(): List<SoundBoardProfile> = listOf(
    buildProfile("Manège 1", MANEGE_1_BUTTONS),
    buildProfile("Echangeur", ECHANGEUR_BUTTONS)
)
