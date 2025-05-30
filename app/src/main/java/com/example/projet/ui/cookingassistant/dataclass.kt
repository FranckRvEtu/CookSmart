package com.example.projet.ui.cookingassistant

import com.example.projet.ui.recipedetail.Recipe

// Data classes
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: MessageType = MessageType.TEXT
)

enum class MessageType {
    TEXT, STEP_INTRODUCTION, STEP_COMPLETION
}

data class CookingStep(
    val stepNumber: Int,
    val instruction: String,
    val estimatedTime: String? = null,
    val tips: List<String> = emptyList()
)

sealed class AiAssistantUiState {
    object Loading : AiAssistantUiState()
    data class Ready(
        val recipe: Recipe,
        val currentStep: Int,
        val messages: List<ChatMessage>,
        val isListening: Boolean = false,
        val isProcessing: Boolean = false,
        val cookingSteps: List<CookingStep>
    ) : AiAssistantUiState()
    data class Error(val message: String) : AiAssistantUiState()
}