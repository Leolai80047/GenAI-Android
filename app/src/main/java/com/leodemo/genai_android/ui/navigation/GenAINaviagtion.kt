package com.leodemo.genai_android.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.leodemo.genai_android.ui.screens.Screen
import com.leodemo.genai_android.ui.screens.cameraCapture.CameraCaptureScreen
import com.leodemo.genai_android.ui.screens.chatRoom.ChatRoomScreen
import com.leodemo.genai_android.ui.screens.menu.MenuScreen
import com.leodemo.genai_android.ui.screens.photoDescribe.PhotoDescribeScreen
import com.leodemo.genai_android.ui.screens.photoDescribe.PhotoDescribeViewModel
import com.leodemo.genai_android.ui.screens.summarize.SummarizeScreen

private const val BITMAP_SELECT_KEY = "BITMAP_SELECT"

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GenAiNavigation() {
    val navController = rememberNavController()
    NavHost(
        modifier = Modifier.semantics {
            testTagsAsResourceId = true
        },
        navController = navController,
        startDestination = Screen.MenuScreen
    ) {
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
            val uri by remember {
                mutableStateOf(
                    navBackStackEntry.savedStateHandle.get<Uri>(BITMAP_SELECT_KEY)
                )
            }
            viewModel.setSelectedUri(uri)
            navBackStackEntry.savedStateHandle.remove<Uri>(BITMAP_SELECT_KEY)
            PhotoDescribeScreen(
                viewModel = viewModel,
                startCamera = {
                    navController.navigate(Screen.CameraCaptureScreen)
                }
            )
        }
        composable<Screen.ChatRoomScreen> {
            ChatRoomScreen()
        }
        composable<Screen.CameraCaptureScreen> {
            CameraCaptureScreen(
                onBack = { uri ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(BITMAP_SELECT_KEY, uri)
                    navController.popBackStack()
                }
            )
        }
    }
}