package com.stepunlock.app.ui.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepunlock.app.data.repository.AppRepository
import com.stepunlock.app.data.repository.CreditRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val creditRepository: CreditRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LockUiState())
    val uiState: StateFlow<LockUiState> = _uiState.asStateFlow()
    
    fun loadAppInfo(packageName: String) {
        viewModelScope.launch {
            try {
                val app = appRepository.getAppByPackage(packageName)
                val creditBalance = creditRepository.getTotalCredits()
                
                _uiState.value = _uiState.value.copy(
                    app = app,
                    creditBalance = creditBalance,
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
    
    fun unlockApp(packageName: String, duration: Long, cost: Int) {
        viewModelScope.launch {
            try {
                // Check if user has enough credits
                val currentBalance = creditRepository.getTotalCredits()
                if (currentBalance < cost) {
                    _uiState.value = _uiState.value.copy(
                        error = "Not enough credits!"
                    )
                    return@launch
                }
                
                // Spend credits
                creditRepository.insertTransaction(
                    com.stepunlock.app.data.entity.CreditLedgerEntity(
                        amount = -cost,
                        reason = "unlock_app_${packageName}",
                        appPackageName = packageName
                    )
                )
                
                // Update app unlock status
                val unlockUntil = System.currentTimeMillis() + duration
                appRepository.updateUnlockStatus(packageName, true, unlockUntil)
                
                // Update UI state
                _uiState.value = _uiState.value.copy(
                    creditBalance = currentBalance - cost,
                    isUnlocked = true
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LockUiState(
    val app: com.stepunlock.app.data.entity.AppRuleEntity? = null,
    val creditBalance: Int = 0,
    val isLoading: Boolean = true,
    val isUnlocked: Boolean = false,
    val error: String? = null
)
