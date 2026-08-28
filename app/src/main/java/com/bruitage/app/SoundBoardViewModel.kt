package com.bruitage.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bruitage.app.audio.AudioPlayerManager
import com.bruitage.app.data.SoundBoardRepository
import com.bruitage.app.data.SoundLibrary
import com.bruitage.app.model.SoundButtonConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val GRID_SIZE = 16

class SoundBoardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SoundBoardRepository(application)
    private val audioPlayer = AudioPlayerManager(application)

    private val _buttons = MutableStateFlow(defaultButtons())
    val buttons: StateFlow<List<SoundButtonConfig>> = _buttons.asStateFlow()

    private val _playingIndex = MutableStateFlow<Set<Int>>(emptySet())
    val playingIndex: StateFlow<Set<Int>> = _playingIndex.asStateFlow()

    val availableSounds: List<String> get() = SoundLibrary.listSounds(getApplication())

    init {
        viewModelScope.launch {
            repository.configsFlow.collect { saved ->
                if (saved.isNotEmpty()) {
                    val merged = defaultButtons().map { default ->
                        saved.find { it.index == default.index } ?: default
                    }
                    _buttons.value = merged
                }
            }
        }
    }

    private fun defaultButtons(): List<SoundButtonConfig> =
        (0 until GRID_SIZE).map { SoundButtonConfig(index = it) }

    fun onTap(config: SoundButtonConfig) {
        if (!config.isConfigured) return
        val index = config.index

        if (config.loop && _playingIndex.value.contains(index)) {
            stopButton(index)
            return
        }

        audioPlayer.play(
            buttonIndex = index,
            assetFileName = config.soundFile!!,
            loop = config.loop,
            volume = config.volume,
            fade = config.fade
        ) {
            _playingIndex.value = _playingIndex.value - index
        }
        _playingIndex.value = _playingIndex.value + index
    }

    fun stopButton(index: Int) {
        audioPlayer.stop(index)
        _playingIndex.value = _playingIndex.value - index
    }

    fun stopAll() {
        audioPlayer.stopAll()
        _playingIndex.value = emptySet()
    }

    fun updateButton(updated: SoundButtonConfig) {
        val newList = _buttons.value.map { if (it.index == updated.index) updated else it }
        _buttons.value = newList
        viewModelScope.launch { repository.saveConfigs(newList) }
    }

    fun clearButton(index: Int) {
        stopButton(index)
        updateButton(SoundButtonConfig(index = index))
    }

    override fun onCleared() {
        audioPlayer.release()
        super.onCleared()
    }
}
