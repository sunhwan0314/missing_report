package com.example.awstestapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.awstestapp.ui.navigation.Screen
import com.example.awstestapp.ui.viewmodel.ChatViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    viewModel: ChatViewModel = koinViewModel()
) {
    val uiState by viewModel.chatListUiState.collectAsState()

    // 화면이 처음 보일 때 채팅방 목록을 불러옵니다.
    LaunchedEffect(Unit) {
        viewModel.loadMyChatRooms()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("채팅 목록") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.chatRooms.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("채팅방이 없습니다.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        // 'test_room_01' 이라는 ID를 가진 채팅방으로 강제 이동
                        navController.navigate(Screen.ChatRoom.createRoute("test_room_01"))
                    }) {
                        Text("임시 테스트 채팅방 들어가기")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(uiState.chatRooms) { room ->
                        ChatRoomItem(room = room, onClick = {
                            navController.navigate(Screen.ChatRoom.createRoute(room.roomId))
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ChatRoomItem(room: com.example.awstestapp.data.remote.dto.ChatRoomDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = room.otherUserName, style = MaterialTheme.typography.titleMedium)
            Text(text = room.lastMessage, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        }
    }
}