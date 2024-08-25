package com.leodemo.genai_android.ui.component

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.leodemo.genai_android.data.di.AiModelModule
import com.leodemo.genai_android.data.di.RepositoryModule
import com.leodemo.genai_android.ui.theme.GenAiAndroidTheme
import com.leodemo.genai_android.utils.TestTags
import com.leodemo.genai_android.utils.speechRecognizer.BasicRecognitionListener
import com.leodemo.genai_android.utils.speechRecognizer.SpeechRecognizerEventListener
import com.leodemo.genai_android.utils.speechRecognizer.SpeechRecognizerManager
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalComposeUiApi::class)
@HiltAndroidTest
@UninstallModules(AiModelModule::class, RepositoryModule::class)
class SpeechRecognizerButtonTest {

    private lateinit var device: UiDevice
    private lateinit var mockSpeechRecognizerManager: SpeechRecognizerManager
    private lateinit var speechRecognizerEventListener: SpeechRecognizerEventListener
    private lateinit var recognitionListener: RecognitionListener
    private lateinit var resultString: String
    private var resultErrorCode = -1

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        mockSpeechRecognizerManager = mockk(relaxed = true)
        speechRecognizerEventListener = object : SpeechRecognizerEventListener {
            override fun onStart() {

            }

            override fun onResult(result: String?) {
                resultString = result ?: throw AssertionError("Result is null!")
            }

            override fun onError(code: Int) {
                resultErrorCode = code
            }
        }
        recognitionListener = BasicRecognitionListener(speechRecognizerEventListener)
        mockSpeechRecognizerManager.setRecognitionListener(recognitionListener)

        composeRule.setContent {
            GenAiAndroidTheme {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .semantics {
                        testTagsAsResourceId = true
                    }
                ) {
                    SpeechRecognizerButton(
                        speechRecognizerManager = mockSpeechRecognizerManager,
                    )
                }
            }
        }
    }

    @Test
    fun testStartVoiceHello() {
        val result = "Hello"

        every {
            mockSpeechRecognizerManager.startVoice(any())
        } answers {
            mockSpeechRecognizerManager.stopVoice()
            recognitionListener.onBeginningOfSpeech()
            recognitionListener.onResults(Bundle().apply {
                putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf(result))
            })
        }

        composeRule.onNodeWithTag(TestTags.SPEECH_RECOGNIZER_BUTTON).apply {
            assertIsDisplayed()
            performClick()
        }

        val allowButton =
            device.findObject(UiSelector().className("android.widget.Button").instance(0))
        if (allowButton.exists()) {
            allowButton.click()
        }

        verify {
            mockSpeechRecognizerManager.startVoice()
        }

        verify {
            mockSpeechRecognizerManager.stopVoice()
        }

        Assert.assertTrue(resultString == result)

    }

    @Test
    fun testRecognizeError() {
        val errorCode = 7

        every {
            mockSpeechRecognizerManager.startVoice(any())
        } answers {
            mockSpeechRecognizerManager.stopVoice()
            recognitionListener.onBeginningOfSpeech()
            recognitionListener.onError(errorCode)
        }

        composeRule.onNodeWithTag(TestTags.SPEECH_RECOGNIZER_BUTTON).apply {
            assertIsDisplayed()
            performClick()
        }

        verify {
            mockSpeechRecognizerManager.startVoice()
        }

        verify {
            mockSpeechRecognizerManager.stopVoice()
        }

        Assert.assertTrue(resultErrorCode == errorCode)
    }
}