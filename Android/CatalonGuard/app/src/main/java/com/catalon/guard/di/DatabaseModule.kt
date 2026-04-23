package com.catalon.guard.di

import android.content.Context
import androidx.room.Room
import com.catalon.guard.data.local.db.AppDatabase
import com.catalon.guard.data.local.db.dao.*
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "catalon_guard.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideProviderConfigDao(db: AppDatabase) = db.providerConfigDao()
    @Provides fun provideModelConfigDao(db: AppDatabase) = db.modelConfigDao()
    @Provides fun provideRequestLogDao(db: AppDatabase) = db.requestLogDao()
    @Provides fun provideProjectDao(db: AppDatabase) = db.projectDao()
    @Provides fun provideConversationSessionDao(db: AppDatabase) = db.conversationSessionDao()
    @Provides fun provideConversationMessageDao(db: AppDatabase) = db.conversationMessageDao()
    @Provides fun provideMemoryChunkDao(db: AppDatabase) = db.memoryChunkDao()
    @Provides fun provideHandoffLogDao(db: AppDatabase) = db.handoffLogDao()
}
