import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.awstestapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.awstestapp"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        // local.properties 파일에서 네이버/구글 맵 키를 읽어와서 BuildConfig 변수로 만듭니다.
        val localProperties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.reader())
        }
        // 네이버 관련 설정은 이제 필요 없으므로 주석 처리하거나 삭제해도 됩니다.
        // buildConfigField("String", "NAVER_MAP_CLIENT_SECRET", "\"${localProperties.getProperty("NAVER_MAP_CLIENT_SECRET")}\"")

        // [새로 추가] 구글 맵 API 키를 BuildConfig 변수로 만듭니다.
        manifestPlaceholders["MAPS_API_KEY"] = localProperties.getProperty("MAPS_API_KEY") ?: ""
    }
    buildFeatures {
        compose = true
        // buildConfig를 사용하도록 설정
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.play.services.maps)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 기본 라이브러리들
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")
    implementation("androidx.activity:activity-compose:1.9.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // BoM을 사용하므로, 개별 라이브러리에는 버전을 명시하지 않아도 됩니다.
    implementation("com.google.firebase:firebase-auth-ktx")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")


    // 이전에 추가했던 OkHttp 라이브러리들
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Retrofit (네트워킹)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") // JSON <-> Kotlin 객체 변환
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // 통신 로그 확인용

    implementation("com.google.firebase:firebase-storage-ktx")



    val koinVersion = "3.5.6" // 최신 버전 확인 후 적용
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")
// 기타 (ViewModel, Navigation 등)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Core Splashscreen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Coil (이미지 로딩 라이브러리)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // [새로 추가] Firebase/Google Play 서비스 Task와 코루틴을 연결해주는 라이브러리
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // [새로 추가] Google 지도 Compose 라이브러리 ✅
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

// [새로 추가] Firebase Realtime Database 라이브러리
    implementation("com.google.firebase:firebase-database-ktx")
    // [새로 추가] LiveData를 Compose State로 변환해주는 라이브러리
    implementation("androidx.compose.runtime:runtime-livedata:1.6.7")
}