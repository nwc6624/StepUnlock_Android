package com.stepunlock.app.ui.screens.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepunlock.app.data.entity.AppRuleEntity
import com.stepunlock.app.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()
    
    init {
        loadApps()
    }
    
    fun loadApps() {
        viewModelScope.launch {
            try {
                combine(
                    appRepository.getAllApps(),
                    appRepository.getLockedAppsCount()
                ) { apps, lockedCount ->
                    val filteredApps = filterApps(apps)
                    val allAppsLocked = apps.isNotEmpty() && apps.all { it.isLocked }
                    
                    _uiState.value = _uiState.value.copy(
                        apps = apps,
                        filteredApps = filteredApps,
                        lockedAppsCount = lockedCount,
                        allAppsLocked = allAppsLocked,
                        isLoading = false
                    )
                }.collect { }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterApps()
    }
    
    fun updateFilter(filter: AppFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        filterApps()
    }
    
    private fun filterApps() {
        val currentState = _uiState.value
        val filtered = filterApps(currentState.apps)
        _uiState.value = currentState.copy(filteredApps = filtered)
    }
    
    private fun filterApps(apps: List<AppRuleEntity>): List<AppRuleEntity> {
        val currentState = _uiState.value
        var filtered = apps
        
        // Apply search filter
        if (currentState.searchQuery.isNotBlank()) {
            filtered = filtered.filter { app ->
                app.appName.contains(currentState.searchQuery, ignoreCase = true) ||
                app.category.contains(currentState.searchQuery, ignoreCase = true)
            }
        }
        
        // Apply category filter
        when (currentState.selectedFilter) {
            AppFilter.LOCKED -> filtered = filtered.filter { it.isLocked }
            AppFilter.SOCIAL -> filtered = filtered.filter { it.category == "Social" }
            AppFilter.ENTERTAINMENT -> filtered = filtered.filter { it.category == "Entertainment" }
            AppFilter.GAMES -> filtered = filtered.filter { it.category == "Games" }
            AppFilter.ALL -> { /* No additional filtering */ }
        }
        
        // Sort by name
        return filtered.sortedBy { it.appName }
    }
    
    fun toggleAppLock(packageName: String) {
        viewModelScope.launch {
            try {
                val app = appRepository.getAppByPackage(packageName)
                if (app != null) {
                    appRepository.updateLockStatus(packageName, !app.isLocked)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun updateAppCost(packageName: String, cost: Int) {
        viewModelScope.launch {
            try {
                val app = appRepository.getAppByPackage(packageName)
                if (app != null && cost > 0) {
                    val updatedApp = app.copy(unlockCost = cost)
                    appRepository.updateApp(updatedApp)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun toggleAllApps() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val shouldLock = !currentState.allAppsLocked
                
                // Update all apps
                currentState.apps.forEach { app ->
                    appRepository.updateLockStatus(app.packageName, shouldLock)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AppsUiState(
    val apps: List<AppRuleEntity> = emptyList(),
    val filteredApps: List<AppRuleEntity> = emptyList(),
    val lockedAppsCount: Int = 0,
    val allAppsLocked: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: AppFilter = AppFilter.ALL,
    val isLoading: Boolean = true,
    val error: String? = null
)
