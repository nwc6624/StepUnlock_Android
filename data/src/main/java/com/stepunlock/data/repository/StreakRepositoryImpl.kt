package com.stepunlock.data.repository

import com.stepunlock.core.Result
import com.stepunlock.data.local.dao.StreakDao
import com.stepunlock.data.mapper.StreakMapper
import com.stepunlock.domain.model.Streak
import com.stepunlock.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakRepositoryImpl @Inject constructor(
    private val streakDao: StreakDao
) : StreakRepository {
    
    override fun getAllStreaks(): Flow<List<Streak>> {
        return streakDao.getAllStreaks().map { entities ->
            entities.map { StreakMapper.toDomain(it) }
        }
    }
    
    override suspend fun getStreak(habitId: String): Result<Streak?> {
        return try {
            val entity = streakDao.getStreak(habitId)
            Result.Success(entity?.let { StreakMapper.toDomain(it) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun getStreakFlow(habitId: String): Flow<Streak?> {
        return streakDao.getStreakFlow(habitId).map { entity ->
            entity?.let { StreakMapper.toDomain(it) }
        }
    }
    
    override suspend fun insertStreak(streak: Streak): Result<Unit> {
        return try {
            streakDao.insertStreak(StreakMapper.toEntity(streak))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updateStreak(streak: Streak): Result<Unit> {
        return try {
            streakDao.updateStreak(StreakMapper.toEntity(streak))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updateStreakData(
        habitId: String,
        currentStreak: Int,
        longestStreak: Int,
        lastEarnedDay: Long
    ): Result<Unit> {
        return try {
            streakDao.updateStreakData(habitId, currentStreak, longestStreak, lastEarnedDay)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
