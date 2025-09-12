package com.stepunlock.data.local.dao

import androidx.room.*
import com.stepunlock.data.local.entities.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    
    @Query("SELECT * FROM streaks ORDER BY currentStreakDays DESC")
    fun getAllStreaks(): Flow<List<StreakEntity>>
    
    @Query("SELECT * FROM streaks WHERE habitId = :habitId")
    suspend fun getStreak(habitId: String): StreakEntity?
    
    @Query("SELECT * FROM streaks WHERE habitId = :habitId")
    fun getStreakFlow(habitId: String): Flow<StreakEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: StreakEntity)
    
    @Update
    suspend fun updateStreak(streak: StreakEntity)
    
    @Query("UPDATE streaks SET currentStreakDays = :currentStreak, longestStreakDays = :longestStreak, lastEarnedDay = :lastEarnedDay, updatedAt = :timestamp WHERE habitId = :habitId")
    suspend fun updateStreakData(
        habitId: String,
        currentStreak: Int,
        longestStreak: Int,
        lastEarnedDay: Long,
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Delete
    suspend fun deleteStreak(streak: StreakEntity)
}
