package com.example.awstestapp.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.awstestapp.ui.viewmodel.AuthResult
import com.example.awstestapp.ui.viewmodel.LoginViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.awstestapp.ui.navigation.Screen

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }

    // 에러 메시지 처리
    LaunchedEffect(key1 = uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.errorShown()
        }
    }

    // --- 이 부분이 새로운 버전입니다 ---
    // 로그인 결과 처리
    LaunchedEffect(key1 = uiState.authResult) {
        when (uiState.authResult) {
            is AuthResult.LoginSuccess -> {
                Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
                // 로그인에 성공했으므로, 메인 화면으로 이동합니다.
                // 이 과정에서 로그인 화면은 스택에서 제거하여, 뒤로 가기 버튼을 눌러도 다시 돌아오지 않게 합니다.
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }
                }
            }
            is AuthResult.RegistrationNeeded -> {
                Toast.makeText(context, "신규 사용자입니다. 회원가입을 진행합니다.", Toast.LENGTH_SHORT).show()
                // [수정] 회원가입 화면으로 이동합니다.
                navController.navigate(Screen.Register.route)
            }
            null -> { /* Do nothing */ }
        }
    }
    // ---------------------------------

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("로그인", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))

                if (!uiState.isOtpSent) {
                    OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("전화번호 (예: +821012345678)") })
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.sendOtp(phoneNumber, context as Activity) }) {
                        Text("인증번호 발송")
                    }
                } else {
                    OutlinedTextField(value = otpCode, onValueChange = { otpCode = it }, label = { Text("인증번호 6자리") })
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.verifyOtpAndSignIn(otpCode) }) {
                        Text("로그인/확인")
                    }
                }
            }
        }
    }
}