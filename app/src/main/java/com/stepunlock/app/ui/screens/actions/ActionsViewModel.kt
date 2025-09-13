package com.stepunlock.app.ui.screens.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepunlock.app.data.entity.CreditLedgerEntity
import com.stepunlock.app.data.entity.HabitProgressEntity
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
import androidx.compose.ui.graphics.Color

@HiltViewModel
class ActionsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val creditRepository: CreditRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ActionsUiState())
    val uiState: StateFlow<ActionsUiState> = _uiState.asStateFlow()
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun loadTodayProgress() {
        viewModelScope.launch {
            try {
                val today = dateFormatter.format(Date())
                
                // Load habit progress
                habitRepository.getProgressForDate(today).collect { habitProgress ->
                    // Load credit transactions
                    creditRepository.getAllTransactions().collect { transactions ->
                        // Load daily credits
                        val dailyCredits = creditRepository.getDailyCredits(System.currentTimeMillis())
                        
                        val creditBalance = transactions.sumOf { it.amount }
                        val habitActions = createHabitActions(habitProgress)
                        val completedToday = habitProgress.count { it.isCompleted }
                        val streakCount = calculateStreak(habitProgress)
                        
                        _uiState.value = _uiState.value.copy(
                            habitActions = habitActions,
                            creditBalance = creditBalance,
                            dailyEarned = dailyCredits,
                            completedToday = completedToday,
                            streakCount = streakCount,
                            isLoading = false
                        )
                    }
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun createHabitActions(habitProgress: List<HabitProgressEntity>): List<HabitAction> {
        val habitTypes = mapOf(
            "steps" to HabitActionInfo(
                name = "Steps",
                description = "Walk to stay healthy",
                icon = Icons.Default.DirectionsWalk,
                color = Color(0xFF2196F3),
                unit = "steps",
                creditsPerCompletion = 10,
                requiresInput = true
            ),
            "pomodoro" to HabitActionInfo(
                name = "Focus Session",
                description = "25-minute focused work",
                icon = Icons.Default.Timer,
                color = Color(0xFF9C27B0),
                unit = "sessions",
                creditsPerCompletion = 25,
                requiresInput = false
            ),
            "water" to HabitActionInfo(
                name = "Hydration",
                description = "Stay hydrated",
                icon = Icons.Default.LocalDrink,
                color = Color(0xFF00BCD4),
                unit = "glasses",
                creditsPerCompletion = 5,
                requiresInput = true
            ),
            "journaling" to HabitActionInfo(
                name = "Journaling",
                description = "Reflect on your day",
                icon = Icons.Default.EditNote,
                color = Color(0xFFFF9800),
                unit = "entries",
                creditsPerCompletion = 15,
                requiresInput = false
            )
        )
        
        return habitTypes.map { (habitType, info) ->
            val progress = habitProgress.find { it.habitType == habitType }
            val currentValue = progress?.currentValue ?: 0
            val targetValue = progress?.targetValue ?: getDefaultTarget(habitType)
            val isCompleted = progress?.isCompleted ?: false
            
            HabitAction(
                type = habitType,
                name = info.name,
                description = info.description,
                icon = info.icon,
                color = info.color,
                currentValue = currentValue,
                targetValue = targetValue,
                unit = info.unit,
                creditsPerCompletion = info.creditsPerCompletion,
                progress = if (targetValue > 0) currentValue.toFloat() / targetValue.toFloat() else 0f,
                isCompleted = isCompleted,
                requiresInput = info.requiresInput
            )
        }
    }
    
    private fun getDefaultTarget(habitType: String): Int {
        return when (habitType) {
            "steps" -> 10000
            "pomodoro" -> 4
            "water" -> 8
            "journaling" -> 1
            else -> 1
        }
    }
    
    private fun calculateStreak(habitProgress: List<HabitProgressEntity>): Int {
        // Simple streak calculation - count consecutive days with completed habits
        // TODO: Implement more sophisticated streak calculation
        return habitProgress.count { it.isCompleted }
    }
    
    fun completeHabit(habitType: String) {
        viewModelScope.launch {
            try {
                val today = dateFormatter.format(Date())
                val habitAction = _uiState.value.habitActions.find { it.type == habitType }
                
                if (habitAction != null && !habitAction.isCompleted) {
                    // Update habit progress
                    val newValue = habitAction.currentValue + 1
                    val isCompleted = newValue >= habitAction.targetValue
                    
                    habitRepository.updateHabitProgress(
                        habitType = habitType,
                        date = today,
                        value = newValue,
                        completed = isCompleted
                    )
                    
                    // Award credits
                    creditRepository.insertTransaction(
                        CreditLedgerEntity(
                            amount = habitAction.creditsPerCompletion,
                            reason = "habit_completion_${habitType}",
                            habitType = habitType
                        )
                    )
                    
                    // Show success animation
                    _uiState.value = _uiState.value.copy(showSuccessAnimation = true)
                    
                    // Refresh data
                    loadTodayProgress()
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun updateHabitValue(habitType: String, value: Int) {
        viewModelScope.launch {
            try {
                val today = dateFormatter.format(Date())
                val habitAction = _uiState.value.habitActions.find { it.type == habitType }
                
                if (habitAction != null && value > 0) {
                    // Update habit progress
                    val newValue = habitAction.currentValue + value
                    val isCompleted = newValue >= habitAction.targetValue
                    
                    habitRepository.updateHabitProgress(
                        habitType = habitType,
                        date = today,
                        value = newValue,
                        completed = isCompleted
                    )
                    
                    // Award credits based on value added
                    val creditsEarned = (value * habitAction.creditsPerCompletion) / habitAction.targetValue
                    if (creditsEarned > 0) {
                        creditRepository.insertTransaction(
                            CreditLedgerEntity(
                                amount = creditsEarned,
                                reason = "habit_progress_${habitType}",
                                habitType = habitType
                            )
                        )
                    }
                    
                    // Show success animation
                    _uiState.value = _uiState.value.copy(showSuccessAnimation = true)
                    
                    // Refresh data
                    loadTodayProgress()
                }
                
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

data class HabitActionInfo(
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val unit: String,
    val creditsPerCompletion: Int,
    val requiresInput: Boolean
)

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
