package com.stepunlock.app.data.services

import com.stepunlock.app.data.events.EarnEvent
import com.stepunlock.app.data.events.SpendEvent
import com.stepunlock.app.data.models.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppService {
    
    private val walletService = WalletService()
    private val habitService = HabitService()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Combined state for easy access
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()
    
    init {
        // Combine wallet and habit data into app state
        scope.launch {
            combine(
                walletService.wallet,
                habitService.progress
            ) { wallet, progress ->
                AppState(
                    wallet = wallet,
                    habitProgress = progress,
                    isLoading = false
                )
            }.collect { state ->
                _appState.value = state
            }
        }
    }
    
    // Wallet operations
    suspend fun earnCredits(event: EarnEvent): Result<Transaction> {
        return try {
            // Validate habit constraints first
            val canEarn = habitService.canEarnCredits(event.type)
            if (canEarn.isFailure) {
                return canEarn.map { false }.map { throw Exception(canEarn.exceptionOrNull()?.message) }
            }
            
            // Update habit progress
            val progressResult = habitService.updateProgress(event.type, event.units ?: 1)
            if (progressResult.isFailure) {
                return progressResult.map { throw Exception(progressResult.exceptionOrNull()?.message) }
            }
            
            // Earn credits
            val transactionResult = walletService.earnCredits(event)
            if (transactionResult.isFailure) {
                return transactionResult
            }
            
            android.util.Log.d("AppService", "Successfully earned credits for ${event.type}")
            transactionResult
            
        } catch (e: Exception) {
            android.util.Log.e("AppService", "Error in earnCredits", e)
            Result.failure(e)
        }
    }
    
    suspend fun spendCredits(event: SpendEvent): Result<Transaction> {
        return walletService.spendCredits(event)
    }
    
    // Convenience methods
    fun getCurrentBalance(): Int = walletService.getCurrentBalance()
    
    fun getTodayProgress(habitId: String) = habitService.getTodayProgress(habitId)
    
    fun getAllTodayProgress() = habitService.getAllTodayProgress()
    
    fun getHabitById(habitId: String) = habitService.getHabitById(habitId)
    
    fun getRemainingCooldownMinutes(habitId: String) = habitService.getRemainingCooldownMinutes(habitId)
    
    fun isHabitCompleted(habitId: String) = habitService.isHabitCompleted(habitId)
    
    fun getHabitProgressPercentage(habitId: String) = habitService.getHabitProgressPercentage(habitId)
    
    fun getTodayTransactions() = walletService.getTodayTransactions()
    
    fun getTransactionsByType(type: com.stepunlock.app.data.models.TransactionType) = walletService.getTransactionsByType(type)
}

data class AppState(
    val wallet: com.stepunlock.app.data.models.Wallet = com.stepunlock.app.data.models.Wallet(),
    val habitProgress: Map<String, com.stepunlock.app.data.models.HabitProgress> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)
