package com.stepunlock.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepunlock.core.time.TimeUtils
import com.stepunlock.domain.model.HabitConfig
import com.stepunlock.domain.repository.AppRuleRepository
import com.stepunlock.domain.repository.CreditRepository
import com.stepunlock.domain.repository.HabitConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val creditRepository: CreditRepository,
    private val appRuleRepository: AppRuleRepository,
    private val habitConfigRepository: HabitConfigRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            // Load total credits
            creditRepository.getTotalCredits()
                .onSuccess { credits ->
                    _uiState.update { it.copy(totalCredits = credits) }
                }
                .onError { /* Handle error */ }
            
            // Load locked apps count
            appRuleRepository.getLockedApps()
                .collect { lockedApps ->
                    _uiState.update { it.copy(lockedAppsCount = lockedApps.size) }
                }
            
            // Load habits and their progress
            habitConfigRepository.getEnabledHabitConfigs()
                .combine(
                    creditRepository.getAllTransactions()
                ) { habits, transactions ->
                    val startOfDay = TimeUtils.startOfTodayMillis()
                    val endOfDay = TimeUtils.endOfDayMillis(startOfDay)
                    
                    habits.map { habit ->
                        val habitTransactions = transactions.filter { 
                            it.habitId == habit.id && 
                            it.timestamp >= startOfDay && 
                            it.timestamp <= endOfDay 
                        }
                        val earnedCredits = habitTransactions.sumOf { it.delta }
                        
                        HabitProgress(
                            name = habit.name,
                            icon = getHabitIcon(habit.id),
                            color = getHabitColor(habit.id),
                            progress = earnedCredits / habit.earnRate,
                            goal = habit.goalPerDay ?: 1
                        )
                    }
                }
                .collect { habits ->
                    _uiState.update { it.copy(habits = habits) }
                }
        }
    }
    
    private fun getHabitIcon(habitId: String): androidx.compose.ui.graphics.vector.ImageVector {
        return when (habitId) {
            "steps" -> androidx.compose.material.icons.Icons.Default.DirectionsWalk
            "pomodoro" -> androidx.compose.material.icons.Icons.Default.Timer
            "water" -> androidx.compose.material.icons.Icons.Default.LocalDrink
            "journal" -> androidx.compose.material.icons.Icons.Default.Edit
            else -> androidx.compose.material.icons.Icons.Default.Star
        }
    }
    
    private fun getHabitColor(habitId: String): androidx.compose.ui.graphics.Color {
        return when (habitId) {
            "steps" -> androidx.compose.ui.graphics.Color(0xFFFF9800)
            "pomodoro" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
            "water" -> androidx.compose.ui.graphics.Color(0xFF2196F3)
            "journal" -> androidx.compose.ui.graphics.Color(0xFF9C27B0)
            else -> androidx.compose.ui.graphics.Color(0xFF757575)
        }
    }
}

data class HomeUiState(
    val totalCredits: Int = 0,
    val lockedAppsCount: Int = 0,
    val habits: List<HabitProgress> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
