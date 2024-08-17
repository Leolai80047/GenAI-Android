package com.leodemo.genai_android.data.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.leodemo.genai_android.BuildConfig
import com.leodemo.genai_android.data.local.ai.GeminiModel
import com.leodemo.genai_android.data.model.AiModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AiModelModule {

    @Provides
    fun provideGenerativeModel(): GenerativeModel {
        return GenerativeModel(
            "gemini-1.5-flash",
            BuildConfig.api_key,
            generationConfig = generationConfig {
                temperature = 0.5f
            },
            safetySettings = listOf(
                SafetySetting(
                    HarmCategory.DANGEROUS_CONTENT,
                    BlockThreshold.NONE
                )
            )
        )
    }

    @Provides
    fun provideGeminiModel(
        model: GenerativeModel
    ): AiModel {
        return GeminiModel(model)
    }
}