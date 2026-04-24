package com.catalon.guard.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2,
    exportSchema = false
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

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE provider_configs ADD COLUMN registrationUrl TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE model_configs ADD COLUMN specialties TEXT NOT NULL DEFAULT 'GENERAL'")
            }
        }
    }
}
