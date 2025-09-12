package com.stepunlock.domain.repository

import com.stepunlock.domain.model.HabitProgress
import com.stepunlock.domain.model.HabitType
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getTodayHabitProgress(): Flow<HabitProgress>
    suspend fun logStepCount(steps: Int)
    suspend fun completePomodoro()
    suspend fun logWaterIntake()
    suspend fun logJournalEntry()
    suspend fun completeCustomHabit(habitId: String)
    suspend fun getHabitStreaks(): Flow<Map<HabitType, Int>>
}
