package com.example.awstestapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.awstestapp.ui.navigation.Screen
import com.example.awstestapp.ui.viewmodel.RegisterViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var nickname by remember { mutableStateOf("") }

    // 에러 메시지가 있을 때 Toast를 보여줌
    LaunchedEffect(key1 = uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.errorShown()
        }
    }

    // 회원가입 성공 시 메인 화면으로 이동
    LaunchedEffect(key1 = uiState.isRegistrationSuccess) {
        if (uiState.isRegistrationSuccess) {
            Toast.makeText(context, "회원가입에 성공했습니다!", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.Main.route) {
                // 로그인과 회원가입 화면을 모두 스택에서 제거
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("회원가입", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("사용하실 닉네임을 입력해주세요.")
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("닉네임") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    viewModel.registerUser(nickname)
                }) {
                    Text("가입 완료")
                }
            }
        }
    }
}