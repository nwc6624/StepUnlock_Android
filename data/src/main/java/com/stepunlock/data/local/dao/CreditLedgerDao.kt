package com.stepunlock.data.local.dao

import androidx.room.*
import com.stepunlock.data.local.entities.CreditLedgerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditLedgerDao {
    @Query("SELECT * FROM credit_ledger ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CreditLedgerEntity>>
    
    @Query("SELECT SUM(CASE WHEN type = 'EARNED' THEN amount ELSE -amount END) FROM credit_ledger")
    fun getCurrentBalance(): Flow<Int?>
    
    @Query("SELECT * FROM credit_ledger ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 50): Flow<List<CreditLedgerEntity>>
    
    @Insert
    suspend fun insertTransaction(transaction: CreditLedgerEntity): Long
    
    @Query("SELECT * FROM credit_ledger WHERE timestamp >= :startDate AND timestamp <= :endDate ORDER BY timestamp DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<CreditLedgerEntity>>
    
    @Query("SELECT * FROM credit_ledger WHERE habitType = :habitType ORDER BY timestamp DESC")
    fun getTransactionsByHabitType(habitType: String): Flow<List<CreditLedgerEntity>>
    
    @Query("SELECT * FROM credit_ledger WHERE appPackageName = :packageName ORDER BY timestamp DESC")
    fun getTransactionsByApp(packageName: String): Flow<List<CreditLedgerEntity>>
}