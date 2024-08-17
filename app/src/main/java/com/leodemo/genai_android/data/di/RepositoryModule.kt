package com.leodemo.genai_android.data.di

import com.leodemo.genai_android.data.repository.GeminiModelRepository
import com.leodemo.genai_android.domain.repository.AiModelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAiModelRepository(aiModelRepository: GeminiModelRepository): AiModelRepository
}