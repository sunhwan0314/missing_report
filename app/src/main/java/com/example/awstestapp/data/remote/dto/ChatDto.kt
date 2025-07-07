package com.example.awstestapp.data.remote.dto

// 채팅방 목록에 표시될 정보
data class ChatRoomDto(
    val roomId: String = "", // 채팅방 고유 ID
    val postTitle: String = "", // 관련 게시물 제목
    val otherUserName: String = "", // 상대방 이름
    val lastMessage: String = "", // 마지막 메시지
    val lastTimestamp: Long = 0L // 마지막 메시지 시간
)

// 채팅방 안의 개별 메시지 정보
data class ChatMessageDto(
    val messageId: String = "", // 메시지 고유 ID
    val senderId: String = "", // 보낸 사람의 UID
    val message: String = "",
    val timestamp: Long = 0L
)