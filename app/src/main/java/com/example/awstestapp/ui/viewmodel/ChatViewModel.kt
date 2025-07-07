package com.example.awstestapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awstestapp.data.remote.dto.ChatMessageDto
import com.example.awstestapp.data.remote.dto.ChatRoomDto
import com.example.awstestapp.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 채팅방 목록 UI 상태
data class ChatListUiState(
    val isLoading: Boolean = true,
    val chatRooms: List<ChatRoomDto> = emptyList(),
    val errorMessage: String? = null
)

// 개별 채팅방 UI 상태
data class ChatRoomUiState(
    val isLoading: Boolean = true,
    val messages: List<ChatMessageDto> = emptyList(),
    val errorMessage: String? = null
)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _chatListUiState = MutableStateFlow(ChatListUiState())
    val chatListUiState = _chatListUiState.asStateFlow()

    private val _chatRoomUiState = MutableStateFlow(ChatRoomUiState())
    val chatRoomUiState = _chatRoomUiState.asStateFlow()

    // 내 채팅방 목록 가져오기
    fun loadMyChatRooms() {
        val userId = auth.currentUser?.uid ?: return
        chatRepository.getMyChatRooms(userId)
            .onEach { result ->
                result.onSuccess { rooms ->
                    _chatListUiState.update { it.copy(isLoading = false, chatRooms = rooms) }
                }.onFailure { error ->
                    _chatListUiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            }.launchIn(viewModelScope)
    }

    // 특정 채팅방의 메시지 가져오기
    fun loadMessages(roomId: String) {
        chatRepository.getChatMessages(roomId)
            .onEach { result ->
                result.onSuccess { messages ->
                    _chatRoomUiState.update { it.copy(isLoading = false, messages = messages) }
                }.onFailure { error ->
                    _chatRoomUiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            }.launchIn(viewModelScope)
    }

    // 메시지 보내기
    fun sendMessage(roomId: String, messageText: String) {
        val senderId = auth.currentUser?.uid ?: return
        val message = ChatMessageDto(
            senderId = senderId,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            chatRepository.sendMessage(roomId, message)
                .onFailure { error ->
                    _chatRoomUiState.update { it.copy(errorMessage = "메시지 전송 실패: ${error.message}") }
                }
        }
    }
}