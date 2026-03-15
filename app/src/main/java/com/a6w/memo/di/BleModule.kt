package com.a6w.memo.di

import android.content.Context
import com.a6w.memo.data.repository.BleRepositoryImpl
import com.a6w.memo.domain.repository.BleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BleModule {
    @Provides
    @Singleton
    fun provideBleRepository(
        @ApplicationContext context: Context
    ): BleRepository = BleRepositoryImpl(context)
}