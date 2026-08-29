package com.bruitage.app.model

import kotlinx.serialization.Serializable

@Serializable
data class SoundBoardProfile(
    val id: String,
    val name: String,
    val buttons: List<SoundButtonConfig>
)
