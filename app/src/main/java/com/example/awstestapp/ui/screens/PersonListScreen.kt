package com.example.awstestapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.awstestapp.ui.viewmodel.PostListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PersonListScreen(
    navController: NavController,
    viewModel: PostListViewModel = koinViewModel()
) {
    // 화면이 처음 그려질 때, '사람' 타입의 게시물을 불러오도록 요청합니다.
    LaunchedEffect(key1 = Unit) {
        viewModel.loadPosts("person")
    }
    // 공통 목록 UI를 재사용합니다.
    CommonPostListScreen(navController = navController, viewModel = viewModel)
}