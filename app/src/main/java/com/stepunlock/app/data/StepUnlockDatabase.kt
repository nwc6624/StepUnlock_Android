package com.stepunlock.app.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.stepunlock.app.data.dao.AppRuleDao
import com.stepunlock.app.data.dao.CreditLedgerDao
import com.stepunlock.app.data.dao.HabitProgressDao
import com.stepunlock.app.data.entity.AppRuleEntity
import com.stepunlock.app.data.entity.CreditLedgerEntity
import com.stepunlock.app.data.entity.HabitProgressEntity

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
