package com.example.awstestapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.awstestapp.ui.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    navController: NavController,
    roomId: String,
    viewModel: ChatViewModel = koinViewModel()
) {
    val uiState by viewModel.chatRoomUiState.collectAsState()
    val listState = rememberLazyListState()
    val myUid = FirebaseAuth.getInstance().currentUser?.uid

    // 화면이 처음 열릴 때 해당 채팅방의 메시지를 불러옵니다.
    LaunchedEffect(roomId) {
        viewModel.loadMessages(roomId)
    }

    // 새 메시지가 오면 맨 아래로 자동 스크롤합니다.
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("$roomId 채팅방") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 메시지 목록 (화면의 남은 공간을 모두 차지)
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(uiState.messages) { message ->
                    MessageBubble(
                        message = message.message,
                        isMine = message.senderId == myUid
                    )
                }
            }

            // 메시지 입력 및 전송 영역
            MessageInput(onSendMessage = { messageText ->
                viewModel.sendMessage(roomId, messageText)
            })
        }
    }
}

@Composable
fun MessageBubble(message: String, isMine: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isMine) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(onSendMessage: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("메시지 입력...") }
        )
        IconButton(onClick = {
            if (text.isNotBlank()) {
                onSendMessage(text)
                text = "" // 메시지 전송 후 입력창 비우기
            }
        }) {
            Icon(Icons.Default.Send, contentDescription = "전송")
        }
    }
}