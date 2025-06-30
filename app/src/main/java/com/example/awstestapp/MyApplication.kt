package com.example.awstestapp // 본인의 패키지 이름에 맞게 수정

import android.app.Application
import com.example.awstestapp.di.authModule
import com.example.awstestapp.di.dataModule
import com.example.awstestapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger() // Koin 로그 출력
            androidContext(this@MyApplication) // 안드로이드 컨텍스트 전달
            modules(dataModule, authModule, viewModelModule) // 우리가 만든 모듈 설정
        }
    }
}