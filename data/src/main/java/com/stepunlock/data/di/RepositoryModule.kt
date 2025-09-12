package com.stepunlock.data.di

import com.stepunlock.data.repository.AppRepositoryImpl
import com.stepunlock.data.repository.CreditRepositoryImpl
import com.stepunlock.data.repository.HabitRepositoryImpl
import com.stepunlock.domain.repository.AppRepository
import com.stepunlock.domain.repository.CreditRepository
import com.stepunlock.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindCreditRepository(
        creditRepositoryImpl: CreditRepositoryImpl
    ): CreditRepository
    
    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        habitRepositoryImpl: HabitRepositoryImpl
    ): HabitRepository
    
    @Binds
    @Singleton
    abstract fun bindAppRepository(
        appRepositoryImpl: AppRepositoryImpl
    ): AppRepository
}
