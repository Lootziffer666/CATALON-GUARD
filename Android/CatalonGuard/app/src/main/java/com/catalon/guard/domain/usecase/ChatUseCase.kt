package com.catalon.guard.domain.usecase

import com.catalon.guard.data.local.db.dao.RequestLogDao
import com.catalon.guard.data.local.db.entity.RequestLogEntity
import com.catalon.guard.data.repository.ConversationRepository
import com.catalon.guard.data.repository.LlmRepository
import com.catalon.guard.domain.model.ChatMessage
import com.catalon.guard.domain.model.HandoffEvent
import com.catalon.guard.util.HandoffManager
import com.catalon.guard.util.RateLimitTracker
import com.catalon.guard.util.SkillRouter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatUseCase @Inject constructor(
    private val llmRepository: LlmRepository,
    private val rateLimitTracker: RateLimitTracker,
    private val handoffManager: HandoffManager,
    private val requestLogDao: RequestLogDao,
    private val conversationRepository: ConversationRepository,
    private val memoryUseCase: MemoryUseCase,
    private val skillRouter: SkillRouter
) {
    sealed class ChatResult {
        data class Token(val text: String, val providerId: String) : ChatResult()
        data class Handoff(val event: HandoffEvent) : ChatResult()
        data class Complete(val providerId: String, val outputTokens: Int) : ChatResult()
        data class Error(val message: String) : ChatResult()
    }

    fun chat(
        messages: List<ChatMessage>,
        sessionId: String,
        preferredProviderId: String? = null
    ): Flow<ChatResult> = flow {
        val memories = memoryUseCase.retrieveRelevant(
            messages.lastOrNull { it.role == "user" }?.content ?: "", sessionId
        )
        val enrichedMessages = buildMessagesWithMemory(messages, memories)

        val lastUserMsg = messages.lastOrNull { it.role == "user" }?.content ?: ""
        val specialty = skillRouter.detectSpecialty(lastUserMsg)
        val providers = rateLimitTracker.getRankedAvailableProviders(specialty).toMutableList()

        // Move user-selected provider to front of the list
        if (preferredProviderId != null) {
            val preferred = providers.find { it.id == preferredProviderId }
            if (preferred != null) {
                providers.remove(preferred)
                providers.add(0, preferred)
            }
        }

        val triedIds = mutableSetOf<String>()
        val accumulated = StringBuilder()
        val startTime = System.currentTimeMillis()

        if (providers.isEmpty()) {
            emit(ChatResult.Error("No available providers. Please add API keys in Settings."))
            return@flow
        }

        while (providers.isNotEmpty()) {
            val provider = providers.removeAt(0)
            triedIds.add(provider.id)
            accumulated.clear()

            try {
                llmRepository.streamChat(provider, enrichedMessages)
                    .collect { token ->
                        accumulated.append(token)
                        emit(ChatResult.Token(token, provider.id))
                    }

                val latency = System.currentTimeMillis() - startTime
                val outputTokens = (accumulated.length / 4).coerceAtLeast(1)
                val inputTokens = enrichedMessages.sumOf { it.content.length / 4 }.coerceAtLeast(1)

                requestLogDao.insert(
                    RequestLogEntity(
                        providerId = provider.id,
                        modelId = provider.selectedModel,
                        sessionId = sessionId,
                        timestamp = System.currentTimeMillis(),
                        inputTokens = inputTokens,
                        outputTokens = outputTokens,
                        latencyMs = latency,
                        wasHandoff = triedIds.size > 1
                    )
                )

                conversationRepository.saveMessage(
                    sessionId = sessionId,
                    role = "assistant",
                    content = accumulated.toString(),
                    providerId = provider.id,
                    modelId = provider.selectedModel
                )

                if (accumulated.isNotEmpty()) {
                    memoryUseCase.store(accumulated.toString(), sessionId)
                }

                emit(ChatResult.Complete(provider.id, outputTokens))
                return@flow

            } catch (e: HttpException) {
                val code = e.code()
                val reason = when {
                    code == 429 -> "rate_limit_429"
                    code in 500..599 -> "server_error_$code"
                    else -> "http_error_$code"
                }

                requestLogDao.insert(
                    RequestLogEntity(
                        providerId = provider.id,
                        modelId = provider.selectedModel,
                        sessionId = sessionId,
                        timestamp = System.currentTimeMillis(),
                        inputTokens = 0, outputTokens = 0,
                        errorCode = code
                    )
                )

                val next = handoffManager.selectNextProvider(provider.id, triedIds)
                if (next != null) {
                    handoffManager.logHandoff(
                        sessionId, provider.id, next.id, reason, enrichedMessages.size
                    )
                    emit(ChatResult.Handoff(
                        HandoffEvent(
                            fromProviderId = provider.id,
                            fromProviderName = provider.name,
                            toProviderId = next.id,
                            toProviderName = next.name,
                            reason = reason
                        )
                    ))
                    providers.add(0, next)
                } else if (code !in listOf(429, 500, 502, 503)) {
                    emit(ChatResult.Error("HTTP $code from ${provider.name}: ${e.message()}"))
                    return@flow
                }

            } catch (e: IllegalStateException) {
                // Missing API key — skip provider silently
                val next = handoffManager.selectNextProvider(provider.id, triedIds)
                if (next != null) {
                    providers.add(0, next)
                }

            } catch (e: IOException) {
                val next = handoffManager.selectNextProvider(provider.id, triedIds)
                if (next != null) {
                    handoffManager.logHandoff(
                        sessionId, provider.id, next.id, "io_error", enrichedMessages.size
                    )
                    emit(ChatResult.Handoff(
                        HandoffEvent(provider.id, provider.name, next.id, next.name, "connection_error")
                    ))
                    providers.add(0, next)
                }
            }
        }

        emit(ChatResult.Error("All providers exhausted. Check API keys and rate limits."))
    }

    private fun buildMessagesWithMemory(
        messages: List<ChatMessage>,
        memories: List<String>
    ): List<ChatMessage> {
        if (memories.isEmpty()) return messages
        val memoryContext = memories.joinToString("\n\n") { "Memory: $it" }
        val systemMsg = ChatMessage(
            id = "memory_context",
            role = "system",
            content = "Relevant context from previous conversations:\n\n$memoryContext"
        )
        return listOf(systemMsg) + messages
    }
}
