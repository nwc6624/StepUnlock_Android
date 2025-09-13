package com.stepunlock.app.data.repository

import com.stepunlock.app.data.entity.HabitProgressEntity
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getProgressForDate(date: String): Flow<List<HabitProgressEntity>>
    suspend fun getHabitProgress(habitType: String, date: String): HabitProgressEntity?
    fun getRecentHabitProgress(habitType: String, limit: Int = 30): Flow<List<HabitProgressEntity>>
    fun getProgressInRange(startDate: String, endDate: String): Flow<List<HabitProgressEntity>>
    suspend fun getCompletionCount(habitType: String, startDate: String): Int
    suspend fun getLastCompletion(habitType: String): HabitProgressEntity?
    suspend fun insertProgress(progress: HabitProgressEntity)
    suspend fun insertProgressList(progressList: List<HabitProgressEntity>)
    suspend fun updateProgress(progress: HabitProgressEntity)
    suspend fun updateHabitProgress(habitType: String, date: String, value: Int, completed: Boolean)
    suspend fun deleteProgress(progress: HabitProgressEntity)
    suspend fun deleteAllProgress()
}
