package com.bruitage.app

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bruitage.app.audio.AudioPlayerManager
import com.bruitage.app.data.SoundBoardRepository
import com.bruitage.app.data.SoundLibrary
import com.bruitage.app.data.defaultProfiles
import com.bruitage.app.model.AppSettings
import com.bruitage.app.model.SoundBoardProfile
import com.bruitage.app.model.SoundButtonConfig
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val GRID_SIZE = 16

class SoundBoardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SoundBoardRepository(application)
    private val audioPlayer = AudioPlayerManager(application)

    private val initialProfiles = defaultProfiles()

    private val _profiles = MutableStateFlow(initialProfiles)
    val profiles: StateFlow<List<SoundBoardProfile>> = _profiles.asStateFlow()

    private val _activeProfileId = MutableStateFlow(initialProfiles.first().id)
    val activeProfileId: StateFlow<String> = _activeProfileId.asStateFlow()

    val buttons: StateFlow<List<SoundButtonConfig>> = combine(_profiles, _activeProfileId) { profiles, activeId ->
        profiles.find { it.id == activeId }?.buttons ?: defaultButtons()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, initialProfiles.first().buttons)

    private val _playingIndex = MutableStateFlow<Set<Int>>(emptySet())
    val playingIndex: StateFlow<Set<Int>> = _playingIndex.asStateFlow()

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    val availableSounds: List<String> get() = SoundLibrary.listSounds(getApplication())

    init {
        viewModelScope.launch {
            repository.settingsFlow.collect { _settings.value = it }
        }
        viewModelScope.launch {
            repository.profilesFlow.collect { saved ->
                if (saved.isNotEmpty()) {
                    _profiles.value = saved
                    if (_profiles.value.none { it.id == _activeProfileId.value }) {
                        _activeProfileId.value = saved.first().id
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.activeProfileIdFlow.collect { savedId ->
                if (savedId != null && _profiles.value.any { it.id == savedId }) {
                    _activeProfileId.value = savedId
                }
            }
        }
    }

    private fun persistProfiles(profiles: List<SoundBoardProfile>) {
        _profiles.value = profiles
        viewModelScope.launch { repository.saveProfiles(profiles) }
    }

    fun switchProfile(id: String) {
        if (id == _activeProfileId.value) return
        stopAll()
        _activeProfileId.value = id
        viewModelScope.launch { repository.setActiveProfileId(id) }
    }

    fun createProfile(name: String) {
        val profile = SoundBoardProfile(
            id = UUID.randomUUID().toString(),
            name = name.ifBlank { "Nouveau profil" },
            buttons = defaultButtons()
        )
        persistProfiles(_profiles.value + profile)
        switchProfile(profile.id)
    }

    fun renameProfile(id: String, newName: String) {
        if (newName.isBlank()) return
        persistProfiles(_profiles.value.map { if (it.id == id) it.copy(name = newName) else it })
    }

    fun duplicateProfile(id: String) {
        val source = _profiles.value.find { it.id == id } ?: return
        val copy = source.copy(id = UUID.randomUUID().toString(), name = "${source.name} (copie)")
        persistProfiles(_profiles.value + copy)
        switchProfile(copy.id)
    }

    fun deleteProfile(id: String) {
        if (_profiles.value.size <= 1) return
        val remaining = _profiles.value.filterNot { it.id == id }
        persistProfiles(remaining)
        if (_activeProfileId.value == id) {
            switchProfile(remaining.first().id)
        }
    }

    private fun defaultButtons(): List<SoundButtonConfig> =
        (0 until GRID_SIZE).map { SoundButtonConfig(index = it) }

    fun onTap(config: SoundButtonConfig) {
        if (!config.isConfigured) return
        val index = config.index

        if (_playingIndex.value.contains(index)) {
            stopButton(index)
            return
        }

        // Un seul son à la fois : on coupe ce qui joue avant de lancer le nouveau.
        stopAll()

        audioPlayer.play(
            buttonIndex = index,
            soundFileName = config.soundFile!!,
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
        val activeId = _activeProfileId.value
        val newProfiles = _profiles.value.map { profile ->
            if (profile.id == activeId) {
                profile.copy(buttons = profile.buttons.map { if (it.index == updated.index) updated else it })
            } else {
                profile
            }
        }
        persistProfiles(newProfiles)
    }

    fun clearButton(index: Int) {
        stopButton(index)
        updateButton(SoundButtonConfig(index = index))
    }

    fun clearFeedback() {
        _feedback.value = null
    }

    fun importSoundsFolder(uri: Uri) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                SoundLibrary.importFolder(getApplication(), uri)
            }
            _feedback.value = if (count > 0) {
                "$count son(s) importé(s)"
            } else {
                "Aucun son (.mp3/.wav) trouvé dans ce dossier"
            }
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
        viewModelScope.launch { repository.saveSettings(newSettings) }
    }

    override fun onCleared() {
        audioPlayer.release()
        super.onCleared()
    }
}
