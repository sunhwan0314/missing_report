package com.example.awstestapp.ui.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit
import com.example.awstestapp.domain.repository.UserRepository // UserRepository import
import kotlinx.coroutines.launch

// --- 이 부분이 새로운 버전입니다 ---
data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOtpSent: Boolean = false,
    val authResult: AuthResult? = null // isLoginSuccess 대신 authResult 사용
)

sealed class AuthResult {
    object LoginSuccess : AuthResult()
    object RegistrationNeeded : AuthResult()
}
// ---------------------------------

class LoginViewModel(private val auth: FirebaseAuth,
                     private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun sendOtp(phoneNumber: String, activity: Activity) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // 1. 자동 인증 또는 즉시 확인이 가능한 경우 (예: 같은 기기에서 재시도)
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("LoginViewModel", "onVerificationCompleted: 자동 인증 성공")
                // 인증이 완료되었으므로 바로 로그인 시도
                signInWithCredential(credential)
            }

            // 2. 인증이 실패했을 경우 (오류 메시지가 지적한, 빠져있던 부분)
            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("LoginViewModel", "onVerificationFailed: 인증 실패", e)
                // UI에 에러 메시지를 전달
                _uiState.update { it.copy(isLoading = false, errorMessage = "인증 실패: ${e.message}") }
            }

            // 3. 인증 코드가 성공적으로 발송되었을 경우
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("LoginViewModel", "onCodeSent: 인증번호 발송 성공")
                // 다음 단계(OTP 입력)를 위해 서버로부터 받은 verificationId를 저장
                storedVerificationId = verificationId
                resendToken = token
                // UI에 인증번호가 발송되었음을 알림
                _uiState.update { it.copy(isLoading = false, isOtpSent = true) }
            }
        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber).setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity).setCallbacks(callbacks).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtpAndSignIn(otpCode: String) {
        val verificationId = storedVerificationId ?: run {
            _uiState.update { it.copy(errorMessage = "인증 ID를 찾을 수 없습니다.") }
            return
        }
        val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
        signInWithCredential(credential)
    }
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        _uiState.update { it.copy(isLoading = true) }
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Firebase 로그인 성공 후, 우리 서버에 사용자가 등록되어 있는지 확인
                    checkUserRegistration() // 함수 이름 오타 수정
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "로그인 실패: ${task.exception?.message}") }
                }
            }
    }
    // LoginViewModel.kt 파일 내부
    private fun checkUserRegistration() {
        val user = auth.currentUser
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result?.token ?: ""
                viewModelScope.launch {
                    // 이제 ViewModel은 직접 통신하지 않고, Repository에게 일을 시킵니다.
                    userRepository.checkUserRegistered(idToken)
                        .onSuccess { isRegistered ->
                            if (isRegistered) {
                                _uiState.value = LoginUiState(authResult = AuthResult.LoginSuccess)
                            } else {
                                _uiState.value = LoginUiState(authResult = AuthResult.RegistrationNeeded)
                            }
                        }
                        .onFailure {
                            _uiState.value = LoginUiState(errorMessage = it.message)
                        }
                }
            } else {
                _uiState.value = LoginUiState(errorMessage = "ID 토큰을 가져오는데 실패했습니다.")
            }
        }
    }

    fun errorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}