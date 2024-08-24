package com.leodemo.genai_android.ui.screens.summarize

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.leodemo.genai_android.R
import com.leodemo.genai_android.data.di.AiModelModule
import com.leodemo.genai_android.data.di.RepositoryModule
import com.leodemo.genai_android.ui.main.MainActivity
import com.leodemo.genai_android.ui.screens.Screen
import com.leodemo.genai_android.ui.theme.GenAiAndroidTheme
import com.leodemo.genai_android.util.WaitTimeout
import com.leodemo.genai_android.utils.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AiModelModule::class, RepositoryModule::class)
class SummarizeScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @OptIn(ExperimentalComposeUiApi::class)
    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.activity.setContent {
            GenAiAndroidTheme {
                val navController = rememberNavController()
                NavHost(
                    modifier = Modifier.semantics {
                        testTagsAsResourceId = true
                    },
                    navController = navController,
                    startDestination = Screen.SummarizeScreen
                ) {
                    composable<Screen.SummarizeScreen> {
                        SummarizeScreen()
                    }
                }
            }
        }
    }

    @Test
    fun testInputPromptAndAnswerSuccess() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val device = UiDevice.getInstance(instrumentation)
        val context = ApplicationProvider.getApplicationContext<Context>()
        composeRule.onNodeWithText(context.getString(R.string.app_name)).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.CHAT_TEXT_FIELD).apply {
            assertIsDisplayed()
            performTextInput("Hello World!")
        }
        composeRule.onNodeWithTag(TestTags.CHAT_TEXT_SEND).apply {
            assertIsDisplayed()
            performClick()
        }

        composeRule.waitUntil(WaitTimeout.MIN) {
            device.hasObject(By.res(TestTags.CHAT_BUBBLE))
        }
        composeRule.waitUntil(WaitTimeout.MIN) {
            device.hasObject(By.res(TestTags.SUMMARIZE_LOADING))
        }
        composeRule.waitUntil(WaitTimeout.MEDIUM) {
            !device.hasObject(By.res(TestTags.SUMMARIZE_LOADING))
        }
        composeRule.waitUntil(WaitTimeout.MIN) {
            !device.hasObject(By.res(TestTags.SUMMARIZE_ERROR))
        }
        composeRule.waitUntil(WaitTimeout.MIN) {
            device.hasObject(By.res(TestTags.SUMMARIZE_ANSWER))
        }
    }
}