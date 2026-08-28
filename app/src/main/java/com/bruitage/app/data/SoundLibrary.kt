package com.bruitage.app.data

import android.content.Context

object SoundLibrary {

    fun listSounds(context: Context): List<String> {
        return try {
            context.assets.list("sounds")
                ?.filter { it.endsWith(".mp3", ignoreCase = true) || it.endsWith(".wav", ignoreCase = true) }
                ?.sorted()
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
