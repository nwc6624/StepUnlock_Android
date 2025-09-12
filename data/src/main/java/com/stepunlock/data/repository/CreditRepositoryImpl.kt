package com.stepunlock.data.repository

import com.stepunlock.core.Result
import com.stepunlock.data.local.dao.CreditLedgerDao
import com.stepunlock.data.mapper.CreditTransactionMapper
import com.stepunlock.domain.model.CreditTransaction
import com.stepunlock.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditRepositoryImpl @Inject constructor(
    private val creditLedgerDao: CreditLedgerDao
) : CreditRepository {
    
    override fun getAllTransactions(): Flow<List<CreditTransaction>> {
        return creditLedgerDao.getAllTransactions().map { entities ->
            entities.map { CreditTransactionMapper.toDomain(it) }
        }
    }
    
    override fun getTransactionsInRange(startTime: Long, endTime: Long): Flow<List<CreditTransaction>> {
        return creditLedgerDao.getTransactionsInRange(startTime, endTime).map { entities ->
            entities.map { CreditTransactionMapper.toDomain(it) }
        }
    }
    
    override fun getTransactionsByReason(reason: String): Flow<List<CreditTransaction>> {
        return creditLedgerDao.getTransactionsByReason(reason).map { entities ->
            entities.map { CreditTransactionMapper.toDomain(it) }
        }
    }
    
    override fun getTransactionsByHabit(habitId: String): Flow<List<CreditTransaction>> {
        return creditLedgerDao.getTransactionsByHabit(habitId).map { entities ->
            entities.map { CreditTransactionMapper.toDomain(it) }
        }
    }
    
    override suspend fun getTotalCredits(): Result<Int> {
        return try {
            val total = creditLedgerDao.getTotalCredits()
            Result.Success(total)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getCreditsInRange(startTime: Long, endTime: Long): Result<Int> {
        return try {
            val credits = creditLedgerDao.getCreditsInRange(startTime, endTime)
            Result.Success(credits)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getCreditsByHabitInRange(habitId: String, startTime: Long, endTime: Long): Result<Int> {
        return try {
            val credits = creditLedgerDao.getCreditsByHabitInRange(habitId, startTime, endTime)
            Result.Success(credits)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getHabitCompletionCount(habitId: String, startTime: Long, endTime: Long): Result<Int> {
        return try {
            val count = creditLedgerDao.getHabitCompletionCount(habitId, startTime, endTime)
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun insertTransaction(transaction: CreditTransaction): Result<Long> {
        return try {
            val id = creditLedgerDao.insertTransaction(CreditTransactionMapper.toEntity(transaction))
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun insertTransactions(transactions: List<CreditTransaction>): Result<Unit> {
        return try {
            creditLedgerDao.insertTransactions(transactions.map { CreditTransactionMapper.toEntity(it) })
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
