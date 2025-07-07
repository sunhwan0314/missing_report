package com.example.awstestapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.awstestapp.ui.navigation.Screen
import com.example.awstestapp.ui.viewmodel.MyProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    mainNavController: NavController,
    viewModel: MyProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

    LaunchedEffect(key1 = isLoggedIn) {
        if (isLoggedIn) {
            viewModel.fetchMyData()
        }
    }

    if (isLoggedIn) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("내 프로필") }
                )
            }
        ) { paddingValues ->
            if (uiState.isLoading && uiState.user == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                Column(Modifier.fillMaxSize().padding(paddingValues)) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("안녕하세요, ${uiState.user?.nickname ?: ""}님", style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.height(32.dp))
                            Text("내가 쓴 글 목록", style = MaterialTheme.typography.titleLarge)
                        }

                        items(uiState.myPosts) { post ->
                            FullPostListItem(
                                post = post,
                                onClick = {
                                    // [수정] post.type을 사용하여 정확한 경로를 만듭니다.
                                    val postType = post.type ?: if (post.personName != null) "person" else "animal"
                                    mainNavController.navigate(Screen.Detail.createRoute(postType, post.id))
                                }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            mainNavController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Main.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("로그아웃")
                    }
                }
            }
        }
    } else {
        // 비로그인 화면
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("로그인이 필요한 서비스입니다.")
            Spacer(Modifier.height(16.dp))
            Button(onClick = { mainNavController.navigate(Screen.Login.route) }) { Text("로그인 / 회원가입") }
        }
    }
}