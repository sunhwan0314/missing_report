package com.example.awstestapp.di

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.data.repository.AuthRepositoryImpl
import com.example.awstestapp.data.repository.CreatePostRepositoryImpl
import com.example.awstestapp.data.repository.HomeRepositoryImpl
import com.example.awstestapp.data.repository.MapRepositoryImpl
import com.example.awstestapp.data.repository.UserRepositoryImpl
import com.example.awstestapp.domain.repository.AuthRepository
import com.example.awstestapp.domain.repository.CreatePostRepository
import com.example.awstestapp.domain.repository.HomeRepository
import com.example.awstestapp.domain.repository.MapRepository
import com.example.awstestapp.domain.repository.UserRepository
import com.example.awstestapp.ui.viewmodel.ChatViewModel
import com.example.awstestapp.ui.viewmodel.CreatePostViewModel
import com.example.awstestapp.ui.viewmodel.DetailViewModel
import com.example.awstestapp.ui.viewmodel.EditPostViewModel
import com.example.awstestapp.ui.viewmodel.HomeViewModel
import com.example.awstestapp.ui.viewmodel.LoginViewModel
import com.example.awstestapp.ui.viewmodel.MapViewModel
import com.example.awstestapp.ui.viewmodel.MyProfileViewModel

import com.example.awstestapp.ui.viewmodel.RegisterViewModel
import com.example.awstestapp.ui.viewmodel.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val API_BASE_URL = "http://3.34.133.110:3000" // 본인의 EC2 IP로 변경

// 데이터 소스, 네트워킹, Repository 관련 모듈
val dataModule = module {
    // API 기본 URL을 "baseUrl"이라는 이름으로 제공
    single(named("baseUrl")) { API_BASE_URL }

    // OkHttpClient
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
    }

    // Retrofit 인스턴스 생성
    single {
        Retrofit.Builder()
            .baseUrl(get<String>(named("baseUrl")))
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ApiService 구현체 생성
    single { get<Retrofit>().create(ApiService::class.java) }

    // UserRepository 인터페이스를 요청하면, UserRepositoryImpl 구현체를 제공
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    // [새로 추가] HomeRepository를 요청하면, HomeRepositoryImpl을 제공
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    single<CreatePostRepository> { CreatePostRepositoryImpl(get()) }
}

// 인증 관련 모듈
val authModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseStorage.getInstance() }
}

// ViewModel들을 위한 모듈
val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    // LoginViewModel이 이제 FirebaseAuth와 UserRepository를 모두 필요로 함
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get(), get()) } // RegisterViewModel 레시피 추가
    viewModel { HomeViewModel(get()) }
    viewModel { params -> DetailViewModel(get(), get(), get(), params.get()) }
    viewModel { CreatePostViewModel(get(),get()) }
    viewModel { params -> EditPostViewModel(get(), get(), get(), params.get()) }

    viewModel { ChatViewModel() }
    viewModel { MyProfileViewModel() }
    single<MapRepository> { MapRepositoryImpl(get()) }
    viewModel { MapViewModel(get()) }
}