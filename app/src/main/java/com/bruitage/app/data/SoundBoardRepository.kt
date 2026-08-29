package com.bruitage.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bruitage.app.model.AppSettings
import com.bruitage.app.model.SoundBoardProfile
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "sound_board")

class SoundBoardRepository(private val context: Context) {

    private val profilesKey = stringPreferencesKey("profiles")
    private val activeProfileIdKey = stringPreferencesKey("active_profile_id")
    private val settingsKey = stringPreferencesKey("app_settings")

    private val json = Json { ignoreUnknownKeys = true }
    private val prettyJson = Json { ignoreUnknownKeys = true; prettyPrint = true }

    val profilesFlow: Flow<List<SoundBoardProfile>> = context.dataStore.data.map { prefs ->
        val raw = prefs[profilesKey]
        if (raw.isNullOrBlank()) {
            emptyList()
        } else {
            runCatching { json.decodeFromString<List<SoundBoardProfile>>(raw) }.getOrDefault(emptyList())
        }
    }

    val activeProfileIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[activeProfileIdKey]
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val raw = prefs[settingsKey]
        if (raw.isNullOrBlank()) {
            AppSettings()
        } else {
            runCatching { json.decodeFromString<AppSettings>(raw) }.getOrDefault(AppSettings())
        }
    }

    suspend fun saveProfiles(profiles: List<SoundBoardProfile>) {
        val raw = json.encodeToString(profiles)
        context.dataStore.edit { prefs ->
            prefs[profilesKey] = raw
        }
        writeExportFile(profiles)
    }

    suspend fun setActiveProfileId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[activeProfileIdKey] = id
        }
    }

    suspend fun saveSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[settingsKey] = json.encodeToString(settings)
        }
    }

    /** Recopie systématiquement les profils dans un fichier lisible à la racine du
     * dossier privé de l'appli (visible en USB), pour pouvoir les récupérer facilement
     * sans action manuelle — par exemple pour les figer ensuite comme profils par défaut
     * dans une future version de l'appli. */
    private fun writeExportFile(profiles: List<SoundBoardProfile>) {
        runCatching {
            val file = File(context.getExternalFilesDir(null), "profils_bruitage.json")
            file.writeText(prettyJson.encodeToString(profiles))
        }
    }
}
