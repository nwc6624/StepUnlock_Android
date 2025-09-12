package com.stepunlock.domain.usecase

import com.stepunlock.core.Result
import com.stepunlock.core.time.TimeUtils
import com.stepunlock.domain.model.CreditTransaction
import com.stepunlock.domain.repository.CreditRepository
import com.stepunlock.domain.repository.StreakRepository
import javax.inject.Inject

class EarnCreditsUseCase @Inject constructor(
    private val creditRepository: CreditRepository,
    private val streakRepository: StreakRepository
) {
    
    suspend operator fun invoke(
        habitId: String,
        credits: Int,
        reason: String,
        metadata: String? = null
    ): Result<Unit> {
        return try {
            // Record the credit transaction
            val transaction = CreditTransaction(
                delta = credits,
                reason = reason,
                habitId = habitId,
                metadata = metadata
            )
            
            val insertResult = creditRepository.insertTransaction(transaction)
            if (insertResult.isSuccess) {
                // Update streak if this is a daily goal completion
                updateStreakIfNeeded(habitId)
                Result.Success(Unit)
            } else {
                Result.Error(insertResult.exceptionOrNull() ?: Exception("Failed to insert transaction"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    private suspend fun updateStreakIfNeeded(habitId: String) {
        try {
            val today = TimeUtils.startOfTodayMillis()
            val endOfDay = TimeUtils.endOfDayMillis(today)
            
            // Check if this habit has a daily goal and if it was completed today
            val completionCount = creditRepository.getHabitCompletionCount(habitId, today, endOfDay)
            if (completionCount.isSuccess && completionCount.getOrNull() ?: 0 > 0) {
                // Update streak for this habit
                val streakResult = streakRepository.getStreak(habitId)
                if (streakResult.isSuccess) {
                    val currentStreak = streakResult.getOrNull()
                    val newStreakDays = calculateNewStreak(currentStreak, today)
                    
                    if (newStreakDays != null) {
                        streakRepository.updateStreakData(
                            habitId = habitId,
                            currentStreak = newStreakDays.current,
                            longestStreak = maxOf(newStreakDays.current, newStreakDays.longest),
                            lastEarnedDay = today
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Log error but don't fail the main operation
        }
    }
    
    private suspend fun calculateNewStreak(
        currentStreak: com.stepunlock.domain.model.Streak?,
        today: Long
    ): StreakUpdate? {
        val yesterday = today - TimeUtils.daysBetween(0L, 24 * 60 * 60 * 1000L)
        
        return when {
            currentStreak == null -> {
                // First time earning for this habit
                StreakUpdate(current = 1, longest = 1)
            }
            currentStreak.lastEarnedDay == today -> {
                // Already earned today, no change
                null
            }
            TimeUtils.isSameDay(currentStreak.lastEarnedDay, yesterday) -> {
                // Consecutive day, increment streak
                StreakUpdate(
                    current = currentStreak.currentStreakDays + 1,
                    longest = maxOf(currentStreak.currentStreakDays + 1, currentStreak.longestStreakDays)
                )
            }
            else -> {
                // Streak broken, reset to 1
                StreakUpdate(current = 1, longest = currentStreak.longestStreakDays)
            }
        }
    }
    
    private data class StreakUpdate(
        val current: Int,
        val longest: Int
    )
}
