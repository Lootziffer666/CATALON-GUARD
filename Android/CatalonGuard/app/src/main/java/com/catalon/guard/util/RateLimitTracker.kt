package com.catalon.guard.util

import com.catalon.guard.data.local.db.dao.ProviderConfigDao
import com.catalon.guard.data.local.db.dao.RequestLogDao
import com.catalon.guard.data.local.db.entity.ProviderConfigEntity
import kotlinx.coroutines.sync.Semaphore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RateLimitTracker @Inject constructor(
    private val requestLogDao: RequestLogDao,
    private val providerConfigDao: ProviderConfigDao
) {
    // Z.AI has a concurrent request limit of 1
    private val zaiSemaphore = Semaphore(1)

    suspend fun canUse(provider: ProviderConfigEntity): Boolean {
        if (!provider.enabled) return false

        val now = System.currentTimeMillis()
        val oneMinuteAgo = now - 60_000L
        val oneDayAgo = now - 86_400_000L

        val rpmCount = requestLogDao.countSince(provider.id, oneMinuteAgo)
        val rpdCount = requestLogDao.countSince(provider.id, oneDayAgo)

        val effectiveRpm = if (provider.rpmLimit == Int.MAX_VALUE) Int.MAX_VALUE else provider.rpmLimit - 1
        val effectiveRpd = if (provider.rpdLimit == Int.MAX_VALUE) Int.MAX_VALUE else provider.rpdLimit - 5

        return rpmCount < effectiveRpm && rpdCount < effectiveRpd
    }

    suspend fun getRankedAvailableProviders(): List<ProviderConfigEntity> =
        providerConfigDao.getEnabledProviders()
            .filter { canUse(it) }
            .sortedWith(compareBy({ it.tier }, { it.rpmLimit.toLong() * -1 }))

    suspend fun getUsage(providerId: String): Pair<Int, Int> {
        val now = System.currentTimeMillis()
        val rpm = requestLogDao.countSince(providerId, now - 60_000L)
        val rpd = requestLogDao.countSince(providerId, now - 86_400_000L)
        return rpm to rpd
    }

    fun getZaiSemaphore(): Semaphore = zaiSemaphore
}
