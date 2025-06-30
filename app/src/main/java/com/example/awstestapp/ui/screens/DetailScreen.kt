package com.example.awstestapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.awstestapp.ui.navigation.Screen
import com.example.awstestapp.ui.viewmodel.DetailViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController) {
    val viewModel: DetailViewModel = getViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // 삭제 성공 시, 이전 화면으로 돌아가는 로직
    LaunchedEffect(key1 = uiState.isPostDeleted) {
        if (uiState.isPostDeleted) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("상세 정보") },
                // [수정] isOwner가 true일 때만 수정/삭제 아이콘을 보여줌
                actions = {
                    if (uiState.isOwner) {
                        IconButton(onClick = {
                            // 2. ViewModel이 가지고 있는 postId와 postType을 사용합니다.
                            navController.navigate(
                                Screen.EditPost.createRoute(
                                    postType = viewModel.postType,
                                    postId = viewModel.postId
                                )
                            )
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "수정")
                        }
                        IconButton(onClick = { viewModel.deletePost() }) {
                            Icon(Icons.Filled.Delete, contentDescription = "삭제")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.errorMessage != null) {
                Text(text = "오류: ${uiState.errorMessage}")
            } else if (uiState.post != null) {
                // post가 null이 아님이 확실하므로, !! 연산자 대신 스마트 캐스트를 사용합니다.
                val post = uiState.post!!
                // 스크롤이 가능하도록 Column을 수정합니다.
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // 1. 이미지 표시 (Coil 사용)
                    if (post.main_photo_url != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(post.main_photo_url)
                                .crossfade(true)
                                .build(),
                            contentDescription = "실종자/동물 사진",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 2. 텍스트 정보 표시
                    Text(
                        text = post.personName ?: post.animalName ?: "이름 정보 없음",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    DetailInfoRow(label = "상태", value = post.status)
                    DetailInfoRow(label = "종류", value = post.animal_type)
                    DetailInfoRow(label = "품종", value = post.breed)
                    DetailInfoRow(label = "성별", value = post.gender)
                    DetailInfoRow(label = "나이", value = (post.age ?: post.ageAtMissing)?.toString())
                    DetailInfoRow(label = "키", value = post.height?.let { "${it}cm" })
                    DetailInfoRow(label = "몸무게", value = post.weight?.let { "${it}kg" })

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    DetailInfoRow(label = "마지막 목격 장소", value = post.last_seen_location)
                    DetailInfoRow(label = "마지막 목격 시간", value = post.last_seen_at)

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    Text("상세 설명", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(post.description ?: "내용 없음", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// 상세 정보를 라벨과 값으로 보여주는 Helper Composable
@Composable
private fun DetailInfoRow(label: String, value: String?) {
    // 값이 null이 아닐 때만 행을 보여줍니다.
    if (value != null) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(120.dp)
            )
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}