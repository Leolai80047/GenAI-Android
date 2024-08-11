package com.leodemo.genai_android.ui.navigation

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.leodemo.genai_android.ui.screens.photoDescribe.CameraCaptureScreen
import com.leodemo.genai_android.ui.screens.photoDescribe.PhotoDescribeScreen
import com.leodemo.genai_android.ui.screens.photoDescribe.PhotoDescribeViewModel
import com.leodemo.genai_android.ui.screens.Screen
import com.leodemo.genai_android.ui.screens.menu.MenuScreen
import com.leodemo.genai_android.ui.screens.summarize.SummarizeScreen

private const val BITMAP_SELECT_KEY = "BITMAP_SELECT"

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
        composable<Screen.PhotoDescribeScreen> { navBackStackEntry ->
            val viewModel = hiltViewModel<PhotoDescribeViewModel>()
            viewModel.setSelectedBitmap(navBackStackEntry.savedStateHandle[BITMAP_SELECT_KEY])
            PhotoDescribeScreen(
                viewModel = viewModel,
                startCamera = {
                    navController.navigate(Screen.CameraCaptureScreen)
                },
                clearBitmap = {
                    navBackStackEntry.savedStateHandle.remove<Bitmap>(BITMAP_SELECT_KEY)
                }
            )
        }
        composable<Screen.ChatScreen> {

        }
        composable<Screen.CameraCaptureScreen> {
            CameraCaptureScreen(
                onBack = { bitmap ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(BITMAP_SELECT_KEY, bitmap)
                    navController.popBackStack()
                }
            )
        }
    }
}