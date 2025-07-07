package com.example.awstestapp.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.awstestapp.ui.viewmodel.CreatePostUiState
import com.example.awstestapp.ui.viewmodel.CreatePostViewModel
import com.example.awstestapp.ui.viewmodel.PostType
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController) {
    val viewModel: CreatePostViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val selectedAddressState = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("selected_address")
        ?.observeAsState()

    LaunchedEffect(selectedAddressState?.value) {
        selectedAddressState?.value?.let { address ->
            if (address.isNotBlank()) {
                viewModel.onLastSeenLocationChange(address)
                navController.currentBackStackEntry?.savedStateHandle?.set("selected_address", null)
            }
        }
    }

    LaunchedEffect(key1 = uiState.isPostCreated) {
        if (uiState.isPostCreated) {
            Toast.makeText(context, "게시물이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("새 게시물 작성") },
                actions = {
                    Button(onClick = { viewModel.submitPost() }) {
                        Text("완료")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            uiState.errorMessage?.let {
                Text(text = "오류: $it", color = MaterialTheme.colorScheme.error)
            }

            PostTypeSelector(
                selectedType = uiState.postType,
                onTypeSelected = { viewModel.onPostTypeChange(it) }
            )

            when (uiState.postType) {
                PostType.PERSON -> PersonInputFields(uiState, viewModel)
                PostType.ANIMAL -> AnimalInputFields(uiState, viewModel)
            }

            CommonInputFields(uiState, viewModel, navController)
        }
    }
}

@Composable
fun PostTypeSelector(selectedType: PostType, onTypeSelected: (PostType) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedType == PostType.PERSON,
            onClick = { onTypeSelected(PostType.PERSON) }
        )
        Text("사람", modifier = Modifier.padding(start = 4.dp, end = 16.dp))
        RadioButton(
            selected = selectedType == PostType.ANIMAL,
            onClick = { onTypeSelected(PostType.ANIMAL) }
        )
        Text("동물", modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun PersonInputFields(uiState: CreatePostUiState, viewModel: CreatePostViewModel) {
    OutlinedTextField(value = uiState.name, onValueChange = viewModel::onNameChange, label = { Text("이름") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = uiState.ageAtMissing, onValueChange = viewModel::onAgeAtMissingChange, label = { Text("실종 당시 나이") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
    OutlinedTextField(value = uiState.height, onValueChange = viewModel::onHeightChange, label = { Text("키 (cm)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
    OutlinedTextField(value = uiState.weight, onValueChange = viewModel::onWeightChange, label = { Text("몸무게 (kg)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
}

@Composable
fun AnimalInputFields(uiState: CreatePostUiState, viewModel: CreatePostViewModel) {
    OutlinedTextField(value = uiState.name, onValueChange = viewModel::onNameChange, label = { Text("이름 (없으면 비워두세요)") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = uiState.animalType, onValueChange = viewModel::onAnimalTypeChange, label = { Text("동물 종류 (예: 개, 고양이)") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = uiState.breed, onValueChange = viewModel::onBreedChange, label = { Text("품종") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = uiState.age, onValueChange = viewModel::onAgeChange, label = { Text("나이") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonInputFields(uiState: CreatePostUiState, viewModel: CreatePostViewModel, navController: NavController) {
    val genderOptions = if (uiState.postType == PostType.PERSON) listOf("남성", "여성", "알 수 없음") else listOf("수컷", "암컷", "알 수 없음")
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.onPhotoSelected(it.toString())
            }
        }
    )
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            TimePickerDialog(
                context,
                { _, hourOfDay: Int, minute: Int ->
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                    val formattedDateTime = String.format("%d-%02d-%02d %02d:%02d", year, month + 1, dayOfMonth, hourOfDay, minute)
                    viewModel.onLastSeenAtChange(formattedDateTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // 성별 드롭다운 메뉴
    ExposedDropdownMenuBox(
        expanded = uiState.isGenderDropdownExpanded,
        onExpandedChange = { viewModel.onGenderDropdownClick() }
    ) {
        OutlinedTextField(
            value = uiState.gender,
            onValueChange = {},
            readOnly = true,
            label = { Text("성별") },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isGenderDropdownExpanded) }
        )
        ExposedDropdownMenu(
            expanded = uiState.isGenderDropdownExpanded,
            onDismissRequest = { viewModel.onGenderDropdownDismiss() }
        ) {
            genderOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { viewModel.onGenderChange(option) })
            }
        }
    }

    // 목격 장소 선택
    Box(modifier = Modifier.fillMaxWidth().clickable(onClick = { navController.navigate("map_selection") })) {
        OutlinedTextField(
            value = uiState.lastSeenLocation,
            onValueChange = {},
            readOnly = true,
            label = { Text("마지막 목격 장소 (지도에서 선택)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )
    }

    // 목격 시간 선택
    Box(modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }) {
        OutlinedTextField(
            value = uiState.lastSeenAt,
            onValueChange = {},
            readOnly = true,
            label = { Text("마지막 목격 시간 (클릭하여 선택)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )
    }

    OutlinedTextField(
        value = uiState.description,
        onValueChange = viewModel::onDescriptionChange,
        label = { Text("상세 설명 (특징, 옷차림 등)") },
        modifier = Modifier.fillMaxWidth().height(150.dp)
    )
    Column {
        Button(
            onClick = {
                // 갤러리를 실행합니다.
                galleryLauncher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("대표 사진 추가")
        }

        // 선택된 이미지가 있으면 미리보기를 보여줍니다.
        uiState.photoUri?.let { uri ->
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context).data(data = uri).build()
                ),
                contentDescription = "선택된 사진",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}