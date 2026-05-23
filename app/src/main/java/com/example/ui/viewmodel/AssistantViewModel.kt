package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatMessage
import com.example.data.local.ChatSession
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class AssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChatRepository(application)

    // All available sessions
    val sessions: StateFlow<List<ChatSession>> = repository.getAllSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    // Observe messages for current session
    val messages: StateFlow<List<ChatMessage>> = _currentSessionId
        .flatMapLatest { sessionId ->
            if (sessionId != null) {
                repository.getMessagesForSession(sessionId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Initialize with default session if none exists
        viewModelScope.launch {
            val initialSessions = repository.getAllSessions().first()
            if (initialSessions.isEmpty()) {
                val defaultId = UUID.randomUUID().toString()
                repository.createSession(defaultId, "💬 Asif's Lounge")
                _currentSessionId.value = defaultId
            } else if (_currentSessionId.value == null) {
                _currentSessionId.value = initialSessions.first().id
            }
        }
    }

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun selectSession(sessionId: String) {
        _currentSessionId.value = sessionId
    }

    fun createNewSession(title: String) {
        viewModelScope.launch {
            val newId = UUID.randomUUID().toString()
            repository.createSession(newId, title)
            _currentSessionId.value = newId
        }
    }

    fun deleteSession(session: ChatSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
            if (_currentSessionId.value == session.id) {
                _currentSessionId.value = sessions.value.firstOrNull()?.id
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun sendMessage() {
        val textToSend = _inputText.value.trim()
        val sessionId = _currentSessionId.value
        if (textToSend.isEmpty() || sessionId == null || _isGenerating.value) return

        _inputText.value = ""
        _isGenerating.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = repository.sendMessage(sessionId, textToSend)
            result.onFailure { exception ->
                _errorMessage.value = exception.localizedMessage ?: "Failed to get reply."
            }
            _isGenerating.value = false
        }
    }

    fun sendPromptSuggestion(suggestion: String) {
        val sessionId = _currentSessionId.value ?: return
        if (_isGenerating.value) return

        _isGenerating.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = repository.sendMessage(sessionId, suggestion)
            result.onFailure { exception ->
                _errorMessage.value = exception.localizedMessage ?: "Failed to get reply."
            }
            _isGenerating.value = false
        }
    }
}
