package com.stepunlock.data.repository

import com.stepunlock.core.Result
import com.stepunlock.data.local.dao.SessionDao
import com.stepunlock.data.mapper.SessionMapper
import com.stepunlock.domain.model.Session
import com.stepunlock.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {
    
    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { SessionMapper.toDomain(it) }
        }
    }
    
    override fun getActiveSessions(): Flow<List<Session>> {
        return sessionDao.getActiveSessions().map { entities ->
            entities.map { SessionMapper.toDomain(it) }
        }
    }
    
    override suspend fun getActiveSessionForApp(packageName: String): Result<Session?> {
        return try {
            val entity = sessionDao.getActiveSessionForApp(packageName)
            Result.Success(entity?.let { SessionMapper.toDomain(it) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun getSessionsForApp(packageName: String): Flow<List<Session>> {
        return sessionDao.getSessionsForApp(packageName).map { entities ->
            entities.map { SessionMapper.toDomain(it) }
        }
    }
    
    override fun getSessionsInRange(startTime: Long, endTime: Long): Flow<List<Session>> {
        return sessionDao.getSessionsInRange(startTime, endTime).map { entities ->
            entities.map { SessionMapper.toDomain(it) }
        }
    }
    
    override suspend fun insertSession(session: Session): Result<Long> {
        return try {
            val id = sessionDao.insertSession(SessionMapper.toEntity(session))
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updateSession(session: Session): Result<Unit> {
        return try {
            sessionDao.updateSession(SessionMapper.toEntity(session))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun endSession(sessionId: Long, endTime: Long): Result<Unit> {
        return try {
            sessionDao.endSession(sessionId, endTime)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun endAllActiveSessions(): Result<Unit> {
        return try {
            sessionDao.endAllActiveSessions()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
