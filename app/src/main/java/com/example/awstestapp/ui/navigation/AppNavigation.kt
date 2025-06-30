package com.example.awstestapp.ui.navigation // 본인의 패키지 이름에 맞게 수정

// 각 화면의 경로를 정의하는 sealed class
sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object Main : Screen("main_screen")
    object Register : Screen("register_screen")
    object CreatePost : Screen("create_post_screen")
    // {postType}과 {postId} 부분에 실제 값이 들어갈 자리임을 의미합니다.
    object Detail : Screen("detail_screen/{postType}/{postId}") {
        // 실제 이동할 때 사용할 URL을 쉽게 만들어주는 함수
        fun createRoute(postType: String, postId: Int) = "detail_screen/$postType/$postId"
    }

    // [새로 추가] 수정 화면 경로. 어떤 게시물을 수정할지 ID를 전달받음
    object EditPost : Screen("edit_post_screen/{postType}/{postId}") {
        fun createRoute(postType: String, postId: Int) = "edit_post_screen/$postType/$postId"
    }
}