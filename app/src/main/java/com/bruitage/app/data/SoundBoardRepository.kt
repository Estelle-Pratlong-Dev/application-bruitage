package com.bruitage.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bruitage.app.model.SoundButtonConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "sound_board")

class SoundBoardRepository(private val context: Context) {

    private val configsKey = stringPreferencesKey("button_configs")
    private val json = Json { ignoreUnknownKeys = true }

    val configsFlow: Flow<List<SoundButtonConfig>> = context.dataStore.data.map { prefs ->
        val raw = prefs[configsKey]
        if (raw.isNullOrBlank()) {
            emptyList()
        } else {
            runCatching { json.decodeFromString<List<SoundButtonConfig>>(raw) }.getOrDefault(emptyList())
        }
    }

    suspend fun saveConfigs(configs: List<SoundButtonConfig>) {
        val raw = json.encodeToString(configs)
        context.dataStore.edit { prefs ->
            prefs[configsKey] = raw
        }
    }
}
