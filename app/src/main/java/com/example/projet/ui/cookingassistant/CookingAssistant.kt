package com.example.projet.ui.cookingassistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
//import com.example.projet.ui.cookingassistant.AiCookingAssistantViewModel
/*
// UI Screen
@Composable
fun AiCookingAssistantScreen(
    recipeId: Int,
    viewModel: AiCookingAssistantViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.initializeAssistant(recipeId)
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = {
                when (val state = uiState) {
                    is AiAssistantUiState.Ready -> {
                        Column {
                            Text("AI Cooking Assistant")
                            Text(
                                text = "Step ${state.currentStep + 1} of ${state.cookingSteps.size}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    else -> Text("AI Cooking Assistant")
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        when (val state = uiState) {
            is AiAssistantUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AiAssistantUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is AiAssistantUiState.Ready -> {
                AiAssistantContent(
                    state = state,
                    onSendMessage = viewModel::sendMessage,
                    onNextStep = viewModel::nextStep,
                    onStartListening = viewModel::startListening,
                    onStopListening = viewModel::stopListening,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun AiAssistantContent(
    state: AiAssistantUiState.Ready,
    onSendMessage: (String) -> Unit,
    onNextStep: () -> Unit,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(modifier = modifier) {
        // Progress indicator
        LinearProgressIndicator(
            progress = (state.currentStep + 1) / state.cookingSteps.size.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.messages) { message ->
                MessageBubble(message = message)
            }

            if (state.isProcessing) {
                item {
                    TypingIndicator()
                }
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Next Step Button
            if (state.currentStep < state.cookingSteps.size) {
                Button(
                    onClick = onNextStep,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.NavigateNext, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (state.currentStep == state.cookingSteps.size - 1)
                            "Complete Recipe"
                        else
                            "Next Step"
                    )
                }
            }

            // Input row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask me anything...") },
                    maxLines = 3
                )

                // Voice button
                IconButton(
                    onClick = {
                        if (state.isListening) onStopListening() else onStartListening()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (state.isListening)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                        )
                ) {
                    Icon(
                        if (state.isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (state.isListening) "Stop listening" else "Start listening",
                        tint = Color.White
                    )
                }

                // Send button
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText)
                            messageText = ""
                        }
                    },
                    enabled = messageText.isNotBlank(),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (message.isFromUser) Spacer(modifier = Modifier.width(48.dp))

        Card(
            modifier = Modifier.fillMaxWidth(if (message.isFromUser) 0.8f else 1f),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                bottomEnd = if (message.isFromUser) 4.dp else 16.dp
            )
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                color = if (message.isFromUser)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                style = if (message.messageType == MessageType.STEP_INTRODUCTION)
                    MaterialTheme.typography.bodyLarge
                else
                    MaterialTheme.typography.bodyMedium
            )
        }

        if (!message.isFromUser) Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(3) { index ->
            var alpha by remember { mutableStateOf(0.3f) }

            LaunchedEffect(Unit) {
                while (true) {
                    delay(300L * index)
                    alpha = 1f
                    delay(300)
                    alpha = 0.3f
                    delay(600)
                }
            }

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "AI is thinking...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

 */