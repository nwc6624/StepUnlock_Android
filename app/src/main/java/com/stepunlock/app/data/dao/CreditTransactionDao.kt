package com.stepunlock.app.data.dao

import androidx.room.*
import com.stepunlock.app.data.model.CreditTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditTransactionDao {
    @Query("SELECT * FROM credit_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CreditTransaction>>

    @Query("SELECT * FROM credit_transactions WHERE type = :type ORDER BY timestamp DESC")
    fun getTransactionsByType(type: String): Flow<List<CreditTransaction>>

    @Query("SELECT SUM(amount) FROM credit_transactions WHERE type = 'EARNED'")
    suspend fun getTotalEarned(): Int

    @Query("SELECT SUM(amount) FROM credit_transactions WHERE type = 'SPENT'")
    suspend fun getTotalSpent(): Int

    @Query("SELECT SUM(CASE WHEN type = 'EARNED' THEN amount ELSE -amount END) FROM credit_transactions")
    suspend fun getCurrentBalance(): Int

    @Insert
    suspend fun insertTransaction(transaction: CreditTransaction)

    @Query("DELETE FROM credit_transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)
}
