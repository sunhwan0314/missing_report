package com.example.awstestapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.awstestapp.data.remote.dto.PostDetailDto
import com.example.awstestapp.data.remote.dto.SightingDto
import com.example.awstestapp.ui.navigation.Screen
import com.example.awstestapp.ui.viewmodel.DetailViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController) {
    val viewModel: DetailViewModel = getViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = uiState.isPostDeleted) {
        if (uiState.isPostDeleted) {
            navController.popBackStack()
        }
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.loadData()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("상세 정보") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    if (uiState.isOwner) {
                        IconButton(onClick = {
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(
                        Screen.CreateSighting.createRoute(
                            postType = viewModel.postType,
                            postId = viewModel.postId
                        )
                    )
                },
                icon = { Icon(Icons.Filled.Edit, contentDescription = "제보하기") },
                text = { Text("목격담 제보하기") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.errorMessage != null) {
                Text(text = "오류: ${uiState.errorMessage}")
            } else if (uiState.post != null) {
                // [수정] 이제 화면 전체가 하나의 스크롤 목록입니다.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // --- 1. 게시물 상세 정보 섹션 ---
                    item {
                        PostDetailContent(post = uiState.post!!)
                    }

                    // --- 2. 목격담 목록 섹션 ---
                    item {
                        Divider(modifier = Modifier.padding(vertical = 24.dp))
                        Text("목격담 목록", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (uiState.sightings.isEmpty()) {
                        item { Text("등록된 목격담이 없습니다.") }
                    } else {
                        items(uiState.sightings) { sighting ->
                            SightingCard(sighting = sighting)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

// 게시물 상세 정보를 그리는 Composable
@Composable
fun PostDetailContent(post: PostDetailDto) {
    Column {
        if (post.main_photo_url != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(post.main_photo_url).crossfade(true).build(),
                contentDescription = "게시물 사진",
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

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

// 각 목격담을 보여주는 카드 UI
@Composable
fun SightingCard(sighting: SightingDto) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${sighting.reporter_nickname ?: "익명"}님의 제보",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            DetailInfoRow(label = "목격 장소", value = sighting.sighting_location)
            DetailInfoRow(label = "목격 시간", value = sighting.sighting_at)
            if (!sighting.description.isNullOrEmpty()) {
                Text(text = sighting.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
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