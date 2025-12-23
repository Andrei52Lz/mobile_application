package com.example.exem

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.runBlocking

class ExemApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val mode = runBlocking {
            ThemeStore(applicationContext).getMode()
        }

        val night = when (mode) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(night)
    }
}
