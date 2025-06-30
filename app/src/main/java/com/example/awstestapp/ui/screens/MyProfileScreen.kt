package com.example.awstestapp.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.awstestapp.ui.viewmodel.MyProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyProfileScreen(mainNavController: NavController, viewModel: MyProfileViewModel = koinViewModel()) {
    // TODO: 로그아웃 로직 등을 이곳으로 옮겨야 함
    Text(text = "여기는 내 프로필 화면입니다.")
}