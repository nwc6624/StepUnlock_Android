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
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    private var stepTrackingRepository: StepTrackingRepository? = null
    private var stepUnlockRepository: StepUnlockRepository? = null

    fun initialize(context: Context) {
        android.util.Log.d("HomeViewModel", "Initializing HomeViewModel")
        stepTrackingRepository = StepTrackingRepository(context)
        stepUnlockRepository = StepUnlockRepository(context)
        
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
                
                // Get real credit balance from database
                val creditsBalance = stepUnlockRepository?.getCurrentBalance() ?: 100
                
                // Get real habit progress from database
                val today = dateFormatter.format(Date())
                val pomodoroProgress = stepUnlockRepository?.getHabitProgress("pomodoro", today)?.firstOrNull()
                val waterProgress = stepUnlockRepository?.getHabitProgress("water", today)?.firstOrNull()
                val journalProgress = stepUnlockRepository?.getHabitProgress("journaling", today)?.firstOrNull()
                
                _uiState.value = HomeUiState(
                    creditsBalance = creditsBalance,
                    stepsToday = currentSteps,
                    stepsProgress = stepsProgress,
                    pomodorosCompleted = pomodoroProgress?.currentValue ?: 0,
                    waterGlasses = waterProgress?.currentValue ?: 0,
                    journalEntries = journalProgress?.currentValue ?: 0,
                    lockedAppsCount = 3, // TODO: Get real locked apps count
                    streakCount = 5, // TODO: Calculate real streak
                    isLoading = false
                )
                
                android.util.Log.d("HomeViewModel", "Loaded data - Credits: $creditsBalance, Steps: $currentSteps")
                
                // Start observing step changes
                observeStepChanges()
                
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
    
    fun performQuickAction(habitType: HabitType) {
        viewModelScope.launch {
            try {
                android.util.Log.d("HomeViewModel", "Performing quick action: $habitType")
                
                val creditsToEarn = when (habitType) {
                    HabitType.STEPS -> 10
                    HabitType.POMODORO -> 25
                    HabitType.WATER -> 5
                    HabitType.JOURNAL -> 15
                }
                
                // Earn credits through the repository
                stepUnlockRepository?.earnCredits(creditsToEarn, "Completed ${habitType.name.lowercase()} activity")
                
                // Update habit progress
                val today = dateFormatter.format(Date())
                when (habitType) {
                    HabitType.STEPS -> {
                        // Steps are tracked automatically, just earn credits
                    }
                    HabitType.POMODORO -> {
                        stepUnlockRepository?.updateHabitProgress("pomodoro", today, 
                            _uiState.value.pomodorosCompleted + 1, 
                            (_uiState.value.pomodorosCompleted + 1) >= 4)
                    }
                    HabitType.WATER -> {
                        stepUnlockRepository?.updateHabitProgress("water", today, 
                            _uiState.value.waterGlasses + 1, 
                            (_uiState.value.waterGlasses + 1) >= 8)
                    }
                    HabitType.JOURNAL -> {
                        stepUnlockRepository?.updateHabitProgress("journaling", today, 
                            _uiState.value.journalEntries + 1, 
                            (_uiState.value.journalEntries + 1) >= 1)
                    }
                }
                
                // Update UI state immediately
                val currentBalance = stepUnlockRepository?.getCurrentBalance() ?: _uiState.value.creditsBalance
                _uiState.value = _uiState.value.copy(
                    creditsBalance = currentBalance + creditsToEarn
                )
                
                android.util.Log.d("HomeViewModel", "Earned $creditsToEarn credits for $habitType")
                
                // Refresh data to get updated values
                loadData()
                
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