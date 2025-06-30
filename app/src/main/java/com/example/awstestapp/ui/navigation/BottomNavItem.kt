package com.example.awstestapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val screenRoute: String
) {
    object Home : BottomNavItem("홈", Icons.Filled.Home, "home_screen")
    object Map : BottomNavItem("목격지도", Icons.Filled.LocationOn, "map_screen")
    object Chat : BottomNavItem("채팅", Icons.Filled.MailOutline, "chat_screen")
    object MyProfile : BottomNavItem("내 프로필", Icons.Filled.Person, "my_profile_screen")
}