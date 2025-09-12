package com.stepunlock.data.local.dao

import androidx.room.*
import com.stepunlock.data.local.entities.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    
    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>
    
    @Query("SELECT * FROM sessions WHERE isActive = 1")
    fun getActiveSessions(): Flow<List<SessionEntity>>
    
    @Query("SELECT * FROM sessions WHERE packageName = :packageName AND isActive = 1")
    suspend fun getActiveSessionForApp(packageName: String): SessionEntity?
    
    @Query("SELECT * FROM sessions WHERE packageName = :packageName ORDER BY startTime DESC")
    fun getSessionsForApp(packageName: String): Flow<List<SessionEntity>>
    
    @Query("SELECT * FROM sessions WHERE startTime >= :startTime AND startTime <= :endTime ORDER BY startTime DESC")
    fun getSessionsInRange(startTime: Long, endTime: Long): Flow<List<SessionEntity>>
    
    @Query("SELECT COUNT(*) FROM sessions WHERE packageName = :packageName AND startTime >= :startTime AND startTime <= :endTime")
    suspend fun getSessionCountForAppInRange(packageName: String, startTime: Long, endTime: Long): Int
    
    @Query("SELECT SUM(grantedMinutes) FROM sessions WHERE packageName = :packageName AND startTime >= :startTime AND startTime <= :endTime")
    suspend fun getTotalMinutesForAppInRange(packageName: String, startTime: Long, endTime: Long): Int?
    
    @Insert
    suspend fun insertSession(session: SessionEntity): Long
    
    @Update
    suspend fun updateSession(session: SessionEntity)
    
    @Query("UPDATE sessions SET isActive = 0, endTime = :endTime WHERE id = :sessionId")
    suspend fun endSession(sessionId: Long, endTime: Long = System.currentTimeMillis())
    
    @Query("UPDATE sessions SET isActive = 0 WHERE isActive = 1")
    suspend fun endAllActiveSessions()
    
    @Delete
    suspend fun deleteSession(session: SessionEntity)
    
    @Query("DELETE FROM sessions WHERE startTime < :cutoffTime")
    suspend fun deleteOldSessions(cutoffTime: Long)
}
