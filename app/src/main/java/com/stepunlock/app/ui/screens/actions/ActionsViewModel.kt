package com.stepunlock.app.ui.screens.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color

class ActionsViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ActionsUiState())
    val uiState: StateFlow<ActionsUiState> = _uiState.asStateFlow()
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun loadTodayProgress() {
        viewModelScope.launch {
            try {
                // Simulate loading delay
                kotlinx.coroutines.delay(500)
                
                // Mock data
                val habitActions = createMockHabitActions()
                val creditBalance = 150
                val dailyEarned = 45
                val completedToday = 2
                val streakCount = 5
                
                _uiState.value = _uiState.value.copy(
                    habitActions = habitActions,
                    creditBalance = creditBalance,
                    dailyEarned = dailyEarned,
                    completedToday = completedToday,
                    streakCount = streakCount,
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
    
    private fun createMockHabitActions(): List<HabitAction> {
        return listOf(
            HabitAction(
                type = "steps",
                name = "Steps",
                description = "Walk to stay healthy",
                icon = Icons.Default.DirectionsWalk,
                color = Color(0xFF2196F3),
                currentValue = 7500,
                targetValue = 10000,
                unit = "steps",
                creditsPerCompletion = 10,
                progress = 0.75f,
                isCompleted = false,
                requiresInput = true
            ),
            HabitAction(
                type = "pomodoro",
                name = "Focus Session",
                description = "25-minute focused work",
                icon = Icons.Default.Timer,
                color = Color(0xFF9C27B0),
                currentValue = 2,
                targetValue = 4,
                unit = "sessions",
                creditsPerCompletion = 25,
                progress = 0.5f,
                isCompleted = false,
                requiresInput = false
            ),
            HabitAction(
                type = "water",
                name = "Hydration",
                description = "Stay hydrated",
                icon = Icons.Default.LocalDrink,
                color = Color(0xFF00BCD4),
                currentValue = 6,
                targetValue = 8,
                unit = "glasses",
                creditsPerCompletion = 5,
                progress = 0.75f,
                isCompleted = false,
                requiresInput = true
            ),
            HabitAction(
                type = "journaling",
                name = "Journaling",
                description = "Reflect on your day",
                icon = Icons.Default.EditNote,
                color = Color(0xFFFF9800),
                currentValue = 1,
                targetValue = 1,
                unit = "entries",
                creditsPerCompletion = 15,
                progress = 1.0f,
                isCompleted = true,
                requiresInput = false
            )
        )
    }
    
    
    fun completeHabit(habitType: String) {
        viewModelScope.launch {
            try {
                // Simulate habit completion
                _uiState.value = _uiState.value.copy(showSuccessAnimation = true)
                
                // Refresh data after a short delay
                kotlinx.coroutines.delay(1000)
                loadTodayProgress()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun updateHabitValue(habitType: String, value: Int) {
        viewModelScope.launch {
            try {
                // Simulate habit value update
                _uiState.value = _uiState.value.copy(showSuccessAnimation = true)
                
                // Refresh data after a short delay
                kotlinx.coroutines.delay(1000)
                loadTodayProgress()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun dismissSuccessAnimation() {
        _uiState.value = _uiState.value.copy(showSuccessAnimation = false)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}


data class ActionsUiState(
    val habitActions: List<HabitAction> = emptyList(),
    val creditBalance: Int = 0,
    val dailyEarned: Int = 0,
    val completedToday: Int = 0,
    val streakCount: Int = 0,
    val showSuccessAnimation: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
