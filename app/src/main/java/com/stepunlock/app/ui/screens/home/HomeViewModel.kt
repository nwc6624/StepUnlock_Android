package com.stepunlock.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            try {
                // Simulate loading delay
                kotlinx.coroutines.delay(500)
                
                // Mock data
                _uiState.value = HomeUiState(
                    creditsBalance = 150,
                    stepsToday = 7500,
                    stepsProgress = 0.75f,
                    pomodorosCompleted = 2,
                    waterGlasses = 6,
                    journalEntries = 1,
                    lockedAppsCount = 3,
                    streakCount = 5,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun performQuickAction(habitType: HabitType) {
        viewModelScope.launch {
            try {
                // Simulate quick action
                _uiState.value = _uiState.value.copy(
                    creditsBalance = _uiState.value.creditsBalance + 10
                )
                
                // Refresh data after a short delay
                kotlinx.coroutines.delay(1000)
                loadData()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun refreshData() {
        loadData()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class HabitProgress(
    val name: String,
    val target: Int,
    val current: Int,
    val type: HabitType
)

enum class HabitType {
    STEPS, POMODORO, WATER, JOURNAL
}

data class HomeUiState(
    val creditsBalance: Int = 0,
    val stepsToday: Int = 0,
    val stepsProgress: Float = 0f,
    val pomodorosCompleted: Int = 0,
    val waterGlasses: Int = 0,
    val journalEntries: Int = 0,
    val lockedAppsCount: Int = 0,
    val streakCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)