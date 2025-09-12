package com.stepunlock.data.repository

import com.stepunlock.data.local.dao.CreditLedgerDao
import com.stepunlock.data.local.entities.CreditLedgerEntity
import com.stepunlock.domain.repository.CreditRepository
import com.stepunlock.domain.repository.CreditTransaction
import com.stepunlock.domain.repository.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditRepositoryImpl @Inject constructor(
    private val creditLedgerDao: CreditLedgerDao
) : CreditRepository {
    
    override fun getCreditsBalance(): Flow<Int> {
        return creditLedgerDao.getCurrentBalance().map { it ?: 0 }
    }
    
    override suspend fun addCredits(amount: Int, reason: String) {
        val transaction = CreditLedgerEntity(
            amount = amount,
            reason = reason,
            type = TransactionType.EARNED.name
        )
        creditLedgerDao.insertTransaction(transaction)
    }
    
    override suspend fun spendCredits(amount: Int, reason: String): Boolean {
        val currentBalance = creditLedgerDao.getCurrentBalance()
        var hasEnoughCredits = false
        
        currentBalance.collect { balance ->
            if ((balance ?: 0) >= amount) {
                val transaction = CreditLedgerEntity(
                    amount = amount,
                    reason = reason,
                    type = TransactionType.SPENT.name
                )
                creditLedgerDao.insertTransaction(transaction)
                hasEnoughCredits = true
            }
        }
        
        return hasEnoughCredits
    }
    
    override fun getCreditHistory(): Flow<List<CreditTransaction>> {
        return creditLedgerDao.getAllTransactions().map { entities ->
            entities.map { entity ->
                CreditTransaction(
                    id = entity.id,
                    amount = entity.amount,
                    reason = entity.reason,
                    timestamp = entity.timestamp,
                    type = TransactionType.valueOf(entity.type)
                )
            }
        }
    }
}