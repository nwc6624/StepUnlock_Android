package com.stepunlock.app.data.dao

import androidx.room.*
import com.stepunlock.app.data.model.HabitProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitProgressDao {
    @Query("SELECT * FROM habit_progress ORDER BY lastUpdated DESC")
    fun getAllProgress(): Flow<List<HabitProgress>>

    @Query("SELECT * FROM habit_progress WHERE date = :date")
    fun getProgressForDate(date: String): Flow<List<HabitProgress>>

    @Query("SELECT * FROM habit_progress WHERE habitType = :habitType AND date = :date")
    fun getHabitProgress(habitType: String, date: String): Flow<HabitProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: HabitProgress)

    @Update
    suspend fun updateProgress(progress: HabitProgress)

    @Query("UPDATE habit_progress SET currentValue = :value, isCompleted = :completed, lastUpdated = :timestamp WHERE habitType = :habitType AND date = :date")
    suspend fun updateHabitProgress(habitType: String, date: String, value: Int, completed: Boolean, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteProgress(progress: HabitProgress)
}