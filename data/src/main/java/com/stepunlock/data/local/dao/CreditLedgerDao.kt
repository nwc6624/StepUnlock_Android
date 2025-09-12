package com.stepunlock.data.local.dao

import androidx.room.*
import com.stepunlock.data.local.entities.CreditLedgerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditLedgerDao {
    
    @Query("SELECT * FROM credit_ledger ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CreditLedgerEntity>>
    
    @Query("SELECT * FROM credit_ledger WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getTransactionsInRange(startTime: Long, endTime: Long): Flow<List<CreditLedgerEntity>>
    
    @Query("SELECT * FROM credit_ledger WHERE reason = :reason ORDER BY timestamp DESC")
    fun getTransactionsByReason(reason: String): Flow<List<CreditLedgerEntity>>
    
    @Query("SELECT * FROM credit_ledger WHERE habitId = :habitId ORDER BY timestamp DESC")
    fun getTransactionsByHabit(habitId: String): Flow<List<CreditLedgerEntity>>
    
    @Query("SELECT COALESCE(SUM(delta), 0) FROM credit_ledger")
    suspend fun getTotalCredits(): Int
    
    @Query("SELECT COALESCE(SUM(delta), 0) FROM credit_ledger WHERE timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getCreditsInRange(startTime: Long, endTime: Long): Int
    
    @Query("SELECT COALESCE(SUM(delta), 0) FROM credit_ledger WHERE habitId = :habitId AND timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getCreditsByHabitInRange(habitId: String, startTime: Long, endTime: Long): Int
    
    @Query("SELECT COUNT(*) FROM credit_ledger WHERE habitId = :habitId AND timestamp >= :startTime AND timestamp <= :endTime AND delta > 0")
    suspend fun getHabitCompletionCount(habitId: String, startTime: Long, endTime: Long): Int
    
    @Insert
    suspend fun insertTransaction(transaction: CreditLedgerEntity): Long
    
    @Insert
    suspend fun insertTransactions(transactions: List<CreditLedgerEntity>)
    
    @Delete
    suspend fun deleteTransaction(transaction: CreditLedgerEntity)
    
    @Query("DELETE FROM credit_ledger WHERE timestamp < :cutoffTime")
    suspend fun deleteOldTransactions(cutoffTime: Long)
}
