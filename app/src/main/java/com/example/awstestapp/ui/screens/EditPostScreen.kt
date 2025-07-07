package com.example.awstestapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.awstestapp.data.remote.dto.CreatePersonRequestDto
import com.example.awstestapp.ui.viewmodel.EditPostViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    navController: NavController,
    viewModel: EditPostViewModel = getViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- 입력 폼의 상태를 관리하는 변수들 ---
    // ViewModel로부터 초기 데이터를 받아와서 remember 상태로 관리
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    // ... 다른 필드들도 모두 추가 ...

    // ViewModel이 기존 데이터를 성공적으로 불러왔을 때, UI 상태 변수를 업데이트
    LaunchedEffect(key1 = uiState.post) {
        uiState.post?.let { post ->
            name = post.personName ?: post.animalName ?: ""
            location = post.last_seen_location
            description = post.description ?: ""
            // ... 다른 필드들도 모두 채워주기 ...
        }
    }

    // 수정 성공 시, 이전 화면으로 돌아가는 로직
    LaunchedEffect(key1 = uiState.isUpdateSuccess) {
        if (uiState.isUpdateSuccess) {
            Toast.makeText(context, "게시물이 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    // 에러 메시지 표시
    LaunchedEffect(key1 = uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("게시물 수정") }) }
    ) { innerPadding ->
        // 최초 데이터 로딩 중일 때 로딩 아이콘 표시
        if (uiState.isLoading && uiState.post == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 이름, 장소, 설명 등을 입력하는 UI (CreatePostScreen과 동일)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("이름") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("마지막 목격 장소") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("상세 설명") },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
                // ... 다른 필드들도 여기에 추가 ...

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        // TODO: 현재는 사람 게시물 수정만 구현, 동물/사람 타입에 따라 분기 처리 필요
                        val updatedData = CreatePersonRequestDto(
                            missing_person_name = name,
                            last_seen_location = location,
                            description = description,
                            // 기존 데이터 또는 수정된 데이터로 채우기
                            last_seen_at = uiState.post?.last_seen_at ?: "",
                            gender = uiState.post?.gender,
                            age_at_missing = uiState.post?.ageAtMissing,
                            height = uiState.post?.height,
                            weight = uiState.post?.weight,
                            main_photo_url = uiState.post?.main_photo_url
                        )
                        viewModel.updatePost(updatedData)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 수정을 요청하는 동안 로딩 표시
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("수정 완료")
                    }
                }
            }
        }
    }
}