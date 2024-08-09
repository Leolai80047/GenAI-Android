package com.leodemo.genai_android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.leodemo.genai_android.ui.screens.Screen
import com.leodemo.genai_android.ui.screens.menu.MenuScreen
import com.leodemo.genai_android.ui.screens.summarize.SummarizeScreen

@Composable
fun GenAiNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MenuScreen) {
        composable<Screen.MenuScreen> {
            MenuScreen { screen ->
                navController.navigate(screen)
            }
        }
        composable<Screen.SummarizeScreen> {
            SummarizeScreen()
        }
        composable<Screen.TextImageMultiScreen> {

        }
        composable<Screen.ChatScreen> {

        }
    }
}