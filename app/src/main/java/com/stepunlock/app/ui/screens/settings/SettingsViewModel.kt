package com.stepunlock.app.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepunlock.app.data.manager.ThemeManager
import com.stepunlock.app.data.model.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private var themeManager: ThemeManager? = null
    
    fun initialize(context: Context) {
        themeManager = ThemeManager(context)
        
        viewModelScope.launch {
            themeManager?.let { manager ->
                combine(
                    manager.themePreference,
                    manager.isDarkMode
                ) { themePreference, isDarkMode ->
                    SettingsUiState(
                        themePreference = themePreference,
                        isDarkMode = isDarkMode,
                        isLoading = false
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            }
        }
    }
    
    fun setThemePreference(theme: ThemePreference) {
        viewModelScope.launch {
            themeManager?.setThemePreference(theme)
        }
    }
    
    fun toggleDarkMode() {
        viewModelScope.launch {
            val currentDarkMode = _uiState.value.isDarkMode
            themeManager?.setDarkMode(!currentDarkMode)
        }
    }
}

data class SettingsUiState(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = true
)
