package com.catalon.guard.data.local.db

import com.catalon.guard.data.local.db.dao.ModelConfigDao
import com.catalon.guard.data.local.db.dao.ProviderConfigDao
import com.catalon.guard.data.local.db.entity.ModelConfigEntity
import com.catalon.guard.data.local.db.entity.ProviderConfigEntity
import com.catalon.guard.data.local.prefs.EncryptedPrefsManager
import com.catalon.guard.data.remote.provider.ProviderRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val providerConfigDao: ProviderConfigDao,
    private val modelConfigDao: ModelConfigDao,
    private val encryptedPrefs: EncryptedPrefsManager
) {
    suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
        if (!encryptedPrefs.isFirstLaunch()) return@withContext
        seedProviders()
        encryptedPrefs.markFirstLaunchDone()
    }

    private suspend fun seedProviders() {
        val providerEntities = ProviderRegistry.defaults.map { default ->
            ProviderConfigEntity(
                id = default.id,
                name = default.name,
                baseUrl = default.baseUrl,
                apiKeyAlias = "api_key_${default.id}",
                rpmLimit = default.rpmLimit,
                rpdLimit = default.rpdLimit,
                contextWindow = default.contextWindow,
                maxOutput = default.maxOutput,
                tier = default.tier,
                isByok = default.isByok,
                enabled = !default.isByok,
                authType = default.authType,
                selectedModel = default.models.firstOrNull()?.modelId ?: "",
                notes = default.notes,
                registrationUrl = default.registrationUrl
            )
        }
        providerConfigDao.upsertAll(providerEntities)

        val modelEntities = ProviderRegistry.defaults.flatMap { default ->
            default.models.mapIndexed { _, model ->
                ModelConfigEntity(
                    id = "${default.id}_${model.modelId}",
                    providerId = default.id,
                    modelId = model.modelId,
                    displayName = model.displayName,
                    contextWindow = model.contextWindow,
                    maxOutput = model.maxOutput,
                    rpmLimit = model.rpmLimit,
                    rpdLimit = model.rpdLimit,
                    supportsVision = model.supportsVision,
                    supportsReasoning = model.supportsReasoning
                )
            }
        }
        modelConfigDao.upsertAll(modelEntities)
    }
}
