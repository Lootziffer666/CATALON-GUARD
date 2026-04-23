package com.catalon.guard.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.catalon.guard.data.local.db.dao.*
import com.catalon.guard.data.local.db.entity.*

@Database(
    entities = [
        ProviderConfigEntity::class,
        ModelConfigEntity::class,
        RequestLogEntity::class,
        ProjectEntity::class,
        ConversationSessionEntity::class,
        ConversationMessageEntity::class,
        MemoryChunkEntity::class,
        HandoffLogEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun providerConfigDao(): ProviderConfigDao
    abstract fun modelConfigDao(): ModelConfigDao
    abstract fun requestLogDao(): RequestLogDao
    abstract fun projectDao(): ProjectDao
    abstract fun conversationSessionDao(): ConversationSessionDao
    abstract fun conversationMessageDao(): ConversationMessageDao
    abstract fun memoryChunkDao(): MemoryChunkDao
    abstract fun handoffLogDao(): HandoffLogDao
}
