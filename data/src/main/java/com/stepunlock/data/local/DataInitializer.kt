package com.stepunlock.data.local

import com.stepunlock.data.local.dao.HabitConfigDao
import com.stepunlock.data.mapper.HabitConfigMapper
import com.stepunlock.domain.repository.HabitConfigRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataInitializer @Inject constructor(
    private val habitConfigRepository: HabitConfigRepository
) {
    
    fun initializeData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if data is already initialized
                val existingConfigs = habitConfigRepository.getAllHabitConfigs()
                existingConfigs.collect { configs ->
                    if (configs.isEmpty()) {
                        // Initialize with default habit configurations
                        val defaultConfigs = DefaultDataProvider.getDefaultHabitConfigs()
                        habitConfigRepository.insertHabitConfigs(defaultConfigs)
                    }
                    return@collect
                }
            } catch (e: Exception) {
                // Log error but don't crash the app
                e.printStackTrace()
            }
        }
    }
}
