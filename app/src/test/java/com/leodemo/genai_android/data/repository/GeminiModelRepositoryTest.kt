package com.leodemo.genai_android.data.repository

import com.leodemo.genai_android.data.local.ai.GeminiModel
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit4.FunSpec
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class GeminiModelRepositoryTest : FunSpec({

    val model = mockk<GeminiModel>()
    val repository = GeminiModelRepository(model)

    fun generateSplitAnswer(answer: String, partCount: Int) = buildList {
        var endIndex = 0
        (1..partCount).forEach { index ->
            val previousIndex = endIndex
            endIndex = Random.nextInt(endIndex, answer.length / partCount)
            when (index) {
                1 -> {
                    answer.substring(0, endIndex)
                }

                partCount -> {
                    answer.substring(previousIndex)
                }

                else -> {
                    answer.substring(previousIndex, endIndex)
                }
            }.let {
                add(it)
            }
        }
    }

    test("test fetchTextGenerationStream") {
        val prompt = "Say hello"
        val answer = "Hello World!"
        var partAnswer = ""
        val splitAnswer = generateSplitAnswer(answer, 3)

        coEvery {
            model.generateTextByTextStream(prompt)
        } returns flow {
            emit(splitAnswer[0])
            delay(1000L)
            emit(splitAnswer[1])
            delay(500L)
            emit(splitAnswer[2])
        }

        runTest {
            val job = launch {
                repository.fetchTextGenerationStream(prompt)
                    .collect {
                        partAnswer += it
                    }
            }

            runCurrent()
            partAnswer shouldBe splitAnswer[0]

            advanceTimeBy(1000L)
            runCurrent()
            partAnswer shouldBe splitAnswer.slice(0..1).joinToString("")

            advanceTimeBy(500L)
            runCurrent()
            partAnswer shouldBe answer

            job.cancel()
        }
    }

    test("test fetchTextImageGenerationStream") {
        val prompt = "What animal in the picture?"
        val answer = "There is a dog!"
        var partAnswer = ""
        val splitAnswer = generateSplitAnswer(answer, 2)

        coEvery {
            model.generateTextByTextAndImageStream(prompt, any())
        } returns flow {
            emit(splitAnswer[0])
            delay(1000L)
            emit(splitAnswer[1])
        }

        runTest {
            val job = launch {
                repository.fetchTextImageGenerationStream(prompt, mockk())
                    .collect {
                        partAnswer += it
                    }
            }

            runCurrent()
            partAnswer shouldBe splitAnswer[0]

            advanceTimeBy(1000L)
            runCurrent()
            partAnswer shouldBe answer

            job.cancel()
        }
    }

    test("test fetchChatAnswerStream") {
        val prompt = "You are a chief."
        val answer = "What recipe do you want make? or what do you want to know?"
        var partAnswer = ""
        val splitAnswer = generateSplitAnswer(answer, 5)

        coEvery {
            model.generateChatStream(prompt)
        } returns flow {
            delay(5000L)
            emit(splitAnswer[0])
            delay(100L)
            emit(splitAnswer[1])
            delay(500L)
            emit(splitAnswer[2])
            delay(1000L)
            emit(splitAnswer[3])
            delay(500L)
            emit(splitAnswer[4])
        }

        runTest {
            val job = launch {
                repository.fetchChatAnswerStream(prompt)
                    .collect {
                        partAnswer += it
                    }
            }

            advanceTimeBy(5000L)
            runCurrent()
            partAnswer shouldBe splitAnswer[0]

            advanceTimeBy(100L)
            runCurrent()
            partAnswer shouldBe splitAnswer.slice(0..1).joinToString("")

            advanceTimeBy(500L)
            runCurrent()
            partAnswer shouldBe splitAnswer.slice(0..2).joinToString("")

            advanceTimeBy(1000L)
            runCurrent()
            partAnswer shouldBe splitAnswer.slice(0..3).joinToString("")

            advanceTimeBy(500L)
            runCurrent()
            partAnswer shouldBe answer

            job.cancel()
        }
    }
})