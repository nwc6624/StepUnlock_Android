package com.stepunlock.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.stepunlock.app.data.dao.AppRuleDao
import com.stepunlock.app.data.dao.CreditTransactionDao
import com.stepunlock.app.data.dao.HabitProgressDao
import com.stepunlock.app.data.model.AppRule
import com.stepunlock.app.data.model.CreditTransaction
import com.stepunlock.app.data.model.HabitProgress

@Database(
    entities = [AppRule::class, CreditTransaction::class, HabitProgress::class],
    version = 1,
    exportSchema = false
)
abstract class StepUnlockDatabase : RoomDatabase() {
    abstract fun appRuleDao(): AppRuleDao
    abstract fun creditTransactionDao(): CreditTransactionDao
    abstract fun habitProgressDao(): HabitProgressDao

    companion object {
        @Volatile
        private var INSTANCE: StepUnlockDatabase? = null

        fun getDatabase(context: Context): StepUnlockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepUnlockDatabase::class.java,
                    "stepunlock_new_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
