package com.bruitage.app.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val tileMinSizeDp: Int = 110,
    val darkTheme: Boolean = true,
    val keepScreenOn: Boolean = true,
    val brightness: Float = 1f
)
