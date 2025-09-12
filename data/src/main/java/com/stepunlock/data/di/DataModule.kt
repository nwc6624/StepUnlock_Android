package com.stepunlock.data.di

import android.content.Context
import com.stepunlock.data.local.StepUnlockDatabase
import com.stepunlock.data.local.dao.*
import com.stepunlock.data.fitness.GoogleFitRepositoryImpl
import com.stepunlock.data.local.DataInitializer
import com.stepunlock.data.repository.*
import com.stepunlock.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
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
    fun provideSessionDao(database: StepUnlockDatabase): SessionDao {
        return database.sessionDao()
    }
    
    @Provides
    fun provideHabitConfigDao(database: StepUnlockDatabase): HabitConfigDao {
        return database.habitConfigDao()
    }
    
    @Provides
    fun provideStreakDao(database: StepUnlockDatabase): StreakDao {
        return database.streakDao()
    }
    
    @Provides
    fun provideAppRuleRepository(repository: AppRuleRepositoryImpl): AppRuleRepository = repository
    
    @Provides
    fun provideCreditRepository(repository: CreditRepositoryImpl): CreditRepository = repository
    
    @Provides
    fun provideSessionRepository(repository: SessionRepositoryImpl): SessionRepository = repository
    
    @Provides
    fun provideHabitConfigRepository(repository: HabitConfigRepositoryImpl): HabitConfigRepository = repository
    
    @Provides
    fun provideStreakRepository(repository: StreakRepositoryImpl): StreakRepository = repository
    
    @Provides
    fun provideGoogleFitRepository(repository: GoogleFitRepositoryImpl): GoogleFitRepository = repository
    
    @Provides
    fun provideDataInitializer(initializer: DataInitializer): DataInitializer = initializer
}
