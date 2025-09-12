package com.stepunlock.app.ui.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepunlock.domain.repository.CreditRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val creditRepository: CreditRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LockUiState())
    val uiState: StateFlow<LockUiState> = _uiState.asStateFlow()
    
    init {
        loadCredits()
    }
    
    private fun loadCredits() {
        viewModelScope.launch {
            creditRepository.getTotalCredits()
                .onSuccess { credits ->
                    _uiState.update { it.copy(totalCredits = credits) }
                }
                .onError { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }
}

data class LockUiState(
    val totalCredits: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
