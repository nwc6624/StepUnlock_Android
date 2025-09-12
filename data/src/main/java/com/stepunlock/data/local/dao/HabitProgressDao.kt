package com.stepunlock.data.local.dao

import androidx.room.*
import com.stepunlock.data.local.entities.HabitProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitProgressDao {
    @Query("SELECT * FROM habit_progress WHERE date = :date")
    suspend fun getHabitProgressForDate(date: String): HabitProgressEntity?
    
    @Query("SELECT * FROM habit_progress WHERE date = :date")
    fun getHabitProgressForDateFlow(date: String): Flow<HabitProgressEntity?>
    
    @Query("SELECT * FROM habit_progress WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getHabitProgressForDateRange(startDate: String, endDate: String): Flow<List<HabitProgressEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitProgress(progress: HabitProgressEntity)
    
    @Update
    suspend fun updateHabitProgress(progress: HabitProgressEntity)
    
    @Query("UPDATE habit_progress SET stepsCount = stepsCount + :steps WHERE date = :date")
    suspend fun addSteps(date: String, steps: Int)
    
    @Query("UPDATE habit_progress SET pomodorosCompleted = pomodorosCompleted + 1 WHERE date = :date")
    suspend fun addPomodoro(date: String)
    
    @Query("UPDATE habit_progress SET waterGlasses = waterGlasses + 1 WHERE date = :date")
    suspend fun addWaterGlass(date: String)
    
    @Query("UPDATE habit_progress SET journalEntries = journalEntries + 1 WHERE date = :date")
    suspend fun addJournalEntry(date: String)
    
    @Query("UPDATE habit_progress SET customHabitsCompleted = customHabitsCompleted + 1 WHERE date = :date")
    suspend fun addCustomHabit(date: String)
    
    @Query("SELECT * FROM habit_progress ORDER BY date DESC LIMIT 30")
    fun getRecentHabitProgress(): Flow<List<HabitProgressEntity>>
}
