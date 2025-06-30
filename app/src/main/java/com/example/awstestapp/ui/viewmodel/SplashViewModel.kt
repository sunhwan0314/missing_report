package com.example.awstestapp.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.awstestapp.ui.navigation.Screen

class SplashViewModel(private val auth: FirebaseAuth) : ViewModel() {

    // 스플래시 화면을 계속 보여줄지 여부를 결정하는 상태
    private val _isReady = mutableStateOf(false)
    val isReady: State<Boolean> = _isReady

    // 로그인 상태에 따라 결정될 시작 화면 경로
    private val _startDestination = mutableStateOf(Screen.Splash.route)
    val startDestination: State<String> = _startDestination

    init {
        // ViewModel이 생성되자마자 로그인 상태를 확인
        if (auth.currentUser != null) {
            // 로그인된 사용자가 있으면 -> 메인 화면으로 시작
            _startDestination.value = Screen.Main.route
        } else {
            // 로그인된 사용자가 없으면 -> 로그인 화면으로 시작
            _startDestination.value = Screen.Login.route
        }
        // 확인이 끝났으니, 스플래시 화면을 그만 보여줘도 된다고 알림
        _isReady.value = true
    }
}