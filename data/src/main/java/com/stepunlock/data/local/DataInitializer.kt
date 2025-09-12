package com.stepunlock.data.local

import com.stepunlock.data.local.entities.CreditLedgerEntity
import com.stepunlock.data.local.entities.HabitProgressEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataInitializer @Inject constructor(
    private val database: StepUnlockDatabase
) {
    
    suspend fun initializeData() = withContext(Dispatchers.IO) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        // Initialize today's habit progress if it doesn't exist
        val existingProgress = database.habitProgressDao().getHabitProgressForDate(today)
        if (existingProgress == null) {
            val defaultProgress = HabitProgressEntity(
                date = today,
                stepsCount = 0,
                stepsTarget = 10000,
                pomodorosCompleted = 0,
                pomodorosTarget = 4,
                waterGlasses = 0,
                waterTarget = 8,
                journalEntries = 0,
                journalTarget = 1,
                customHabitsCompleted = 0
            )
            database.habitProgressDao().insertHabitProgress(defaultProgress)
        }
        
        // Add welcome credits if this is the first time
        val currentBalance = database.creditLedgerDao().getCurrentBalance()
        if (currentBalance != null) {
            currentBalance.collect { balance ->
                if (balance == null || balance == 0) {
                    val welcomeTransaction = CreditLedgerEntity(
                        amount = 50,
                        reason = "Welcome to StepUnlock!",
                        type = "EARNED",
                        timestamp = System.currentTimeMillis()
                    )
                    database.creditLedgerDao().insertTransaction(welcomeTransaction)
                }
            }
        }
    }
}