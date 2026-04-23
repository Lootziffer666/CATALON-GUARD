package com.catalon.guard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catalon.guard.data.local.db.dao.ProviderConfigDao
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
        val inputText: String = ""
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var activeSessionId: String = ""

    init {
        viewModelScope.launch {
            val providers = providerConfigDao.getEnabledProviders()
            val defaultProvider = providers.firstOrNull()
            if (defaultProvider != null) {
                activeSessionId = conversationRepository.createSession(
                    projectId = null,
                    providerId = defaultProvider.id,
                    modelId = defaultProvider.selectedModel
                )
                _uiState.update { it.copy(sessionId = activeSessionId) }
            }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank() || _uiState.value.isStreaming) return

        viewModelScope.launch {
            if (activeSessionId.isEmpty()) {
                _uiState.update { it.copy(error = "No provider configured. Please add API keys in Settings.") }
                return@launch
            }

            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                role = "user",
                content = text,
                timestamp = System.currentTimeMillis()
            )
            conversationRepository.saveMessage(activeSessionId, "user", text)
            val allMessages = _uiState.value.messages + userMsg

            _uiState.update { it.copy(
                messages = allMessages,
                inputText = "",
                isStreaming = true,
                streamingText = "",
                handoffToast = null,
                error = null
            )}

            chatUseCase.chat(allMessages, activeSessionId).collect { result ->
                when (result) {
                    is ChatUseCase.ChatResult.Token ->
                        _uiState.update { it.copy(
                            streamingText = it.streamingText + result.text,
                            currentProviderId = result.providerId
                        )}

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
                        _uiState.update { it.copy(
                            messages = it.messages + assistantMsg,
                            streamingText = "",
                            isStreaming = false
                        )}
                    }

                    is ChatUseCase.ChatResult.Error ->
                        _uiState.update { it.copy(
                            isStreaming = false,
                            streamingText = "",
                            error = result.message
                        )}
                }
            }
        }
    }

    fun dismissHandoffToast() = _uiState.update { it.copy(handoffToast = null) }
    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun newSession() {
        viewModelScope.launch {
            val providers = providerConfigDao.getEnabledProviders()
            val provider = providers.firstOrNull() ?: return@launch
            activeSessionId = conversationRepository.createSession(
                projectId = null,
                providerId = provider.id,
                modelId = provider.selectedModel
            )
            _uiState.update { it.copy(
                messages = emptyList(),
                streamingText = "",
                isStreaming = false,
                sessionId = activeSessionId,
                error = null
            )}
        }
    }
}
