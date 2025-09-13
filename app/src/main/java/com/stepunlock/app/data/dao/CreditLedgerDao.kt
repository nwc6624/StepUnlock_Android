package com.stepunlock.app.data.dao

import androidx.room.*
import com.stepunlock.app.data.entity.CreditLedgerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditLedgerDao {
    
    @Query("SELECT * FROM credit_ledger ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CreditLedgerEntity>>
    
    @Query("SELECT * FROM credit_ledger WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getTransactionsInRange(startTime: Long, endTime: Long): Flow<List<CreditLedgerEntity>>
    
    @Query("SELECT * FROM credit_ledger WHERE reason LIKE '%earn%' OR reason LIKE '%complete%' ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEarnings(limit: Int = 50): Flow<List<CreditLedgerEntity>>
    
    @Query("SELECT * FROM credit_ledger WHERE reason LIKE '%unlock%' OR reason LIKE '%spend%' ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSpendings(limit: Int = 50): Flow<List<CreditLedgerEntity>>
    
    @Query("SELECT SUM(amount) FROM credit_ledger")
    suspend fun getTotalCredits(): Int?
    
    @Query("SELECT SUM(amount) FROM credit_ledger WHERE timestamp >= :startTime")
    suspend fun getCreditsSince(startTime: Long): Int?
    
    @Query("SELECT SUM(amount) FROM credit_ledger WHERE DATE(timestamp/1000, 'unixepoch') = DATE(:date/1000, 'unixepoch')")
    suspend fun getDailyCredits(date: Long): Int?
    
    @Insert
    suspend fun insertTransaction(transaction: CreditLedgerEntity): Long
    
    @Insert
    suspend fun insertTransactions(transactions: List<CreditLedgerEntity>)
    
    @Delete
    suspend fun deleteTransaction(transaction: CreditLedgerEntity)
    
    @Query("DELETE FROM credit_ledger")
    suspend fun deleteAllTransactions()
}
