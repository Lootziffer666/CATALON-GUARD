package com.catalon.guard.presentation.ui.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.catalon.guard.domain.model.ChatMessage
import com.catalon.guard.domain.model.HandoffEvent
import com.catalon.guard.presentation.theme.*
import com.catalon.guard.presentation.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    paddingValues: PaddingValues,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.messages.size, state.streamingText) {
        if (state.messages.isNotEmpty() || state.streamingText.isNotEmpty()) {
            listState.animateScrollToItem(
                (state.messages.size + if (state.isStreaming) 1 else 0).coerceAtLeast(0)
            )
        }
    }

    LaunchedEffect(state.handoffToast) {
        state.handoffToast?.let { event ->
            snackbarHostState.showSnackbar(
                message = "Switched: ${event.fromProviderName} → ${event.toProviderName}",
                duration = SnackbarDuration.Short
            )
            viewModel.dismissHandoffToast()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Catalon Guard", fontWeight = FontWeight.Bold) },
                actions = {
                    if (state.currentProviderId.isNotEmpty()) {
                        ProviderBadge(state.currentProviderId)
                    }
                    IconButton(onClick = { viewModel.newSession() }) {
                        Icon(Icons.Default.Add, "New Chat")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.messages.isEmpty() && !state.isStreaming) {
                    item { WelcomeCard() }
                }
                items(state.messages) { msg ->
                    MessageBubble(msg)
                }
                if (state.isStreaming && state.streamingText.isNotEmpty()) {
                    item {
                        StreamingBubble(state.streamingText, state.currentProviderId)
                    }
                }
                if (state.isStreaming && state.streamingText.isEmpty()) {
                    item { ThinkingIndicator() }
                }
            }

            state.error?.let { error ->
                ErrorBanner(error) { viewModel.dismissError() }
            }

            ChatInputBar(
                text = state.inputText,
                onTextChange = { viewModel.onInputChanged(it) },
                onSend = { viewModel.sendMessage() },
                isStreaming = state.isStreaming
            )
        }
    }
}

@Composable
private fun ProviderBadge(providerId: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = providerId,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text("AI", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
        }
        Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = if (isUser) 16.dp else 4.dp,
                    topEnd = if (isUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = if (isUser) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!isUser && message.providerId != null) {
                Text(
                    text = message.providerId,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun StreamingBubble(text: String, providerId: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text("AI", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = "$text▊",
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ThinkingIndicator() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text("AI", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        LinearProgressIndicator(
            modifier = Modifier.padding(top = 12.dp).width(80.dp),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Welcome to Catalon Guard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Unified LLM router with automatic failover across 14+ providers.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("Add API keys in Settings → Providers to get started.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun ErrorBanner(error: String, onDismiss: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(8.dp))
            Text(error, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, "Dismiss")
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    isStreaming: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Message...") },
            maxLines = 5,
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        FloatingActionButton(
            onClick = { if (!isStreaming) onSend() },
            containerColor = if (isStreaming) MaterialTheme.colorScheme.outline
                            else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(if (isStreaming) Icons.Default.HourglassEmpty else Icons.Default.Send, "Send")
        }
    }
}
