package com.example.awstestapp.ui.screens

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSelectionScreen(navController: NavController) {
    val context = LocalContext.current
    // 서울을 기본 위치로 설정
    val defaultLocation = LatLng(37.5665, 126.9780)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 11f)
    }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("장소 선택") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLocation = latLng
                }
            ) {
                // 사용자가 위치를 선택하면 마커를 표시
                selectedLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "선택한 위치"
                    )
                }
            }

            // '선택 완료' 버튼
            Button(
                onClick = {
                    selectedLocation?.let { latLng ->
                        // 위도/경도를 주소로 변환
                        val address = getAddressFromLatLng(context, latLng)
                        if (address != null) {
                            // 이전 화면으로 주소 데이터를 전달
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selected_address", address)
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                enabled = selectedLocation != null // 위치가 선택되었을 때만 활성화
            ) {
                Text("이 위치로 설정")
            }
        }
    }
}

// 위도/경도를 주소 문자열로 변환하는 함수 (Reverse Geocoding)
private fun getAddressFromLatLng(context: Context, latLng: LatLng): String? {
    val geocoder = Geocoder(context, Locale.KOREAN)
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)?.firstOrNull()?.getAddressLine(0)
        } else {
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)?.firstOrNull()?.getAddressLine(0)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}