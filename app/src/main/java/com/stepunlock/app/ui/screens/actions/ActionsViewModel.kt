package com.stepunlock.app.ui.screens.actions

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.stepunlock.app.data.repository.StepTrackingRepository
import com.stepunlock.app.data.repository.StepUnlockRepository
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color

class ActionsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ActionsUiState())
    val uiState: StateFlow<ActionsUiState> = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    private var stepTrackingRepository: StepTrackingRepository? = null
    private var stepUnlockRepository: StepUnlockRepository? = null
    
    fun initialize(context: Context) {
        stepTrackingRepository = StepTrackingRepository(context)
        stepUnlockRepository = StepUnlockRepository(context)
        
        viewModelScope.launch {
            stepTrackingRepository?.initialize()
            loadTodayProgress()
        }
    }
    
    fun loadTodayProgress() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Get real step data
                val currentSteps = stepTrackingRepository?.currentSteps?.value ?: 0
                
                val habitActions = createRealHabitActions(
                    steps = currentSteps,
                    pomodoros = 2,
                    water = 6,
                    journal = 1
                )
                
                val completedToday = habitActions.count { it.isCompleted }
                val dailyEarned = habitActions.filter { it.isCompleted }.sumOf { it.creditsPerCompletion }

                _uiState.value = _uiState.value.copy(
                    habitActions = habitActions,
                    creditBalance = 150,
                    dailyEarned = dailyEarned,
                    completedToday = completedToday,
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
    
    private fun createRealHabitActions(
        steps: Int,
        pomodoros: Int,
        water: Int,
        journal: Int
    ): List<HabitAction> {
        return listOf(
            HabitAction(
                type = "steps",
                name = "Steps",
                description = "Walk to stay healthy",
                icon = Icons.Default.DirectionsWalk,
                color = Color(0xFF2196F3),
                currentValue = steps,
                targetValue = 10000,
                unit = "steps",
                creditsPerCompletion = 10,
                progress = (steps / 10000f).coerceAtMost(1f),
                isCompleted = steps >= 10000,
                requiresInput = true
            ),
            HabitAction(
                type = "pomodoro",
                name = "Focus Session",
                description = "25-minute focused work",
                icon = Icons.Default.Timer,
                color = Color(0xFF9C27B0),
                currentValue = pomodoros,
                targetValue = 4,
                unit = "sessions",
                creditsPerCompletion = 25,
                progress = (pomodoros / 4f).coerceAtMost(1f),
                isCompleted = pomodoros >= 4,
                requiresInput = false
            ),
            HabitAction(
                type = "water",
                name = "Hydration",
                description = "Stay hydrated",
                icon = Icons.Default.LocalDrink,
                color = Color(0xFF00BCD4),
                currentValue = water,
                targetValue = 8,
                unit = "glasses",
                creditsPerCompletion = 5,
                progress = (water / 8f).coerceAtMost(1f),
                isCompleted = water >= 8,
                requiresInput = true
            ),
            HabitAction(
                type = "journaling",
                name = "Journaling",
                description = "Reflect on your day",
                icon = Icons.Default.EditNote,
                color = Color(0xFFFF9800),
                currentValue = journal,
                targetValue = 1,
                unit = "entries",
                creditsPerCompletion = 15,
                progress = (journal / 1f).coerceAtMost(1f),
                isCompleted = journal >= 1,
                requiresInput = false
            )
        )
    }
    
    private fun createMockHabitActions(): List<HabitAction> {
        return createRealHabitActions(7500, 2, 6, 1)
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
