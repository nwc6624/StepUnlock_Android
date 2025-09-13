package com.stepunlock.app.data.repository

import com.stepunlock.app.data.entity.CreditLedgerEntity
import kotlinx.coroutines.flow.Flow

interface CreditRepository {
    fun getAllTransactions(): Flow<List<CreditLedgerEntity>>
    fun getTransactionsInRange(startTime: Long, endTime: Long): Flow<List<CreditLedgerEntity>>
    fun getRecentEarnings(limit: Int = 50): Flow<List<CreditLedgerEntity>>
    fun getRecentSpendings(limit: Int = 50): Flow<List<CreditLedgerEntity>>
    suspend fun getTotalCredits(): Int
    suspend fun getCreditsSince(startTime: Long): Int
    suspend fun getDailyCredits(date: Long): Int
    suspend fun insertTransaction(transaction: CreditLedgerEntity): Long
    suspend fun insertTransactions(transactions: List<CreditLedgerEntity>)
    suspend fun deleteTransaction(transaction: CreditLedgerEntity)
    suspend fun deleteAllTransactions()
}
