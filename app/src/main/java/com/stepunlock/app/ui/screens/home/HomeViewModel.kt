package com.stepunlock.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        // Simulate some initial data
        _uiState.value = HomeUiState(
            creditsBalance = 50,
            stepsToday = 2500,
            stepsProgress = 0.25f,
            pomodorosCompleted = 1,
            waterGlasses = 3,
            journalEntries = 0,
            lockedAppsCount = 3
        )
    }
    
    fun performQuickAction(habitType: HabitType) {
        viewModelScope.launch {
            // TODO: Implement quick action logic
            // This would trigger the appropriate habit completion
            when (habitType) {
                HabitType.STEPS -> {
                    // Simulate earning credits for steps
                    val currentCredits = _uiState.value.creditsBalance
                    _uiState.value = _uiState.value.copy(creditsBalance = currentCredits + 10)
                }
                HabitType.POMODORO -> {
                    // Simulate earning credits for pomodoro
                    val currentCredits = _uiState.value.creditsBalance
                    _uiState.value = _uiState.value.copy(creditsBalance = currentCredits + 25)
                }
                HabitType.WATER -> {
                    // Simulate earning credits for water
                    val currentCredits = _uiState.value.creditsBalance
                    _uiState.value = _uiState.value.copy(creditsBalance = currentCredits + 5)
                }
                HabitType.JOURNAL -> {
                    // Simulate earning credits for journal
                    val currentCredits = _uiState.value.creditsBalance
                    _uiState.value = _uiState.value.copy(creditsBalance = currentCredits + 15)
                }
            }
        }
    }
}

enum class HabitType {
    STEPS,
    POMODORO,
    WATER,
    JOURNAL
}

data class HomeUiState(
    val creditsBalance: Int = 0,
    val stepsToday: Int = 0,
    val stepsProgress: Float = 0f,
    val pomodorosCompleted: Int = 0,
    val waterGlasses: Int = 0,
    val journalEntries: Int = 0,
    val lockedAppsCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
