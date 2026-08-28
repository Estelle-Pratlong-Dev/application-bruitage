package com.bruitage.app.audio

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * Gère la lecture des sons associés aux carrés. Plusieurs sons peuvent être
 * joués simultanément (un MediaPlayer par index de carré).
 */
class AudioPlayerManager(private val context: Context) {

    private val players = mutableMapOf<Int, MediaPlayer>()
    private val fadeJobs = mutableMapOf<Int, Job>()
    private val scope = CoroutineScope(Dispatchers.Main)

    fun play(
        buttonIndex: Int,
        assetFileName: String,
        loop: Boolean,
        volume: Float,
        fade: Boolean,
        onComplete: () -> Unit
    ) {
        stop(buttonIndex)

        val player = try {
            context.assets.openFd("sounds/$assetFileName").use { afd ->
                MediaPlayer().apply {
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    isLooping = loop
                    prepare()
                }
            }
        } catch (e: Exception) {
            return
        }

        players[buttonIndex] = player

        player.setOnCompletionListener {
            if (!loop) {
                stop(buttonIndex)
                onComplete()
            }
        }

        if (fade) {
            player.setVolume(0f, 0f)
            player.start()
            fadeJobs[buttonIndex] = scope.launch {
                fadeVolume(player, from = 0f, to = volume, durationMs = 800)
            }
        } else {
            player.setVolume(volume, volume)
            player.start()
        }
    }

    fun stop(buttonIndex: Int) {
        fadeJobs.remove(buttonIndex)?.cancel()
        players.remove(buttonIndex)?.apply {
            runCatching { stop() }
            release()
        }
    }

    fun stopAll() {
        players.keys.toList().forEach { stop(it) }
    }

    fun isPlaying(buttonIndex: Int): Boolean = players[buttonIndex]?.isPlaying == true

    private suspend fun fadeVolume(player: MediaPlayer, from: Float, to: Float, durationMs: Int) {
        val steps = 20
        val stepDuration = max(1, durationMs / steps).toLong()
        for (i in 1..steps) {
            val v = from + (to - from) * (i / steps.toFloat())
            runCatching { player.setVolume(v, v) }
            delay(stepDuration)
        }
    }

    fun release() {
        stopAll()
    }
}
