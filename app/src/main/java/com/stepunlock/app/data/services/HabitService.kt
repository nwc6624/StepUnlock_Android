package com.stepunlock.app.data.services

import com.stepunlock.app.data.events.EarnEvent
import com.stepunlock.app.data.models.Habit
import com.stepunlock.app.data.models.HabitProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class HabitService {
    
    private val _habits = MutableStateFlow<List<Habit>>(getDefaultHabits())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()
    
    private val _progress = MutableStateFlow<Map<String, HabitProgress>>(emptyMap())
    val progress: StateFlow<Map<String, HabitProgress>> = _progress.asStateFlow()
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun getDefaultHabits(): List<Habit> {
        return listOf(
            Habit(
                id = "steps",
                name = "Steps",
                unit = "steps",
                goalPerDay = 10000,
                creditValue = 10,
                cooldownMinutes = 0,
                dailyCap = 10
            ),
            Habit(
                id = "focus_session",
                name = "Focus Session",
                unit = "sessions",
                goalPerDay = 4,
                creditValue = 25,
                cooldownMinutes = 10,
                dailyCap = 4
            ),
            Habit(
                id = "hydration",
                name = "Hydration",
                unit = "glasses",
                goalPerDay = 8,
                creditValue = 5,
                cooldownMinutes = 15,
                dailyCap = 8
            ),
            Habit(
                id = "journaling",
                name = "Journaling",
                unit = "entries",
                goalPerDay = 1,
                creditValue = 15,
                cooldownMinutes = 0,
                dailyCap = 1
            )
        )
    }
    
    fun getHabitById(habitId: String): Habit? {
        return _habits.value.find { it.id == habitId }
    }
    
    fun getTodayProgress(habitId: String): HabitProgress {
        val today = dateFormatter.format(Date())
        val key = "${habitId}_$today"
        return _progress.value[key] ?: HabitProgress(habitId = habitId, date = today)
    }
    
    fun getAllTodayProgress(): Map<String, HabitProgress> {
        val today = dateFormatter.format(Date())
        return _progress.value.filter { it.value.date == today }
    }
    
    fun canEarnCredits(habitId: String): Result<Boolean> {
        return try {
            val habit = getHabitById(habitId) ?: return Result.failure(Exception("Habit not found"))
            val progress = getTodayProgress(habitId)
            
            // Check daily cap
            if (progress.valueSoFar >= habit.dailyCap) {
                return Result.failure(Exception("Daily limit reached for ${habit.name}"))
            }
            
            // Check cooldown
            if (habit.cooldownMinutes > 0) {
                val timeSinceLastEarn = System.currentTimeMillis() - progress.lastEarnedAt
                val cooldownMs = habit.cooldownMinutes * 60 * 1000L
                
                if (timeSinceLastEarn < cooldownMs) {
                    val remainingMinutes = ((cooldownMs - timeSinceLastEarn) / (60 * 1000)).toInt() + 1
                    return Result.failure(Exception("Please wait $remainingMinutes more minutes before earning ${habit.name} credits"))
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun updateProgress(habitId: String, additionalValue: Int = 1): Result<HabitProgress> {
        return try {
            val habit = getHabitById(habitId) ?: return Result.failure(Exception("Habit not found"))
            val today = dateFormatter.format(Date())
            val key = "${habitId}_$today"
            
            val currentProgress = _progress.value[key] ?: HabitProgress(habitId = habitId, date = today)
            val newValue = currentProgress.valueSoFar + additionalValue
            
            val updatedProgress = currentProgress.copy(
                valueSoFar = newValue,
                lastEarnedAt = System.currentTimeMillis(),
                isCompleted = newValue >= habit.goalPerDay
            )
            
            val newProgressMap = _progress.value.toMutableMap()
            newProgressMap[key] = updatedProgress
            _progress.value = newProgressMap
            
            android.util.Log.d("HabitService", "Updated progress for $habitId: $newValue/${habit.goalPerDay}")
            Result.success(updatedProgress)
            
        } catch (e: Exception) {
            android.util.Log.e("HabitService", "Error updating progress", e)
            Result.failure(e)
        }
    }
    
    fun getNextEligibleTime(habitId: String): Long {
        val habit = getHabitById(habitId) ?: return 0
        val progress = getTodayProgress(habitId)
        
        if (habit.cooldownMinutes <= 0) return 0
        
        return progress.lastEarnedAt + (habit.cooldownMinutes * 60 * 1000L)
    }
    
    fun getRemainingCooldownMinutes(habitId: String): Int {
        val nextEligible = getNextEligibleTime(habitId)
        if (nextEligible <= 0) return 0
        
        val remaining = nextEligible - System.currentTimeMillis()
        return if (remaining > 0) (remaining / (60 * 1000)).toInt() + 1 else 0
    }
    
    fun isHabitCompleted(habitId: String): Boolean {
        val progress = getTodayProgress(habitId)
        return progress.isCompleted
    }
    
    fun getHabitProgressPercentage(habitId: String): Float {
        val habit = getHabitById(habitId) ?: return 0f
        val progress = getTodayProgress(habitId)
        return (progress.valueSoFar.toFloat() / habit.goalPerDay).coerceAtMost(1f)
    }
}
