package com.example.awstestapp.data.repository

import com.example.awstestapp.data.remote.dto.ChatMessageDto
import com.example.awstestapp.data.remote.dto.ChatRoomDto
import com.example.awstestapp.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatRepositoryImpl(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : ChatRepository {

    // TODO: 실제 채팅방 목록을 가져오는 로직 구현 필요
    override fun getMyChatRooms(userId: String): Flow<Result<List<ChatRoomDto>>> = callbackFlow {
        // 임시로 빈 목록을 반환
        trySend(Result.success(emptyList()))
        awaitClose { }
    }

    // 특정 채팅방의 메시지들을 실시간으로 가져옴
    override fun getChatMessages(roomId: String): Flow<Result<List<ChatMessageDto>>> = callbackFlow {
        val messagesRef = database.getReference("chats").child(roomId).child("messages")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(ChatMessageDto::class.java) }
                trySend(Result.success(messages))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        messagesRef.addValueEventListener(listener)

        // Flow가 취소될 때 리스너를 제거하여 메모리 누수 방지
        awaitClose { messagesRef.removeEventListener(listener) }
    }

    // 새로운 메시지를 DB에 저장
    override suspend fun sendMessage(roomId: String, message: ChatMessageDto): Result<Unit> = suspendCoroutine { continuation ->
        val messagesRef = database.getReference("chats").child(roomId).child("messages")
        val newMessageRef = messagesRef.push() // 고유한 키 생성

        val messageWithId = message.copy(messageId = newMessageRef.key ?: "")

        newMessageRef.setValue(messageWithId)
            .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
            .addOnFailureListener { continuation.resume(Result.failure(it)) }
    }

    // TODO: 새로운 채팅방을 만드는 로직 구현 필요
    override suspend fun createChatRoom(postOwnerId: String): Result<String> {
        return Result.success("temp_room_id") // 임시 채팅방 ID 반환
    }
}