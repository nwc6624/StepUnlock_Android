package com.stepunlock.app.data.repository

import com.stepunlock.app.data.dao.HabitProgressDao
import com.stepunlock.app.data.entity.HabitProgressEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitProgressDao: HabitProgressDao
) : HabitRepository {
    
    override fun getProgressForDate(date: String): Flow<List<HabitProgressEntity>> = 
        habitProgressDao.getProgressForDate(date)
    
    override suspend fun getHabitProgress(habitType: String, date: String): HabitProgressEntity? = 
        habitProgressDao.getHabitProgress(habitType, date)
    
    override fun getRecentHabitProgress(habitType: String, limit: Int): Flow<List<HabitProgressEntity>> = 
        habitProgressDao.getRecentHabitProgress(habitType, limit)
    
    override fun getProgressInRange(startDate: String, endDate: String): Flow<List<HabitProgressEntity>> = 
        habitProgressDao.getProgressInRange(startDate, endDate)
    
    override suspend fun getCompletionCount(habitType: String, startDate: String): Int = 
        habitProgressDao.getCompletionCount(habitType, startDate)
    
    override suspend fun getLastCompletion(habitType: String): HabitProgressEntity? = 
        habitProgressDao.getLastCompletion(habitType)
    
    override suspend fun insertProgress(progress: HabitProgressEntity) = 
        habitProgressDao.insertProgress(progress)
    
    override suspend fun insertProgressList(progressList: List<HabitProgressEntity>) = 
        habitProgressDao.insertProgressList(progressList)
    
    override suspend fun updateProgress(progress: HabitProgressEntity) = 
        habitProgressDao.updateProgress(progress)
    
    override suspend fun updateHabitProgress(habitType: String, date: String, value: Int, completed: Boolean) = 
        habitProgressDao.updateHabitProgress(habitType, date, value, completed)
    
    override suspend fun deleteProgress(progress: HabitProgressEntity) = 
        habitProgressDao.deleteProgress(progress)
    
    override suspend fun deleteAllProgress() = 
        habitProgressDao.deleteAllProgress()
}
