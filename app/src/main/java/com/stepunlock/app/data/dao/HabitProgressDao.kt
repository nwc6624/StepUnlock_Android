package com.stepunlock.app.data.dao

import androidx.room.*
import com.stepunlock.app.data.entity.HabitProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitProgressDao {
    
    @Query("SELECT * FROM habit_progress WHERE date = :date")
    fun getProgressForDate(date: String): Flow<List<HabitProgressEntity>>
    
    @Query("SELECT * FROM habit_progress WHERE habitType = :habitType AND date = :date")
    suspend fun getHabitProgress(habitType: String, date: String): HabitProgressEntity?
    
    @Query("SELECT * FROM habit_progress WHERE habitType = :habitType ORDER BY date DESC LIMIT :limit")
    fun getRecentHabitProgress(habitType: String, limit: Int = 30): Flow<List<HabitProgressEntity>>
    
    @Query("SELECT * FROM habit_progress WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getProgressInRange(startDate: String, endDate: String): Flow<List<HabitProgressEntity>>
    
    @Query("SELECT COUNT(*) FROM habit_progress WHERE habitType = :habitType AND isCompleted = 1 AND date >= :startDate")
    suspend fun getCompletionCount(habitType: String, startDate: String): Int
    
    @Query("SELECT * FROM habit_progress WHERE habitType = :habitType AND isCompleted = 1 ORDER BY date DESC LIMIT 1")
    suspend fun getLastCompletion(habitType: String): HabitProgressEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: HabitProgressEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressList(progressList: List<HabitProgressEntity>)
    
    @Update
    suspend fun updateProgress(progress: HabitProgressEntity)
    
    @Query("UPDATE habit_progress SET currentValue = :value, isCompleted = :completed, lastUpdated = :timestamp WHERE habitType = :habitType AND date = :date")
    suspend fun updateHabitProgress(habitType: String, date: String, value: Int, completed: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteProgress(progress: HabitProgressEntity)
    
    @Query("DELETE FROM habit_progress")
    suspend fun deleteAllProgress()
}
