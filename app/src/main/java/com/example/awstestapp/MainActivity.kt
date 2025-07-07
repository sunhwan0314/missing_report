package com.example.awstestapp // 본인의 패키지 이름

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.awstestapp.ui.navigation.Screen
import com.example.awstestapp.ui.screens.CreatePostScreen
import com.example.awstestapp.ui.screens.LoginScreen
import com.example.awstestapp.ui.screens.MainScreen
import com.example.awstestapp.ui.screens.SplashScreen
import com.example.awstestapp.ui.screens.RegisterScreen

import com.example.awstestapp.ui.theme.AwstestappTheme
import com.example.awstestapp.ui.viewmodel.SplashViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.awstestapp.ui.screens.DetailScreen
import com.example.awstestapp.ui.screens.EditPostScreen
import com.example.awstestapp.ui.screens.PersonListScreen // 추가
import com.example.awstestapp.ui.screens.AnimalListScreen
import com.example.awstestapp.ui.screens.ChatListScreen
import com.example.awstestapp.ui.screens.ChatRoomScreen
import com.example.awstestapp.ui.screens.CreateSightingScreen
import com.example.awstestapp.ui.screens.MapSelectionScreen

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                splashViewModel.isReady.value.not()
            }
        }

        setContent {
            AwstestappTheme {
                // 앱의 전체 네비게이션을 여기서 직접 관리합니다.
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = splashViewModel.startDestination.value
                ) {
                    // 시작 화면을 스플래시로 설정하고, ViewModel의 결과에 따라 이동
                    composable(Screen.Splash.route) {
                        SplashScreen(navController = navController)
                    }
                    // 로그인 화면 경로
                    composable(Screen.Login.route) {
                        LoginScreen(navController = navController)
                    }
                    composable(Screen.Register.route) {
                        RegisterScreen(navController = navController)
                    }
                    // 메인 화면 경로
                    composable(Screen.Main.route) {
                        // MainScreen에는 앱 전체의 NavController를 전달합니다.
                        MainScreen(mainNavController = navController)
                    }

                    composable(
                        route = Screen.Detail.route,
                        arguments = listOf( // URL 경로에서 어떤 이름으로 파라미터를 받을지 정의
                            navArgument("postType") { type = NavType.StringType },
                            navArgument("postId") { type = NavType.IntType }
                        )
                    ) {
                        DetailScreen(navController = navController)
                    }
                    // [새로 추가] 게시물 등록 화면 경로
                    composable(Screen.CreatePost.route) {
                        CreatePostScreen(navController = navController)
                    }
                    // [새로 추가] 게시물 수정 화면 경로
                    composable(
                        route = Screen.EditPost.route,
                        arguments = listOf(
                            navArgument("postType") { type = NavType.StringType },
                            navArgument("postId") { type = NavType.IntType }
                        )
                    ) {
                        EditPostScreen(navController = navController)
                    }
                    composable(Screen.PersonList.route) {
                        PersonListScreen(navController = navController)
                    }
                    composable(Screen.AnimalList.route) {
                        AnimalListScreen(navController = navController)
                    }
                    composable(Screen.ChatList.route) {
                        ChatListScreen(navController = navController)
                    }
                    composable(
                        route = Screen.ChatRoom.route,
                        arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
                        ChatRoomScreen(navController = navController, roomId = roomId)
                    }
                    // [새로 추가] 목격담 작성 화면 경로
                    composable(
                        route = Screen.CreateSighting.route,
                        arguments = listOf(
                            navArgument("postType") { type = NavType.StringType },
                            navArgument("postId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val postType = backStackEntry.arguments?.getString("postType") ?: ""
                        val postId = backStackEntry.arguments?.getInt("postId") ?: 0
                        CreateSightingScreen(
                            navController = navController,
                            postType = postType,
                            postId = postId
                        )
                    }
                    composable("map_selection") {
                        MapSelectionScreen(navController = navController)
                    }
                }
            }
        }
    }
}