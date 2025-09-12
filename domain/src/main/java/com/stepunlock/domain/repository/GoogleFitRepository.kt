package com.stepunlock.domain.repository

import com.stepunlock.core.Result
import kotlinx.coroutines.flow.Flow

interface GoogleFitRepository {
    suspend fun getTodaySteps(): Result<Int>
    suspend fun isConnected(): Result<Boolean>
    suspend fun connect(): Result<Unit>
    suspend fun disconnect(): Result<Unit>
    fun getStepsFlow(): Flow<Int>
}
