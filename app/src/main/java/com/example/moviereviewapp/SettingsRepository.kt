package com.example.moviereviewapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object SettingsRepository {
    private const val PREFS_NAME = "cook_mate_prefs"
    private const val KEY_FAVORITES_ENABLED = "favorites_enabled"
    private const val KEY_DARK_THEME = "dark_theme_enabled"

    private lateinit var prefs: SharedPreferences

    private val _favoritesEnabled = MutableLiveData<Boolean>(true)
    val favoritesEnabledLiveData: LiveData<Boolean> = _favoritesEnabled

    private val _darkThemeEnabled = MutableLiveData<Boolean>(false)
    val darkThemeEnabledLiveData: LiveData<Boolean> = _darkThemeEnabled

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val current = prefs.getBoolean(KEY_FAVORITES_ENABLED, true)
            _favoritesEnabled.value = current
            val darkCurrent = prefs.getBoolean(KEY_DARK_THEME, false)
            _darkThemeEnabled.value = darkCurrent
        }
    }

    fun isFavoritesEnabled(): Boolean {
        return if (::prefs.isInitialized) prefs.getBoolean(KEY_FAVORITES_ENABLED, true) else true
    }

    fun isDarkThemeEnabled(): Boolean {
        return if (::prefs.isInitialized) prefs.getBoolean(KEY_DARK_THEME, false) else false
    }

    fun setFavoritesEnabled(enabled: Boolean) {
        if (!::prefs.isInitialized) return
        prefs.edit().putBoolean(KEY_FAVORITES_ENABLED, enabled).apply()
        _favoritesEnabled.value = enabled
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        if (!::prefs.isInitialized) return
        prefs.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
        _darkThemeEnabled.value = enabled
        Log.d("SettingsRepository", "setDarkThemeEnabled: $enabled")
    }
}
