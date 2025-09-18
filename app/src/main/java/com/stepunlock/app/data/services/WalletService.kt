package com.stepunlock.app.data.services

import com.stepunlock.app.data.events.EarnEvent
import com.stepunlock.app.data.events.SpendEvent
import com.stepunlock.app.data.models.Transaction
import com.stepunlock.app.data.models.TransactionType
import com.stepunlock.app.data.models.Wallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class WalletService {
    
    private val _wallet = MutableStateFlow(Wallet(balance = 100)) // Start with 100 credits
    val wallet: StateFlow<Wallet> = _wallet.asStateFlow()
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val processedKeys = mutableSetOf<String>()
    
    suspend fun earnCredits(event: EarnEvent): Result<Transaction> {
        return try {
            // Check for duplicate idempotency key
            if (processedKeys.contains(event.idempotencyKey)) {
                val existingTransaction = _transactions.value.find { it.idempotencyKey == event.idempotencyKey }
                return Result.success(existingTransaction ?: throw Exception("Duplicate transaction not found"))
            }
            
            val currentWallet = _wallet.value
            val creditAmount = event.amount ?: getDefaultCreditAmount(event.type)
            
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.EARN,
                amount = creditAmount,
                source = event.type,
                meta = event.meta,
                createdAt = event.occurredAt,
                idempotencyKey = event.idempotencyKey
            )
            
            val newWallet = currentWallet.copy(
                balance = currentWallet.balance + creditAmount,
                totalEarned = currentWallet.totalEarned + creditAmount,
                lastUpdated = System.currentTimeMillis()
            )
            
            _wallet.value = newWallet
            _transactions.value = _transactions.value + transaction
            processedKeys.add(event.idempotencyKey)
            
            android.util.Log.d("WalletService", "Earned $creditAmount credits from ${event.type}. New balance: ${newWallet.balance}")
            Result.success(transaction)
            
        } catch (e: Exception) {
            android.util.Log.e("WalletService", "Error earning credits", e)
            Result.failure(e)
        }
    }
    
    suspend fun spendCredits(event: SpendEvent): Result<Transaction> {
        return try {
            // Check for duplicate idempotency key
            if (processedKeys.contains(event.idempotencyKey)) {
                val existingTransaction = _transactions.value.find { it.idempotencyKey == event.idempotencyKey }
                return Result.success(existingTransaction ?: throw Exception("Duplicate transaction not found"))
            }
            
            val currentWallet = _wallet.value
            
            if (currentWallet.balance < event.cost) {
                return Result.failure(Exception("Insufficient balance. Need ${event.cost}, have ${currentWallet.balance}"))
            }
            
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.SPEND,
                amount = event.cost,
                source = event.rewardSlug,
                meta = event.meta,
                createdAt = event.occurredAt,
                idempotencyKey = event.idempotencyKey
            )
            
            val newWallet = currentWallet.copy(
                balance = currentWallet.balance - event.cost,
                totalSpent = currentWallet.totalSpent + event.cost,
                lastUpdated = System.currentTimeMillis()
            )
            
            _wallet.value = newWallet
            _transactions.value = _transactions.value + transaction
            processedKeys.add(event.idempotencyKey)
            
            android.util.Log.d("WalletService", "Spent ${event.cost} credits on ${event.rewardSlug}. New balance: ${newWallet.balance}")
            Result.success(transaction)
            
        } catch (e: Exception) {
            android.util.Log.e("WalletService", "Error spending credits", e)
            Result.failure(e)
        }
    }
    
    fun getCurrentBalance(): Int = _wallet.value.balance
    
    fun getTransactionsByType(type: TransactionType): List<Transaction> {
        return _transactions.value.filter { it.type == type }
    }
    
    fun getTodayTransactions(): List<Transaction> {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return _transactions.value.filter { 
            java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.createdAt) == today 
        }
    }
    
    private fun getDefaultCreditAmount(habitType: String): Int {
        return when (habitType) {
            "steps" -> 10
            "focus_session" -> 25
            "hydration" -> 5
            "journaling" -> 15
            else -> 10
        }
    }
}
