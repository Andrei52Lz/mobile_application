package com.example.exem

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "favorites_store")

class FavoritesStore(private val context: Context) {

    private val KEY = stringSetPreferencesKey("favorites_set")

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY] ?: emptySet()
    }

    suspend fun isFavorite(text: String): Boolean {
        return favoritesFlow.first().contains(text)
    }

    suspend fun add(text: String) {
        context.dataStore.edit { prefs ->
            val old = prefs[KEY] ?: emptySet()
            prefs[KEY] = old + text
        }
    }

    suspend fun remove(text: String) {
        context.dataStore.edit { prefs ->
            val old = prefs[KEY] ?: emptySet()
            prefs[KEY] = old - text
        }
    }
}
