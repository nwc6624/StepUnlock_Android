package com.stepunlock.domain.repository

import com.stepunlock.core.Result
import com.stepunlock.domain.model.Streak
import kotlinx.coroutines.flow.Flow

interface StreakRepository {
    fun getAllStreaks(): Flow<List<Streak>>
    suspend fun getStreak(habitId: String): Result<Streak?>
    fun getStreakFlow(habitId: String): Flow<Streak?>
    suspend fun insertStreak(streak: Streak): Result<Unit>
    suspend fun updateStreak(streak: Streak): Result<Unit>
    suspend fun updateStreakData(
        habitId: String,
        currentStreak: Int,
        longestStreak: Int,
        lastEarnedDay: Long
    ): Result<Unit>
}
