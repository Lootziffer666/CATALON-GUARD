package com.catalon.guard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catalon.guard.data.local.db.dao.ProviderConfigDao
import com.catalon.guard.data.local.db.entity.ProviderConfigEntity
import com.catalon.guard.data.repository.ConversationRepository
import com.catalon.guard.domain.model.ChatMessage
import com.catalon.guard.domain.model.HandoffEvent
import com.catalon.guard.domain.usecase.ChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase,
    private val conversationRepository: ConversationRepository,
    private val providerConfigDao: ProviderConfigDao
) : ViewModel() {

    data class UiState(
        val messages: List<ChatMessage> = emptyList(),
        val streamingText: String = "",
        val isStreaming: Boolean = false,
        val currentProviderId: String = "",
        val sessionId: String = "",
        val handoffToast: HandoffEvent? = null,
        val error: String? = null,
        val inputText: String = "",
        // Provider picker
        val availableProviders: List<ProviderConfigEntity> = emptyList(),
        val selectedProviderId: String = "",
        val selectedProviderName: String = "",
        val showProviderPicker: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var activeSessionId: String = ""

    init {
        // Observe providers so we react even when DB seeding finishes after ViewModel creation
        viewModelScope.launch {
            providerConfigDao.observeAll()
                .map { all -> all.filter { it.enabled } }
                .collect { enabled ->
                    _uiState.update { it.copy(availableProviders = enabled) }

                    // Auto-create initial session once providers are available
                    if (activeSessionId.isEmpty() && enabled.isNotEmpty()) {
                        val provider = enabled.firstOrNull { it.id == _uiState.value.selectedProviderId }
                            ?: enabled.first()
                        createSession(provider)
                    }
                }
        }
    }

    private suspend fun createSession(provider: ProviderConfigEntity) {
        activeSessionId = conversationRepository.createSession(
            projectId = null,
            providerId = provider.id,
            modelId = provider.selectedModel
        )
        _uiState.update {
            it.copy(
                sessionId = activeSessionId,
                selectedProviderId = provider.id,
                selectedProviderName = provider.name
            )
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun selectProvider(provider: ProviderConfigEntity) {
        _uiState.update {
            it.copy(
                selectedProviderId = provider.id,
                selectedProviderName = provider.name,
                showProviderPicker = false
            )
        }
    }

    fun toggleProviderPicker() {
        _uiState.update { it.copy(showProviderPicker = !it.showProviderPicker) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank() || _uiState.value.isStreaming) return

        viewModelScope.launch {
            if (activeSessionId.isEmpty()) {
                val providers = _uiState.value.availableProviders
                if (providers.isEmpty()) {
                    _uiState.update { it.copy(error = "Keine Provider konfiguriert. Bitte API-Keys in Providers hinterlegen.") }
                    return@launch
                }
                val provider = providers.firstOrNull { it.id == _uiState.value.selectedProviderId }
                    ?: providers.first()
                createSession(provider)
            }

            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                role = "user",
                content = text,
                timestamp = System.currentTimeMillis()
            )
            conversationRepository.saveMessage(activeSessionId, "user", text)
            val allMessages = _uiState.value.messages + userMsg

            _uiState.update {
                it.copy(
                    messages = allMessages,
                    inputText = "",
                    isStreaming = true,
                    streamingText = "",
                    handoffToast = null,
                    error = null
                )
            }

            val preferredId = _uiState.value.selectedProviderId.ifEmpty { null }
            chatUseCase.chat(allMessages, activeSessionId, preferredId).collect { result ->
                when (result) {
                    is ChatUseCase.ChatResult.Token ->
                        _uiState.update {
                            it.copy(
                                streamingText = it.streamingText + result.text,
                                currentProviderId = result.providerId,
                                selectedProviderId = result.providerId,
                                selectedProviderName = it.availableProviders
                                    .find { p -> p.id == result.providerId }?.name
                                    ?: result.providerId
                            )
                        }

                    is ChatUseCase.ChatResult.Handoff ->
                        _uiState.update { it.copy(handoffToast = result.event) }

                    is ChatUseCase.ChatResult.Complete -> {
                        val assistantMsg = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            role = "assistant",
                            content = _uiState.value.streamingText,
                            timestamp = System.currentTimeMillis(),
                            providerId = result.providerId
                        )
                        _uiState.update {
                            it.copy(
                                messages = it.messages + assistantMsg,
                                streamingText = "",
                                isStreaming = false
                            )
                        }
                    }

                    is ChatUseCase.ChatResult.Error ->
                        _uiState.update {
                            it.copy(
                                isStreaming = false,
                                streamingText = "",
                                error = result.message
                            )
                        }
                }
            }
        }
    }

    fun newSession() {
        viewModelScope.launch {
            val providers = _uiState.value.availableProviders
            if (providers.isEmpty()) {
                _uiState.update { it.copy(error = "Keine Provider verfügbar. Bitte API-Keys hinterlegen.") }
                return@launch
            }
            val provider = providers.firstOrNull { it.id == _uiState.value.selectedProviderId }
                ?: providers.first()
            createSession(provider)
            _uiState.update {
                it.copy(
                    messages = emptyList(),
                    streamingText = "",
                    isStreaming = false,
                    error = null
                )
            }
        }
    }

    fun dismissHandoffToast() = _uiState.update { it.copy(handoffToast = null) }
    fun dismissError() = _uiState.update { it.copy(error = null) }
}
