package com.example.exem

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.themeDataStore by preferencesDataStore(name = "theme_store")

class ThemeStore(private val context: Context) {

    private val KEY = stringPreferencesKey("theme_mode") // system | light | dark

    suspend fun getMode(): String {
        val prefs = context.themeDataStore.data.first()
        return prefs[KEY] ?: "system"
    }

    suspend fun setMode(mode: String) {
        context.themeDataStore.edit { prefs ->
            prefs[KEY] = mode
        }
    }
}
