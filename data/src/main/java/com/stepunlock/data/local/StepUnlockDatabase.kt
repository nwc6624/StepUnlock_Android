package com.stepunlock.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.stepunlock.data.local.dao.*
import com.stepunlock.data.local.entities.*

@Database(
    entities = [
        AppRuleEntity::class,
        CreditLedgerEntity::class,
        SessionEntity::class,
        HabitConfigEntity::class,
        StreakEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StepUnlockDatabase : RoomDatabase() {
    
    abstract fun appRuleDao(): AppRuleDao
    abstract fun creditLedgerDao(): CreditLedgerDao
    abstract fun sessionDao(): SessionDao
    abstract fun habitConfigDao(): HabitConfigDao
    abstract fun streakDao(): StreakDao
    
    companion object {
        const val DATABASE_NAME = "stepunlock_database"
        
        @Volatile
        private var INSTANCE: StepUnlockDatabase? = null
        
        fun getDatabase(context: Context): StepUnlockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepUnlockDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
