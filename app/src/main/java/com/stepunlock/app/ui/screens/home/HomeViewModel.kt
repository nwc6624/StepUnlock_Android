package com.stepunlock.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepunlock.app.data.entity.HabitProgressEntity
import com.stepunlock.app.data.repository.AppRepository
import com.stepunlock.app.data.repository.CreditRepository
import com.stepunlock.app.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val creditRepository: CreditRepository,
    private val habitRepository: HabitRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            val today = dateFormatter.format(Date())
            
            try {
                // Combine all data sources
                combine(
                    appRepository.getLockedAppsCount(),
                    creditRepository.getAllTransactions(),
                    habitRepository.getProgressForDate(today)
                ) { lockedCount, transactions, habitProgress ->
                    
                    val creditBalance = transactions.sumOf { it.amount }
                    val habits = getHabitProgressList(habitProgress)
                    val streakCount = calculateStreak(habitProgress)
                    
                    HomeUiState(
                        creditsBalance = creditBalance,
                        stepsToday = habits.find { it.type == HabitType.STEPS }?.current ?: 0,
                        stepsProgress = if (habits.find { it.type == HabitType.STEPS }?.target ?: 1 > 0) {
                            (habits.find { it.type == HabitType.STEPS }?.current ?: 0).toFloat() / 
                            (habits.find { it.type == HabitType.STEPS }?.target ?: 1).toFloat()
                        } else 0f,
                        pomodorosCompleted = habits.find { it.type == HabitType.POMODORO }?.current ?: 0,
                        waterGlasses = habits.find { it.type == HabitType.WATER }?.current ?: 0,
                        journalEntries = habits.find { it.type == HabitType.JOURNAL }?.current ?: 0,
                        lockedAppsCount = lockedCount,
                        streakCount = streakCount,
                        isLoading = false
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun getHabitProgressList(habitProgress: List<HabitProgressEntity>): List<HabitProgress> {
        val habitTypes = listOf("steps", "pomodoro", "water", "journaling")
        val defaultTargets = mapOf(
            "steps" to 10000,
            "pomodoro" to 4,
            "water" to 8,
            "journaling" to 1
        )
        
        return habitTypes.map { habitType ->
            val progress = habitProgress.find { it.habitType == habitType }
            val target = defaultTargets[habitType] ?: 1
            val current = progress?.currentValue ?: 0
            
            HabitProgress(
                name = habitType.replaceFirstChar { it.uppercase() },
                target = target,
                current = current,
                type = HabitType.valueOf(habitType.uppercase())
            )
        }
    }
    
    private fun calculateStreak(habitProgress: List<HabitProgressEntity>): Int {
        // Simple streak calculation - count consecutive days with completed habits
        // TODO: Implement more sophisticated streak calculation
        return habitProgress.count { it.isCompleted }
    }
    
    fun performQuickAction(habitType: HabitType) {
        viewModelScope.launch {
            try {
                val today = dateFormatter.format(Date())
                val creditsEarned = when (habitType) {
                    HabitType.STEPS -> 10
                    HabitType.POMODORO -> 25
                    HabitType.WATER -> 5
                    HabitType.JOURNAL -> 15
                }
                
                // Add credit transaction
                creditRepository.insertTransaction(
                    com.stepunlock.app.data.entity.CreditLedgerEntity(
                        amount = creditsEarned,
                        reason = "quick_action_${habitType.name.lowercase()}",
                        habitType = habitType.name.lowercase()
                    )
                )
                
                // Update habit progress
                val currentProgress = habitRepository.getHabitProgress(habitType.name.lowercase(), today)
                val newValue = (currentProgress?.currentValue ?: 0) + 1
                val isCompleted = newValue >= (currentProgress?.targetValue ?: 1)
                
                habitRepository.updateHabitProgress(
                    habitType = habitType.name.lowercase(),
                    date = today,
                    value = newValue,
                    completed = isCompleted
                )
                
                // Refresh data
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