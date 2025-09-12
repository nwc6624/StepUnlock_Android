package com.stepunlock.domain.repository

import kotlinx.coroutines.flow.Flow

interface CreditRepository {
    fun getCreditsBalance(): Flow<Int>
    suspend fun addCredits(amount: Int, reason: String)
    suspend fun spendCredits(amount: Int, reason: String): Boolean
    suspend fun getCreditHistory(): Flow<List<CreditTransaction>>
}

data class CreditTransaction(
    val id: Long,
    val amount: Int,
    val reason: String,
    val timestamp: Long,
    val type: TransactionType
)

enum class TransactionType {
    EARNED,
    SPENT
}