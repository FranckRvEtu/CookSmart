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
/*
// ViewModel
class AiCookingAssistantViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AiAssistantUiState>(AiAssistantUiState.Loading)
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    fun initializeAssistant(recipeId: Int) {
        viewModelScope.launch {
            try {
                // Load recipe data
                val recipe = loadRecipeById(recipeId)
                val cookingSteps = recipe.instructions.mapIndexed { index, instruction ->
                    CookingStep(
                        stepNumber = index + 1,
                        instruction = instruction,
                        estimatedTime = estimateStepTime(instruction),
                        tips = generateStepTips(instruction)
                    )
                }

                val welcomeMessage = ChatMessage(
                    content = "Hi! I'm your AI cooking assistant. I'll guide you through making ${recipe.title} step by step. Let's start with step 1!",
                    isFromUser = false
                )

                _uiState.value = AiAssistantUiState.Ready(
                    recipe = recipe,
                    currentStep = 0,
                    messages = listOf(welcomeMessage),
                    cookingSteps = cookingSteps
                )

                // Introduce first step
                introduceCurrentStep()

            } catch (e: Exception) {
                _uiState.value = AiAssistantUiState.Error("Failed to load recipe: ${e.message}")
            }
        }
    }

    fun sendMessage(message: String) {
        val currentState = _uiState.value as? AiAssistantUiState.Ready ?: return

        // Add user message
        val userMessage = ChatMessage(content = message, isFromUser = true)
        val updatedMessages = currentState.messages + userMessage

        _uiState.value = currentState.copy(
            messages = updatedMessages,
            isProcessing = true
        )

        // Process AI response
        viewModelScope.launch {
            delay(1000) // Simulate AI processing
            val aiResponse = generateAiResponse(message, currentState)
            val aiMessage = ChatMessage(content = aiResponse, isFromUser = false)

            _uiState.value = currentState.copy(
                messages = updatedMessages + aiMessage,
                isProcessing = false
            )
        }
    }

    fun nextStep() {
        val currentState = _uiState.value as? AiAssistantUiState.Ready ?: return

        if (currentState.currentStep < currentState.cookingSteps.size - 1) {
            val nextStepIndex = currentState.currentStep + 1
            _uiState.value = currentState.copy(currentStep = nextStepIndex)
            introduceCurrentStep()
        } else {
            // Recipe completed
            val completionMessage = ChatMessage(
                content = "🎉 Congratulations! You've completed ${currentState.recipe.title}. Enjoy your delicious meal!",
                isFromUser = false,
                messageType = MessageType.STEP_COMPLETION
            )
            _uiState.value = currentState.copy(
                messages = currentState.messages + completionMessage
            )
        }
    }

    fun startListening() {
        val currentState = _uiState.value as? AiAssistantUiState.Ready ?: return
        _uiState.value = currentState.copy(isListening = true)

        // TODO: Implement voice recognition
        viewModelScope.launch {
            delay(3000) // Simulate listening
            _uiState.value = currentState.copy(isListening = false)
        }
    }

    fun stopListening() {
        val currentState = _uiState.value as? AiAssistantUiState.Ready ?: return
        _uiState.value = currentState.copy(isListening = false)
    }

    private fun introduceCurrentStep() {
        val currentState = _uiState.value as? AiAssistantUiState.Ready ?: return
        val currentStep = currentState.cookingSteps[currentState.currentStep]

        val introMessage = buildString {
            append("**Step ${currentStep.stepNumber}:**\n\n")
            append(currentStep.instruction)
            currentStep.estimatedTime?.let { time ->
                append("\n\n⏱️ Estimated time: $time")
            }
            if (currentStep.tips.isNotEmpty()) {
                append("\n\n💡 **Tips:**")
                currentStep.tips.forEach { tip ->
                    append("\n• $tip")
                }
            }
            append("\n\nLet me know when you're ready or if you have any questions!")
        }

        val stepMessage = ChatMessage(
            content = introMessage,
            isFromUser = false,
            messageType = MessageType.STEP_INTRODUCTION
        )

        _uiState.value = currentState.copy(
            messages = currentState.messages + stepMessage
        )
    }

    private fun generateAiResponse(userMessage: String, currentState: AiAssistantUiState.Ready): String {
        val lowerMessage = userMessage.lowercase()

        return when {
            lowerMessage.contains("next") || lowerMessage.contains("continue") || lowerMessage.contains("done") -> {
                "Great! Let's move to the next step."
            }
            lowerMessage.contains("help") || lowerMessage.contains("how") -> {
                "I'm here to help! Could you be more specific about what you need assistance with in this step?"
            }
            lowerMessage.contains("time") || lowerMessage.contains("long") -> {
                val currentStep = currentState.cookingSteps[currentState.currentStep]
                currentStep.estimatedTime?.let { "This step should take about $it." }
                    ?: "This step timing can vary, take your time and let me know when you're ready!"
            }
            lowerMessage.contains("temperature") || lowerMessage.contains("heat") -> {
                "For this recipe, make sure to follow the temperature guidelines in the original instructions. If you need specific temperatures, let me know what you're cooking!"
            }
            lowerMessage.contains("ingredient") -> {
                "If you have questions about ingredients, I can help clarify measurements or suggest substitutions if needed!"
            }
            else -> {
                listOf(
                    "That's a great question! Let me help you with that step.",
                    "I understand your concern. Here's what I suggest for this step.",
                    "Good question! For this particular step, I recommend taking your time.",
                    "I'm here to help! Could you tell me more about what you're experiencing?",
                    "That's totally normal at this step. Keep going, you're doing great!"
                ).random()
            }
        }
    }

    private fun estimateStepTime(instruction: String): String? {
        val lowerInstruction = instruction.lowercase()
        return when {
            lowerInstruction.contains("mix") || lowerInstruction.contains("stir") -> "2-3 minutes"
            lowerInstruction.contains("chop") || lowerInstruction.contains("cut") -> "5-7 minutes"
            lowerInstruction.contains("cook") || lowerInstruction.contains("fry") -> "10-15 minutes"
            lowerInstruction.contains("bake") || lowerInstruction.contains("roast") -> "20-45 minutes"
            lowerInstruction.contains("boil") -> "5-10 minutes"
            lowerInstruction.contains("simmer") -> "15-30 minutes"
            else -> null
        }
    }

    private fun generateStepTips(instruction: String): List<String> {
        val lowerInstruction = instruction.lowercase()
        val tips = mutableListOf<String>()

        when {
            lowerInstruction.contains("chop") || lowerInstruction.contains("cut") -> {
                tips.add("Keep your fingers curled and use a sharp knife for safety")
                tips.add("Cut pieces uniformly for even cooking")
            }
            lowerInstruction.contains("mix") || lowerInstruction.contains("stir") -> {
                tips.add("Mix gently to avoid overmixing")
                tips.add("Scrape the sides of the bowl occasionally")
            }
            lowerInstruction.contains("heat") || lowerInstruction.contains("cook") -> {
                tips.add("Preheat your pan/oven for best results")
                tips.add("Don't overcrowd the cooking surface")
            }
        }

        return tips
    }
}

 */