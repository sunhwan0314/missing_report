package com.example.awstestapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.awstestapp.data.remote.dto.PostListItemDto
import com.example.awstestapp.ui.navigation.Screen
import com.example.awstestapp.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel() // Koin으로 ViewModel 주입
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
// [새로 추가] HomeScreen의 생명주기를 관찰하는 부분
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // 화면이 다시 활성화될 때 (onResume) 데이터를 새로고침합니다.
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchHomeData()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        // 화면이 사라질 때 observer를 정리합니다.
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 1. 로딩 중일 때
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }
        // 2. 에러가 발생했을 때
        else if (uiState.errorMessage != null) {
            Text(text = "데이터를 불러오는데 실패했습니다: ${uiState.errorMessage}")
        }
        // 3. 성공적으로 데이터를 불러왔을 때
        else {
            // LazyColumn은 화면에 보이는 부분만 렌더링하여 성능을 최적화합니다.
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                // "최근 실종자" 섹션
                item {
                    Text("최근 실종자", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
                }
                items(uiState.missingPersons) { person ->
                    PostCard(post = person, onClick = {
                        // "person" 타입의 "person.id" 게시물로 이동하라고 명령
                        navController.navigate(Screen.Detail.createRoute("person", person.id))
                    })
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // "최근 실종 동물" 섹션
                item {
                    Text("최근 실종 동물", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
                }
                items(uiState.missingAnimals) { animal ->
                    PostCard(post = animal, onClick = {
                        // "animal" 타입의 "animal.id" 게시물로 이동하라고 명령
                        navController.navigate(Screen.Detail.createRoute("animal", animal.id))
                    })
                }
            }
        }
    }
}

// 각 게시물을 보여주는 카드 UI
@Composable
fun PostCard(post: PostListItemDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick), // Card 전체를 클릭할 수 있도록 수정
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 이름 표시 (사람 이름이 없으면 동물 이름 표시)
            Text(
                text = post.personName ?: post.animalName ?: "이름 없음",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "마지막 목격 장소: ${post.last_seen_location}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}