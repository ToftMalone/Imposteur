package com.toftmalone.imposteur.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "imposteur")

/**
 * Stores the roster, the tuning options and any custom packs. Everything lives
 * on device; the game never talks to a network.
 */
class ImposteurRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val settings: Flow<GameSettings> = context.dataStore.data.map { prefs ->
        prefs[KEY_SETTINGS]?.let { raw ->
            runCatching { json.decodeFromString(GameSettings.serializer(), raw) }.getOrNull()
        } ?: GameSettings()
    }

    val players: Flow<List<Player>> = context.dataStore.data.map { prefs ->
        prefs[KEY_PLAYERS]?.let { raw ->
            runCatching {
                json.decodeFromString(ListSerializer(Player.serializer()), raw)
            }.getOrNull()
        } ?: emptyList()
    }

    val customPacks: Flow<List<WordPack>> = context.dataStore.data.map { prefs ->
        prefs[KEY_CUSTOM_PACKS]?.let { raw ->
            runCatching {
                json.decodeFromString(ListSerializer(WordPack.serializer()), raw)
            }.getOrNull()
        } ?: emptyList()
    }

    suspend fun saveSettings(value: GameSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SETTINGS] = json.encodeToString(GameSettings.serializer(), value)
        }
    }

    suspend fun savePlayers(value: List<Player>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PLAYERS] = json.encodeToString(ListSerializer(Player.serializer()), value)
        }
    }

    suspend fun saveCustomPacks(value: List<WordPack>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CUSTOM_PACKS] =
                json.encodeToString(ListSerializer(WordPack.serializer()), value)
        }
    }

    private companion object {
        val KEY_SETTINGS = stringPreferencesKey("settings")
        val KEY_PLAYERS = stringPreferencesKey("players")
        val KEY_CUSTOM_PACKS = stringPreferencesKey("custom_packs")
    }
}
