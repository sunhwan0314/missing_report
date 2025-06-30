package com.example.awstestapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.awstestapp.data.remote.dto.CreateAnimalRequestDto
import com.example.awstestapp.data.remote.dto.CreatePersonRequestDto
import com.example.awstestapp.ui.viewmodel.CreatePostViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    viewModel: CreatePostViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- UI의 각 입력 필드 상태를 관리하는 변수들 ---
    val postTypes = listOf("사람", "동물")
    var selectedPostType by remember { mutableStateOf(postTypes[0]) }

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    // ... 다른 필드들도 필요에 따라 remember로 추가 가능 ...

    // 등록 성공 시, 이전 화면으로 돌아가는 로직
    LaunchedEffect(key1 = uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "게시물이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("새 게시물 작성") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- UI 요소들 ---

            // 1. 사람/동물 선택 라디오 버튼
            Text("게시물 종류", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth()) {
                postTypes.forEach { type ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (type == selectedPostType),
                                onClick = { selectedPostType = type }
                            )
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (type == selectedPostType),
                            onClick = { selectedPostType = type }
                        )
                        Text(text = type, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 2. 텍스트 입력 필드들
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("이름") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("나이") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("마지막 목격 장소") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("상세 설명 (인상착의 등)") }, modifier = Modifier.fillMaxWidth().height(150.dp))

            // 3. 사진 업로드 버튼 (기능은 추후 연결)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* TODO: 갤러리 열기 */ }, modifier = Modifier.fillMaxWidth()) {
                Text("사진 추가하기")
            }


            // 4. 등록하기 버튼
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val currentTime = sdf.format(Date())

                    if (selectedPostType == "사람") {
                        val personData = CreatePersonRequestDto(
                            missing_person_name = name,
                            age_at_missing = age.toIntOrNull(),
                            last_seen_location = location,
                            description = description,
                            last_seen_at = currentTime, // 현재 시간으로 임시 설정
                            gender = null, height = null, weight = null, main_photo_url = null
                        )
                        viewModel.createPersonPost(personData)
                    } else {
                        // TODO: 동물 데이터(CreateAnimalRequestDto)로 동물 등록 API 호출
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading // 로딩 중일 때는 버튼 비활성화
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("등록하기")
                }
            }
        }
    }
}