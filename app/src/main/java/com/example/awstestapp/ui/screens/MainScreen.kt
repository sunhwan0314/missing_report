package com.example.awstestapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.awstestapp.ui.navigation.BottomNavItem
import com.example.awstestapp.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.material.icons.Icons // 추가
import androidx.compose.material.icons.filled.Add // 추가
import androidx.compose.material3.FloatingActionButton // 추가
import androidx.navigation.compose.currentBackStackEntryAsState


import com.example.awstestapp.ui.screens.MapScreen
import com.example.awstestapp.ui.screens.ChatListScreen
import com.example.awstestapp.ui.screens.MyProfileScreen

@Composable
fun MainScreen(mainNavController: NavController) {
    val bottomNavController = rememberNavController()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = bottomNavController) },
        floatingActionButton = {
            if (currentRoute == BottomNavItem.Home.screenRoute) {
                FloatingActionButton(onClick = {
                    mainNavController.navigate(Screen.CreatePost.route)
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "글쓰기")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavGraph(
                bottomNavController = bottomNavController,
                mainNavController = mainNavController // 앱 전체 NavController를 전달
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Map,
        BottomNavItem.Chat,
        BottomNavItem.MyProfile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.screenRoute,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun BottomNavGraph(bottomNavController: NavHostController, mainNavController: NavController) {


    NavHost(navController = bottomNavController, startDestination = BottomNavItem.Home.screenRoute) {
        composable(BottomNavItem.Home.screenRoute) {
            HomeScreen(navController = mainNavController)
        }
        // [수정] Text()를 실제 Screen Composable로 교체
        composable(BottomNavItem.Map.screenRoute) {
            MapScreen(navController = mainNavController)
        }
        composable(BottomNavItem.Chat.screenRoute) {
            ChatListScreen(navController = mainNavController)
        }
        composable(BottomNavItem.MyProfile.screenRoute) {
            MyProfileScreen(mainNavController = mainNavController)
        }
    }
}