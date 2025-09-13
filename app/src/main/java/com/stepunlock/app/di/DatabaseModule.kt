package com.stepunlock.app.di

import android.content.Context
import com.stepunlock.app.data.StepUnlockDatabase
import com.stepunlock.app.data.dao.AppRuleDao
import com.stepunlock.app.data.dao.CreditLedgerDao
import com.stepunlock.app.data.dao.HabitProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StepUnlockDatabase {
        return StepUnlockDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideAppRuleDao(database: StepUnlockDatabase): AppRuleDao {
        return database.appRuleDao()
    }
    
    @Provides
    fun provideCreditLedgerDao(database: StepUnlockDatabase): CreditLedgerDao {
        return database.creditLedgerDao()
    }
    
    @Provides
    fun provideHabitProgressDao(database: StepUnlockDatabase): HabitProgressDao {
        return database.habitProgressDao()
    }
}
