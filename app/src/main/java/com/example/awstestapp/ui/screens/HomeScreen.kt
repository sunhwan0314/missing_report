package com.example.awstestapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.awstestapp.data.remote.dto.PostListItemDto
import com.example.awstestapp.ui.navigation.Screen
import com.example.awstestapp.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()

) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.errorMessage != null) {
            Text(text = "데이터를 불러오는데 실패했습니다: ${uiState.errorMessage}")
        } else {
            // 세로 스크롤이 가능한 Column으로 전체를 감쌉니다.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // "최근 실종자" 대시보드 섹션
                DashboardSection(
                    title = "최근 실종자",
                    items = uiState.missingPersons,
                    onMoreClicked = { navController.navigate(Screen.PersonList.route) },
                    onItemClicked = { personId ->
                        navController.navigate(Screen.Detail.createRoute("person", personId))
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // "최근 실종 동물" 대시보드 섹션
                DashboardSection(
                    title = "최근 실종 동물",
                    items = uiState.missingAnimals,
                    onMoreClicked = { navController.navigate(Screen.AnimalList.route) },
                    onItemClicked = { animalId ->
                        navController.navigate(Screen.Detail.createRoute("animal", animalId))
                    }
                )
            }
        }
    }
}

// 대시보드의 각 섹션을 그리는 Composable
@Composable
fun DashboardSection(
    title: String,
    items: List<PostListItemDto>,
    onMoreClicked: () -> Unit,
    onItemClicked: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // 섹션 헤더 (제목 + 더보기 버튼)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onMoreClicked) {
                Text("더보기 >")
            }
        }

        // 가로 스크롤 목록
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(items) { post ->
                // 미리보기용 작은 카드
                PreviewPostCard(post = post, onClick = { onItemClicked(post.id) })
            }
        }
    }
}

// 미리보기용 작은 카드 UI
@Composable
fun PreviewPostCard(post: PostListItemDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp) // 카드의 가로 크기 지정
            .clickable(onClick = onClick)
    ) {
        Column {
            // TODO: 여기에 Coil을 사용한 이미지 추가
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("사진 영역", modifier = Modifier.align(Alignment.Center))
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = post.personName ?: post.animalName ?: "이름 없음",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.last_seen_location,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}