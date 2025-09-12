package com.stepunlock.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.stepunlock.data.local.dao.AppRuleDao
import com.stepunlock.data.local.dao.CreditLedgerDao
import com.stepunlock.data.local.dao.HabitProgressDao
import com.stepunlock.data.local.entities.AppRuleEntity
import com.stepunlock.data.local.entities.CreditLedgerEntity
import com.stepunlock.data.local.entities.HabitProgressEntity

@Database(
    entities = [
        AppRuleEntity::class,
        CreditLedgerEntity::class,
        HabitProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class StepUnlockDatabase : RoomDatabase() {
    abstract fun appRuleDao(): AppRuleDao
    abstract fun creditLedgerDao(): CreditLedgerDao
    abstract fun habitProgressDao(): HabitProgressDao
    
    companion object {
        @Volatile
        private var INSTANCE: StepUnlockDatabase? = null
        
        fun getDatabase(context: Context): StepUnlockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepUnlockDatabase::class.java,
                    "stepunlock_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}