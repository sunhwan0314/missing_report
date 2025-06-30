package com.example.awstestapp.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.awstestapp.ui.viewmodel.ChatViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatScreen(navController: NavController, viewModel: ChatViewModel = koinViewModel()) {
    Text(text = "여기는 채팅 화면입니다.")
}