package com.stepunlock.data.repository

import com.stepunlock.data.local.dao.HabitProgressDao
import com.stepunlock.data.local.entities.HabitProgressEntity
import com.stepunlock.domain.model.HabitProgress
import com.stepunlock.domain.model.HabitType
import com.stepunlock.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitProgressDao: HabitProgressDao
) : HabitRepository {
    
    override fun getTodayHabitProgress(): Flow<HabitProgress> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return habitProgressDao.getHabitProgressForDateFlow(today).map { entity ->
            entity?.toHabitProgress() ?: HabitProgress()
        }
    }
    
    override suspend fun logStepCount(steps: Int) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        habitProgressDao.addSteps(today, steps)
    }
    
    override suspend fun completePomodoro() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        habitProgressDao.addPomodoro(today)
    }
    
    override suspend fun logWaterIntake() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        habitProgressDao.addWaterGlass(today)
    }
    
    override suspend fun logJournalEntry() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        habitProgressDao.addJournalEntry(today)
    }
    
    override suspend fun completeCustomHabit(habitId: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        habitProgressDao.addCustomHabit(today)
    }
    
    override fun getHabitStreaks(): Flow<Map<HabitType, Int>> {
        // TODO: Implement streak calculation
        return kotlinx.coroutines.flow.flowOf(emptyMap())
    }
}

private fun HabitProgressEntity.toHabitProgress(): HabitProgress {
    return HabitProgress(
        stepsToday = stepsCount,
        stepsTarget = stepsTarget,
        stepsProgress = if (stepsTarget > 0) stepsCount.toFloat() / stepsTarget else 0f,
        pomodorosCompleted = pomodorosCompleted,
        pomodorosTarget = pomodorosTarget,
        waterGlasses = waterGlasses,
        waterTarget = waterTarget,
        journalEntries = journalEntries,
        journalTarget = journalTarget,
        customHabitsCompleted = customHabitsCompleted
    )
}
