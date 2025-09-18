package com.stepunlock.app.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.stepunlock.app.data.repository.StepTrackingRepository
import com.stepunlock.app.data.repository.StepUnlockRepository
import com.stepunlock.app.data.services.AppService
import com.stepunlock.app.data.events.EarnEvent
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    private var stepTrackingRepository: StepTrackingRepository? = null
    private var stepUnlockRepository: StepUnlockRepository? = null
    private var appService: AppService? = null

    fun initialize(context: Context) {
        android.util.Log.d("HomeViewModel", "Initializing HomeViewModel")
        stepTrackingRepository = StepTrackingRepository(context)
        stepUnlockRepository = StepUnlockRepository(context)
        appService = AppService()
        
        viewModelScope.launch {
            android.util.Log.d("HomeViewModel", "Starting step tracking initialization")
            stepTrackingRepository?.initialize()
            stepTrackingRepository?.startTracking()
            
            // Initialize default data in repository
            stepUnlockRepository?.initializeDefaultData()
            
            android.util.Log.d("HomeViewModel", "Step tracking started, loading data")
            loadData()
        }
    }
    
    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Get real step data
                val currentSteps = stepTrackingRepository?.currentSteps?.value ?: 0
                val stepsProgress = (currentSteps / 10000f).coerceAtMost(1f)
                
                // Get data from AppService
                val creditsBalance = appService?.getCurrentBalance() ?: 100
                val lockedAppsCount = getLockedAppsCount()
                
                // Get habit progress from AppService
                val pomodoroProgress = appService?.getTodayProgress("focus_session")?.valueSoFar ?: 0
                val waterProgress = appService?.getTodayProgress("hydration")?.valueSoFar ?: 0
                val journalProgress = appService?.getTodayProgress("journaling")?.valueSoFar ?: 0
                
                _uiState.value = HomeUiState(
                    creditsBalance = creditsBalance,
                    stepsToday = currentSteps,
                    stepsProgress = stepsProgress,
                    pomodorosCompleted = pomodoroProgress,
                    waterGlasses = waterProgress,
                    journalEntries = journalProgress,
                    lockedAppsCount = lockedAppsCount,
                    streakCount = calculateStreak(), // Calculate real streak
                    isLoading = false
                )
                
                android.util.Log.d("HomeViewModel", "Loaded data - Credits: $creditsBalance, Steps: $currentSteps")
                
                // Start observing step changes
                observeStepChanges()
                
                // Start observing AppService changes
                observeAppServiceChanges()
                
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Error loading data", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun observeStepChanges() {
        viewModelScope.launch {
            stepTrackingRepository?.currentSteps?.collect { steps ->
                _uiState.value = _uiState.value.copy(
                    stepsToday = steps,
                    stepsProgress = (steps / 10000f).coerceAtMost(1f)
                )
            }
        }
    }
    
    private fun observeAppServiceChanges() {
        viewModelScope.launch {
            appService?.appState?.collect { appState ->
                val currentSteps = stepTrackingRepository?.currentSteps?.value ?: 0
                val stepsProgress = (currentSteps / 10000f).coerceAtMost(1f)
                
                val pomodoroProgress = appService?.getTodayProgress("focus_session")?.valueSoFar ?: 0
                val waterProgress = appService?.getTodayProgress("hydration")?.valueSoFar ?: 0
                val journalProgress = appService?.getTodayProgress("journaling")?.valueSoFar ?: 0
                
                _uiState.value = _uiState.value.copy(
                    creditsBalance = appState.wallet.balance,
                    stepsToday = currentSteps,
                    stepsProgress = stepsProgress,
                    pomodorosCompleted = pomodoroProgress,
                    waterGlasses = waterProgress,
                    journalEntries = journalProgress,
                    isLoading = appState.isLoading,
                    error = appState.error
                )
            }
        }
    }
    
    private fun refreshUIState() {
        viewModelScope.launch {
            val currentSteps = stepTrackingRepository?.currentSteps?.value ?: 0
            val stepsProgress = (currentSteps / 10000f).coerceAtMost(1f)
            
            val creditsBalance = appService?.getCurrentBalance() ?: _uiState.value.creditsBalance
            val pomodoroProgress = appService?.getTodayProgress("focus_session")?.valueSoFar ?: 0
            val waterProgress = appService?.getTodayProgress("hydration")?.valueSoFar ?: 0
            val journalProgress = appService?.getTodayProgress("journaling")?.valueSoFar ?: 0
            
            _uiState.value = _uiState.value.copy(
                creditsBalance = creditsBalance,
                stepsToday = currentSteps,
                stepsProgress = stepsProgress,
                pomodorosCompleted = pomodoroProgress,
                waterGlasses = waterProgress,
                journalEntries = journalProgress
            )
        }
    }
    
    fun performQuickAction(habitType: HabitType) {
        viewModelScope.launch {
            try {
                android.util.Log.d("HomeViewModel", "Performing quick action: $habitType")
                
                val eventType = when (habitType) {
                    HabitType.STEPS -> "steps"
                    HabitType.POMODORO -> "focus_session"
                    HabitType.WATER -> "hydration"
                    HabitType.JOURNAL -> "journaling"
                }
                
                val earnEvent = EarnEvent(
                    type = eventType,
                    units = 1,
                    meta = mapOf("source" to "quick_action")
                )
                
                val result = appService?.earnCredits(earnEvent)
                
                if (result?.isSuccess == true) {
                    val transaction = result.getOrNull()
                    android.util.Log.d("HomeViewModel", "Successfully earned credits: ${transaction?.amount}")
                    
                    // Update UI state with new data
                    refreshUIState()
                } else {
                    val error = result?.exceptionOrNull()?.message ?: "Unknown error"
                    android.util.Log.e("HomeViewModel", "Failed to earn credits: $error")
                    _uiState.value = _uiState.value.copy(error = error)
                }
                
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Error performing quick action", e)
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
    
    // Test method to verify credit system is working
    fun testCreditSystem() {
        viewModelScope.launch {
            try {
                android.util.Log.d("HomeViewModel", "Testing credit system")
                performQuickAction(HabitType.POMODORO)
                android.util.Log.d("HomeViewModel", "Test credit system completed")
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Error in test credit system", e)
            }
        }
    }
    
    private fun calculateStreak(): Int {
        // TODO: Implement real streak calculation based on consecutive days of habit completion
        // For now, return a mock value
        return 5
    }
    
    private suspend fun getLockedAppsCount(): Int {
        return try {
            // For now, return mock data until we implement proper Flow collection
            3
        } catch (e: Exception) {
            android.util.Log.e("HomeViewModel", "Error getting locked apps count", e)
            0
        }
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