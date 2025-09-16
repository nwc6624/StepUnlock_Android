package com.stepunlock.app.ui.screens.history

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color

class HistoryViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    private val todayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun loadHistory() {
        viewModelScope.launch {
            try {
                // Simulate loading delay
                kotlinx.coroutines.delay(500)
                
                // Mock data
                _uiState.value = _uiState.value.copy(
                    transactions = emptyList(),
                    filteredTransactions = emptyList(),
                    totalCredits = 150,
                    todayTransactions = 3,
                    isLoading = false
                )
                
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
