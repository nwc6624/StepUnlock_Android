package com.stepunlock.app.ui.screens.apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepunlock.app.data.model.AppRule
import com.stepunlock.app.data.repository.StepUnlockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AppsViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()
    
    private var stepUnlockRepository: StepUnlockRepository? = null
    private var packageManager: PackageManager? = null
    
    fun initialize(context: Context) {
        stepUnlockRepository = StepUnlockRepository(context)
        packageManager = context.packageManager
        
        viewModelScope.launch {
            loadInstalledApps()
            observeAppRules()
        }
    }
    
    private suspend fun loadInstalledApps() {
        try {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val installedApps = getInstalledApps()
            
            // For now, create AppRule objects directly from installed apps
            // TODO: Load existing rules from database
            val mergedApps = installedApps.map { installedApp ->
                AppRule(
                    packageName = installedApp.packageName,
                    appName = installedApp.appName,
                    isLocked = false,
                    unlockCost = 10, // Default cost
                    category = installedApp.category
                )
            }.sortedBy { it.appName }
            
            _uiState.value = _uiState.value.copy(
                allApps = mergedApps,
                isLoading = false
            )
            
            applyFilters()
            
        } catch (e: Exception) {
            android.util.Log.e("AppsViewModel", "Error loading apps", e)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message
            )
        }
    }
    
    private fun getInstalledApps(): List<InstalledApp> {
        val packageManager = this.packageManager ?: return emptyList()
        val installedPackages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        
        return installedPackages
            .filter { appInfo ->
                // Filter out system apps and our own app
                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                appInfo.packageName != "com.stepunlock.app"
            }
            .map { appInfo ->
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val category = getAppCategory(appInfo.packageName)
                
                InstalledApp(
                    packageName = appInfo.packageName,
                    appName = appName,
                    category = category
                )
            }
    }
    
    private fun getAppCategory(packageName: String): String {
        return when {
            packageName.contains("social") || 
            packageName.contains("facebook") || 
            packageName.contains("twitter") || 
            packageName.contains("instagram") || 
            packageName.contains("tiktok") -> "SOCIAL"
            
            packageName.contains("game") || 
            packageName.contains("play") -> "GAMES"
            
            packageName.contains("video") || 
            packageName.contains("youtube") || 
            packageName.contains("netflix") -> "ENTERTAINMENT"
            
            packageName.contains("browser") || 
            packageName.contains("chrome") || 
            packageName.contains("firefox") -> "BROWSER"
            
            else -> "OTHER"
        }
    }
    
    private fun observeAppRules() {
        // TODO: Implement proper database observation
        // For now, just update the locked count based on current state
        viewModelScope.launch {
            val currentApps = _uiState.value.allApps
            val lockedCount = currentApps.count { it.isLocked }
            _uiState.value = _uiState.value.copy(lockedAppsCount = lockedCount)
        }
    }
    
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }
    
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchQuery = "")
        applyFilters()
    }
    
    fun setFilter(filter: AppFilter) {
        _uiState.value = _uiState.value.copy(currentFilter = filter)
        applyFilters()
    }
    
    private fun applyFilters() {
        val allApps = _uiState.value.allApps
        val searchQuery = _uiState.value.searchQuery.lowercase()
        val filter = _uiState.value.currentFilter
        
        val filtered = allApps.filter { app ->
            // Search filter
            val matchesSearch = searchQuery.isEmpty() || 
                app.appName.lowercase().contains(searchQuery) ||
                app.packageName.lowercase().contains(searchQuery)
            
            // Category filter
            val matchesFilter = when (filter) {
                AppFilter.ALL -> true
                AppFilter.LOCKED -> app.isLocked
                AppFilter.UNLOCKED -> !app.isLocked
            }
            
            matchesSearch && matchesFilter
        }
        
        _uiState.value = _uiState.value.copy(filteredApps = filtered)
    }
    
    fun toggleAppLock(app: AppRule) {
        viewModelScope.launch {
            try {
                val updatedApp = app.copy(isLocked = !app.isLocked)
                
                // Update the app in the current list
                val currentApps = _uiState.value.allApps.toMutableList()
                val index = currentApps.indexOfFirst { it.packageName == app.packageName }
                if (index != -1) {
                    currentApps[index] = updatedApp
                    _uiState.value = _uiState.value.copy(allApps = currentApps)
                    applyFilters()
                }
                
                android.util.Log.d("AppsViewModel", "Toggled lock for ${app.appName}: ${updatedApp.isLocked}")
                
            } catch (e: Exception) {
                android.util.Log.e("AppsViewModel", "Error toggling app lock", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun updateUnlockCost(app: AppRule, newCost: Int) {
        viewModelScope.launch {
            try {
                val updatedApp = app.copy(unlockCost = newCost)
                
                // Update the app in the current list
                val currentApps = _uiState.value.allApps.toMutableList()
                val index = currentApps.indexOfFirst { it.packageName == app.packageName }
                if (index != -1) {
                    currentApps[index] = updatedApp
                    _uiState.value = _uiState.value.copy(allApps = currentApps)
                    applyFilters()
                }
                
                android.util.Log.d("AppsViewModel", "Updated unlock cost for ${app.appName}: $newCost")
                
            } catch (e: Exception) {
                android.util.Log.e("AppsViewModel", "Error updating unlock cost", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val category: String
)

data class AppsUiState(
    val allApps: List<AppRule> = emptyList(),
    val filteredApps: List<AppRule> = emptyList(),
    val searchQuery: String = "",
    val currentFilter: AppFilter = AppFilter.ALL,
    val lockedAppsCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)
