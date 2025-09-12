package com.stepunlock.data.local.dao

import androidx.room.*
import com.stepunlock.data.local.entities.HabitConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitConfigDao {
    
    @Query("SELECT * FROM habit_configs ORDER BY name ASC")
    fun getAllHabitConfigs(): Flow<List<HabitConfigEntity>>
    
    @Query("SELECT * FROM habit_configs WHERE enabled = 1 ORDER BY name ASC")
    fun getEnabledHabitConfigs(): Flow<List<HabitConfigEntity>>
    
    @Query("SELECT * FROM habit_configs WHERE id = :habitId")
    suspend fun getHabitConfig(habitId: String): HabitConfigEntity?
    
    @Query("SELECT * FROM habit_configs WHERE id = :habitId")
    fun getHabitConfigFlow(habitId: String): Flow<HabitConfigEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitConfig(habitConfig: HabitConfigEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitConfigs(habitConfigs: List<HabitConfigEntity>)
    
    @Update
    suspend fun updateHabitConfig(habitConfig: HabitConfigEntity)
    
    @Query("UPDATE habit_configs SET enabled = :enabled, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun updateHabitEnabled(habitId: String, enabled: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE habit_configs SET earnRate = :earnRate, goalPerDay = :goalPerDay, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun updateHabitSettings(habitId: String, earnRate: Int, goalPerDay: Int?, timestamp: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteHabitConfig(habitConfig: HabitConfigEntity)
}
