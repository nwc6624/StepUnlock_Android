package com.stepunlock.domain.repository

import com.stepunlock.core.Result
import com.stepunlock.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getAllSessions(): Flow<List<Session>>
    fun getActiveSessions(): Flow<List<Session>>
    suspend fun getActiveSessionForApp(packageName: String): Result<Session?>
    fun getSessionsForApp(packageName: String): Flow<List<Session>>
    fun getSessionsInRange(startTime: Long, endTime: Long): Flow<List<Session>>
    suspend fun insertSession(session: Session): Result<Long>
    suspend fun updateSession(session: Session): Result<Unit>
    suspend fun endSession(sessionId: Long, endTime: Long): Result<Unit>
    suspend fun endAllActiveSessions(): Result<Unit>
}
