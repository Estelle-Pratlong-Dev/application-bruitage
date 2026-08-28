package com.bruitage.app.model

import kotlinx.serialization.Serializable

@Serializable
data class SoundButtonConfig(
    val index: Int,
    val name: String = "",
    val soundFile: String? = null,
    val colorHex: String = "#2E86AB",
    val loop: Boolean = false,
    val volume: Float = 1f,
    val fade: Boolean = false
) {
    val isConfigured: Boolean get() = soundFile != null
}
