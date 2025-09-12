package com.stepunlock.data.repository

import com.stepunlock.core.Result
import com.stepunlock.data.local.dao.HabitConfigDao
import com.stepunlock.data.mapper.HabitConfigMapper
import com.stepunlock.domain.model.HabitConfig
import com.stepunlock.domain.repository.HabitConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitConfigRepositoryImpl @Inject constructor(
    private val habitConfigDao: HabitConfigDao
) : HabitConfigRepository {
    
    override fun getAllHabitConfigs(): Flow<List<HabitConfig>> {
        return habitConfigDao.getAllHabitConfigs().map { entities ->
            entities.map { HabitConfigMapper.toDomain(it) }
        }
    }
    
    override fun getEnabledHabitConfigs(): Flow<List<HabitConfig>> {
        return habitConfigDao.getEnabledHabitConfigs().map { entities ->
            entities.map { HabitConfigMapper.toDomain(it) }
        }
    }
    
    override suspend fun getHabitConfig(habitId: String): Result<HabitConfig?> {
        return try {
            val entity = habitConfigDao.getHabitConfig(habitId)
            Result.Success(entity?.let { HabitConfigMapper.toDomain(it) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun getHabitConfigFlow(habitId: String): Flow<HabitConfig?> {
        return habitConfigDao.getHabitConfigFlow(habitId).map { entity ->
            entity?.let { HabitConfigMapper.toDomain(it) }
        }
    }
    
    override suspend fun insertHabitConfig(habitConfig: HabitConfig): Result<Unit> {
        return try {
            habitConfigDao.insertHabitConfig(HabitConfigMapper.toEntity(habitConfig))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun insertHabitConfigs(habitConfigs: List<HabitConfig>): Result<Unit> {
        return try {
            habitConfigDao.insertHabitConfigs(habitConfigs.map { HabitConfigMapper.toEntity(it) })
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updateHabitConfig(habitConfig: HabitConfig): Result<Unit> {
        return try {
            habitConfigDao.updateHabitConfig(HabitConfigMapper.toEntity(habitConfig))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updateHabitEnabled(habitId: String, enabled: Boolean): Result<Unit> {
        return try {
            habitConfigDao.updateHabitEnabled(habitId, enabled)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updateHabitSettings(habitId: String, earnRate: Int, goalPerDay: Int?): Result<Unit> {
        return try {
            habitConfigDao.updateHabitSettings(habitId, earnRate, goalPerDay)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
