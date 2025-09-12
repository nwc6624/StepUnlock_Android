package com.stepunlock.domain.repository

import com.stepunlock.core.Result
import com.stepunlock.domain.model.CreditTransaction
import kotlinx.coroutines.flow.Flow

interface CreditRepository {
    fun getAllTransactions(): Flow<List<CreditTransaction>>
    fun getTransactionsInRange(startTime: Long, endTime: Long): Flow<List<CreditTransaction>>
    fun getTransactionsByReason(reason: String): Flow<List<CreditTransaction>>
    fun getTransactionsByHabit(habitId: String): Flow<List<CreditTransaction>>
    suspend fun getTotalCredits(): Result<Int>
    suspend fun getCreditsInRange(startTime: Long, endTime: Long): Result<Int>
    suspend fun getCreditsByHabitInRange(habitId: String, startTime: Long, endTime: Long): Result<Int>
    suspend fun getHabitCompletionCount(habitId: String, startTime: Long, endTime: Long): Result<Int>
    suspend fun insertTransaction(transaction: CreditTransaction): Result<Long>
    suspend fun insertTransactions(transactions: List<CreditTransaction>): Result<Unit>
}
