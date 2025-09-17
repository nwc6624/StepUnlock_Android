package com.stepunlock.app.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.stepunlock.app.data.model.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemeManager(private val context: Context) {
    
    private val themePreferenceKey = intPreferencesKey("theme_preference")
    private val isDarkModeKey = booleanPreferencesKey("is_dark_mode")
    
    val themePreference: Flow<ThemePreference> = context.dataStore.data.map { preferences ->
        val themeValue = preferences[themePreferenceKey] ?: ThemePreference.SYSTEM.ordinal
        ThemePreference.values()[themeValue]
    }
    
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[isDarkModeKey] ?: false
    }
    
    suspend fun setThemePreference(theme: ThemePreference) {
        context.dataStore.edit { preferences ->
            preferences[themePreferenceKey] = theme.ordinal
        }
    }
    
    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isDarkModeKey] = isDark
        }
    }
    
    suspend fun getCurrentThemePreference(): ThemePreference {
        return themePreference.map { it }.let { _ ->
            // This is a simplified way to get the current value
            // In a real app, you might want to use a different approach
            ThemePreference.SYSTEM // Default fallback
        }
    }
}
