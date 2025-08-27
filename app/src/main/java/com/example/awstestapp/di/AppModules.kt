package com.example.awstestapp.di

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.data.repository.*
import com.example.awstestapp.domain.repository.*
import com.example.awstestapp.ui.viewmodel.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val API_BASE_URL = "http://15.164.213.121:3000" // 본인의 EC2 IP

// 데이터 소스, 네트워킹, Repository 관련 모듈
val dataModule = module {
    single(named("baseUrl")) { API_BASE_URL }

    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(get<String>(named("baseUrl")))
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(ApiService::class.java) }

    // --- 모든 Repository들을 여기에 등록합니다. ---
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    single<CreatePostRepository> { CreatePostRepositoryImpl(get()) }
    single<SightingRepository> { SightingRepositoryImpl(get()) }
    single<PostListRepository> { PostListRepositoryImpl(get()) }
    single<MyProfileRepository> { MyProfileRepositoryImpl(get()) }
    single<MapRepository> { MapRepositoryImpl(get()) }
    // [새로 추가] DetailRepository를 등록합니다.
    single<DetailRepository> { DetailRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get(), get()) }
}

// 인증 관련 모듈
val authModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseStorage.getInstance() }
    single { FirebaseDatabase.getInstance() }
}

// ViewModel들을 위한 모듈
val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    // [수정] EditPostViewModel이 DetailRepository도 필요로 할 수 있으므로, 모든 필요 의존성을 get()으로 전달
    viewModel { params -> EditPostViewModel(get(), get(), get(), params.get()) }
    viewModel { params -> DetailViewModel(get(), get(), get(),get(), params.get()) }
    viewModel { CreatePostViewModel(get(), get(),get()) }
    viewModel { MapViewModel(get()) }
    viewModel { MyProfileViewModel(get(), get()) }
    viewModel { PostListViewModel(get()) }
    viewModel { params -> CreateSightingViewModel(get(), get(), params.get()) }
    viewModel { ChatViewModel(get(), get()) }
}