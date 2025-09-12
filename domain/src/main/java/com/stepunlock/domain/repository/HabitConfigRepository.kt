package com.stepunlock.domain.repository

import com.stepunlock.core.Result
import com.stepunlock.domain.model.HabitConfig
import kotlinx.coroutines.flow.Flow

interface HabitConfigRepository {
    fun getAllHabitConfigs(): Flow<List<HabitConfig>>
    fun getEnabledHabitConfigs(): Flow<List<HabitConfig>>
    suspend fun getHabitConfig(habitId: String): Result<HabitConfig?>
    fun getHabitConfigFlow(habitId: String): Flow<HabitConfig?>
    suspend fun insertHabitConfig(habitConfig: HabitConfig): Result<Unit>
    suspend fun insertHabitConfigs(habitConfigs: List<HabitConfig>): Result<Unit>
    suspend fun updateHabitConfig(habitConfig: HabitConfig): Result<Unit>
    suspend fun updateHabitEnabled(habitId: String, enabled: Boolean): Result<Unit>
    suspend fun updateHabitSettings(habitId: String, earnRate: Int, goalPerDay: Int?): Result<Unit>
}
