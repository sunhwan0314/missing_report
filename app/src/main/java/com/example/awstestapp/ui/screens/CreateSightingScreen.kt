package com.example.awstestapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.awstestapp.data.remote.dto.CreateSightingRequestDto
import com.example.awstestapp.ui.viewmodel.CreateSightingViewModel
import org.koin.androidx.compose.getViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSightingScreen(
    navController: NavController,
    postType: String,
    postId: Int
) {
    val viewModel: CreateSightingViewModel = getViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(key1 = uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "제보가 등록되었습니다.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("목격담 작성") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("목격 장소") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("상세 설명") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val currentTime = sdf.format(Date())
                    val sightingData = CreateSightingRequestDto(
                        sighting_at = currentTime,
                        sighting_location = location,
                        description = description,
                        sighting_photo_url = null // TODO: 사진 업로드 기능 추가
                    )
                    viewModel.createSighting(sightingData)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) CircularProgressIndicator() else Text("제보 등록")
            }
        }
    }
}