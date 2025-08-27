package com.example.awstestapp.ui.screens

import android.location.Geocoder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.awstestapp.ui.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel
import java.util.*

// 주소를 위도/경도로 변환하는 확장 함수
fun Geocoder.getLatLng(address: String): LatLng? {
    return try {
        @Suppress("DEPRECATION")
        val addresses = this.getFromLocationName(address, 1)
        if (addresses?.isNotEmpty() == true) {
            LatLng(addresses[0].latitude, addresses[0].longitude)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val geocoder = remember { Geocoder(context, Locale.KOREAN) }

    // 목격담 주소를 좌표로 변환한 결과를 담을 상태 변수
    var markers by remember { mutableStateOf<List<Pair<LatLng, String>>>(emptyList()) }

    // uiState.sightings가 변경될 때마다 주소를 좌표로 변환
    LaunchedEffect(key1 = uiState.sightings) {
        val markerList = mutableListOf<Pair<LatLng, String>>()
        uiState.sightings.forEach { sighting ->
            geocoder.getLatLng(sighting.sighting_location)?.let { latLng ->
                val markerText = sighting.name ?: if(sighting.type == "person") "사람" else "동물"
                markerList.add(latLng to "${markerText} 목격")
            }
        }
        markers = markerList
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.loadData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.errorMessage != null) {
            Text(text = "오류: ${uiState.errorMessage}", modifier = Modifier.align(Alignment.Center))
        } else {
            val seoul = LatLng(37.5665, 126.9780)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(seoul, 11f)
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
            ) {
                markers.forEach { (position, title) ->
                    Marker(
                        state = MarkerState(position = position),
                        title = title
                    )
                }
            }
        }
    }
}