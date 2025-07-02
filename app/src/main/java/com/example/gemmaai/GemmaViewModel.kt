package com.example.gemmaai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GemmaViewModel(application: Application) : AndroidViewModel(application) {
    
    private val gemmaModel = GemmaModel(application)
    
    private val _response = MutableLiveData<String>()
    val response: LiveData<String> = _response
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _isModelReady = MutableLiveData<Boolean>()
    val isModelReady: LiveData<Boolean> = _isModelReady
    
    private val _initializationProgress = MutableLiveData<String>()
    val initializationProgress: LiveData<String> = _initializationProgress
    
    private val _conversationHistory = MutableLiveData<List<ChatMessage>>()
    val conversationHistory: LiveData<List<ChatMessage>> = _conversationHistory
    
    init {
        _conversationHistory.value = emptyList()
        initializeModel()
    }
    
    private fun initializeModel() {
        viewModelScope.launch {
            _isLoading.value = true
            _isModelReady.value = false
            _initializationProgress.value = "Starting Gemma initialization..."
            
            try {
                _initializationProgress.value = "Loading model file..."
                val success = gemmaModel.initializeModel()
                
                if (success) {
                    _isModelReady.value = true
                    _initializationProgress.value = "Gemma is ready!"
                    addSystemMessage("Hello! I'm Gemma, your AI assistant. How can I help you today?")
                } else {
                    _initializationProgress.value = "Failed to initialize model. Please check if the model file exists in assets folder."
                    addSystemMessage("Sorry, I couldn't load properly. Please restart the app.")
                }
            } catch (e: Exception) {
                _initializationProgress.value = "Error: ${e.message}"
                addSystemMessage("Initialization error occurred.")
            }
            
            _isLoading.value = false
        }
    }
    
    fun sendMessage(message: String) {
        if (_isModelReady.value != true) {
            addSystemMessage("Please wait for the model to finish loading.")
            return
        }
        
        if (message.isBlank()) return
        
        // Add user message to conversation
        addUserMessage(message)
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val response = gemmaModel.generateResponse(message)
                addAssistantMessage(response)
            } catch (e: Exception) {
                addSystemMessage("Error generating response: ${e.message}")
            }
            
            _isLoading.value = false
        }
    }
    
    private fun addUserMessage(message: String) {
        val currentHistory = _conversationHistory.value ?: emptyList()
        _conversationHistory.value = currentHistory + ChatMessage(
            content = message,
            isUser = true,
            timestamp = System.currentTimeMillis()
        )
    }
    
    private fun addAssistantMessage(message: String) {
        val currentHistory = _conversationHistory.value ?: emptyList()
        _conversationHistory.value = currentHistory + ChatMessage(
            content = message,
            isUser = false,
            timestamp = System.currentTimeMillis()
        )
    }
    
    private fun addSystemMessage(message: String) {
        val currentHistory = _conversationHistory.value ?: emptyList()
        _conversationHistory.value = currentHistory + ChatMessage(
            content = message,
            isUser = false,
            isSystem = true,
            timestamp = System.currentTimeMillis()
        )
    }
    
    fun clearConversation() {
        _conversationHistory.value = emptyList()
        addSystemMessage("Conversation cleared. How can I help you?")
    }
    
    fun getModelStatus(): String {
        return gemmaModel.getModelStatus()
    }
    
    override fun onCleared() {
        super.onCleared()
        gemmaModel.cleanup()
    }
}

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val isSystem: Boolean = false,
    val timestamp: Long
)