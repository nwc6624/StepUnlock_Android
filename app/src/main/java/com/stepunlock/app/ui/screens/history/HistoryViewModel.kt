package com.stepunlock.app.ui.screens.history

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepunlock.app.data.entity.CreditLedgerEntity
import com.stepunlock.app.data.repository.CreditRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import androidx.compose.ui.graphics.Color

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val creditRepository: CreditRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    private val todayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun loadHistory() {
        viewModelScope.launch {
            try {
                // Load transactions
                creditRepository.getAllTransactions().collect { transactions ->
                    val totalCredits = creditRepository.getTotalCredits()
                    
                    val today = todayFormatter.format(Date())
                    val todayTransactions = transactions.count { 
                        todayFormatter.format(Date(it.timestamp)) == today 
                    }
                    
                    val transactionItems = transactions.map { transaction ->
                        convertToTransactionItem(transaction)
                    }
                    
                    val filteredTransactions = filterTransactions(transactionItems)
                    
                    _uiState.value = _uiState.value.copy(
                        transactions = transactionItems,
                        filteredTransactions = filteredTransactions,
                        totalCredits = totalCredits,
                        todayTransactions = todayTransactions,
                        isLoading = false
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun updateFilter(filter: HistoryFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        filterTransactions()
    }
    
    private fun filterTransactions() {
        val currentState = _uiState.value
        val filtered = filterTransactions(currentState.transactions)
        _uiState.value = currentState.copy(filteredTransactions = filtered)
    }
    
    private fun filterTransactions(transactions: List<TransactionHistoryItem>): List<TransactionHistoryItem> {
        val filter = _uiState.value.selectedFilter
        return when (filter) {
            HistoryFilter.EARNINGS -> transactions.filter { it.amount > 0 }
            HistoryFilter.SPENDING -> transactions.filter { it.amount < 0 }
            HistoryFilter.ALL -> transactions
        }
    }
    
    private fun convertToTransactionItem(transaction: CreditLedgerEntity): TransactionHistoryItem {
        val isEarning = transaction.amount > 0
        val (displayReason, icon, iconColor) = getTransactionDetails(transaction, isEarning)
        
        return TransactionHistoryItem(
            id = transaction.id,
            amount = transaction.amount,
            reason = transaction.reason,
            displayReason = displayReason,
            timestamp = transaction.timestamp,
            formattedTime = formatTimestamp(transaction.timestamp),
            appName = transaction.appPackageName?.let { getAppNameFromPackage(it) },
            icon = icon,
            iconColor = iconColor
        )
    }
    
    private fun getTransactionDetails(transaction: CreditLedgerEntity, isEarning: Boolean): Triple<String, androidx.compose.ui.graphics.vector.ImageVector, Color> {
        return when {
            transaction.reason.contains("steps") -> Triple(
                "Steps Completed",
                Icons.Default.DirectionsWalk,
                Color(0xFF2196F3)
            )
            transaction.reason.contains("pomodoro") -> Triple(
                "Focus Session",
                Icons.Default.Timer,
                Color(0xFF9C27B0)
            )
            transaction.reason.contains("water") -> Triple(
                "Hydration",
                Icons.Default.LocalDrink,
                Color(0xFF00BCD4)
            )
            transaction.reason.contains("journaling") -> Triple(
                "Journal Entry",
                Icons.Default.EditNote,
                Color(0xFFFF9800)
            )
            transaction.reason.contains("unlock_app") -> Triple(
                "App Unlocked",
                Icons.Default.LockOpen,
                Color(0xFFFF5722)
            )
            transaction.reason.contains("welcome_bonus") -> Triple(
                "Welcome Bonus",
                Icons.Default.Celebration,
                Color(0xFFFFC107)
            )
            isEarning -> Triple(
                "Habit Completed",
                Icons.Default.CheckCircle,
                Color(0xFF4CAF50)
            )
            else -> Triple(
                "Credit Spent",
                Icons.Default.Remove,
                Color(0xFFF44336)
            )
        }
    }
    
    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val today = Date()
        
        return when {
            isSameDay(date, today) -> {
                val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                "Today at ${timeFormatter.format(date)}"
            }
            isYesterday(date, today) -> {
                val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                "Yesterday at ${timeFormatter.format(date)}"
            }
            else -> dateFormatter.format(date)
        }
    }
    
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun isYesterday(date: Date, today: Date): Boolean {
        val yesterday = Calendar.getInstance().apply { 
            time = today
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val cal = Calendar.getInstance().apply { time = date }
        return cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun getAppNameFromPackage(packageName: String): String {
        // Simple mapping for common apps
        return when {
            packageName.contains("facebook") -> "Facebook"
            packageName.contains("instagram") -> "Instagram"
            packageName.contains("twitter") -> "Twitter"
            packageName.contains("tiktok") -> "TikTok"
            packageName.contains("youtube") -> "YouTube"
            packageName.contains("netflix") -> "Netflix"
            packageName.contains("spotify") -> "Spotify"
            packageName.contains("whatsapp") -> "WhatsApp"
            packageName.contains("telegram") -> "Telegram"
            packageName.contains("discord") -> "Discord"
            packageName.contains("reddit") -> "Reddit"
            else -> packageName.substringAfterLast(".")
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class HistoryUiState(
    val transactions: List<TransactionHistoryItem> = emptyList(),
    val filteredTransactions: List<TransactionHistoryItem> = emptyList(),
    val totalCredits: Int = 0,
    val todayTransactions: Int = 0,
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,
    val isLoading: Boolean = true,
    val error: String? = null
)
