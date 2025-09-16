package com.stepunlock.app.ui.screens.apps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.stepunlock.app.data.repository.StepUnlockRepository
import com.stepunlock.app.data.model.AppRule
import com.stepunlock.app.data.repository.AppCategory

class AppsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    private var repository: StepUnlockRepository? = null
    private var allApps: List<AppRule> = emptyList()

    fun loadApps(context: Context) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                if (repository == null) {
                    repository = StepUnlockRepository(context)
                    repository?.initializeDefaultData()
                    repository?.syncInstalledApps()
                }
                
                repository?.getAllApps()?.collect { apps ->
                    allApps = apps
                    val lockedCount = apps.count { it.isLocked }
                    
                    _uiState.value = _uiState.value.copy(
                        apps = apps,
                        filteredApps = filterApps(apps, _uiState.value.searchQuery, _uiState.value.selectedFilter),
                        lockedAppsCount = lockedCount,
                        allAppsLocked = lockedCount == apps.size,
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

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredApps = filterApps(allApps, query, _uiState.value.selectedFilter)
        )
    }

    fun updateFilter(filter: AppFilter) {
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            filteredApps = filterApps(allApps, _uiState.value.searchQuery, filter)
        )
    }

    fun toggleAppLock(packageName: String) {
        viewModelScope.launch {
            repository?.updateLockStatus(packageName, !allApps.find { it.packageName == packageName }?.isLocked ?: false)
        }
    }

    fun updateAppCost(packageName: String, cost: Int) {
        viewModelScope.launch {
            repository?.updateUnlockCost(packageName, cost)
        }
    }

    fun toggleAllApps() {
        viewModelScope.launch {
            val shouldLockAll = !_uiState.value.allAppsLocked
            for (app in allApps) {
                repository?.updateLockStatus(app.packageName, shouldLockAll)
            }
        }
    }

    private fun filterApps(apps: List<AppRule>, query: String, filter: AppFilter): List<AppRule> {
        var filtered = apps
        
        // Apply search filter
        if (query.isNotBlank()) {
            filtered = filtered.filter { 
                it.appName.contains(query, ignoreCase = true) ||
                it.packageName.contains(query, ignoreCase = true)
            }
        }
        
        // Apply category filter
        filtered = when (filter) {
            AppFilter.ALL -> filtered
            AppFilter.LOCKED -> filtered.filter { it.isLocked }
            AppFilter.UNLOCKED -> filtered.filter { !it.isLocked }
            AppFilter.SOCIAL -> filtered.filter { it.category == AppCategory.SOCIAL.name }
            AppFilter.ENTERTAINMENT -> filtered.filter { it.category == AppCategory.ENTERTAINMENT.name }
            AppFilter.GAMES -> filtered.filter { it.category == AppCategory.GAMES.name }
            AppFilter.PRODUCTIVITY -> filtered.filter { it.category == AppCategory.PRODUCTIVITY.name }
        }
        
        return filtered
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AppsUiState(
    val apps: List<AppRule> = emptyList(),
    val filteredApps: List<AppRule> = emptyList(),
    val lockedAppsCount: Int = 0,
    val allAppsLocked: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: AppFilter = AppFilter.ALL,
    val isLoading: Boolean = true,
    val error: String? = null
)

enum class AppFilter {
    ALL, LOCKED, UNLOCKED, SOCIAL, ENTERTAINMENT, GAMES, PRODUCTIVITY
}