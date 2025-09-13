package com.stepunlock.app.data.repository

import com.stepunlock.app.data.dao.CreditLedgerDao
import com.stepunlock.app.data.entity.CreditLedgerEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditRepositoryImpl @Inject constructor(
    private val creditLedgerDao: CreditLedgerDao
) : CreditRepository {
    
    override fun getAllTransactions(): Flow<List<CreditLedgerEntity>> = 
        creditLedgerDao.getAllTransactions()
    
    override fun getTransactionsInRange(startTime: Long, endTime: Long): Flow<List<CreditLedgerEntity>> = 
        creditLedgerDao.getTransactionsInRange(startTime, endTime)
    
    override fun getRecentEarnings(limit: Int): Flow<List<CreditLedgerEntity>> = 
        creditLedgerDao.getRecentEarnings(limit)
    
    override fun getRecentSpendings(limit: Int): Flow<List<CreditLedgerEntity>> = 
        creditLedgerDao.getRecentSpendings(limit)
    
    override suspend fun getTotalCredits(): Int = 
        creditLedgerDao.getTotalCredits() ?: 0
    
    override suspend fun getCreditsSince(startTime: Long): Int = 
        creditLedgerDao.getCreditsSince(startTime) ?: 0
    
    override suspend fun getDailyCredits(date: Long): Int = 
        creditLedgerDao.getDailyCredits(date) ?: 0
    
    override suspend fun insertTransaction(transaction: CreditLedgerEntity): Long = 
        creditLedgerDao.insertTransaction(transaction)
    
    override suspend fun insertTransactions(transactions: List<CreditLedgerEntity>) = 
        creditLedgerDao.insertTransactions(transactions)
    
    override suspend fun deleteTransaction(transaction: CreditLedgerEntity) = 
        creditLedgerDao.deleteTransaction(transaction)
    
    override suspend fun deleteAllTransactions() = 
        creditLedgerDao.deleteAllTransactions()
}
