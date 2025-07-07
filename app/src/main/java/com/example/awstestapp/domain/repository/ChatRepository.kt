package com.example.awstestapp.domain.repository

import com.example.awstestapp.data.remote.dto.ChatMessageDto
import com.example.awstestapp.data.remote.dto.ChatRoomDto
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    // 내 채팅방 목록을 실시간으로 가져오는 기능
    fun getMyChatRooms(userId: String): Flow<Result<List<ChatRoomDto>>>

    // 특정 채팅방의 메시지들을 실시간으로 가져오는 기능
    fun getChatMessages(roomId: String): Flow<Result<List<ChatMessageDto>>>

    // 새로운 메시지를 보내는(저장하는) 기능
    suspend fun sendMessage(roomId: String, message: ChatMessageDto): Result<Unit>

    // (선택) 새로운 채팅방을 만드는 기능
    suspend fun createChatRoom(postOwnerId: String): Result<String>
}