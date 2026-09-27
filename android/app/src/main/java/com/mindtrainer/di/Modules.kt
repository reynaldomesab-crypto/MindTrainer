package com.mindtrainer.di

import android.content.Context
import com.google.dagger.hilt.android.qualifiers.ApplicationContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.mindtrainer.data.api.MindTrainerApi
import com.mindtrainer.data.local.AppDatabase
import com.mindtrainer.data.preferences.UserPreferences
import com.mindtrainer.data.repository.{ExerciseRepositoryImpl, ProgressRepositoryImpl, UserRepositoryImpl}
import com.mindtrainer.domain.repository.{ExerciseRepository, ProgressRepository, UserRepository}
import com.mindtrainer.domain.usecase.*

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.mindtrainer.app/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): MindTrainerApi {
        return retrofit.create(MindTrainerApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideExerciseRepository(
        api: MindTrainerApi,
        database: AppDatabase,
        preferences: UserPreferences
    ): ExerciseRepository {
        return ExerciseRepositoryImpl(api, database, preferences)
    }

    @Provides
    @Singleton
    fun provideProgressRepository(
        api: MindTrainerApi,
        database: AppDatabase
    ): ProgressRepository {
        return ProgressRepositoryImpl(api, database)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        api: MindTrainerApi,
        database: AppDatabase,
        preferences: UserPreferences
    ): UserRepository {
        return UserRepositoryImpl(api, database, preferences)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetDailyExerciseUseCase(repository: ExerciseRepository) =
        GetDailyExerciseUseCase(repository)

    @Provides
    @Singleton
    fun provideSubmitExerciseUseCase(repository: ExerciseRepository) =
        SubmitExerciseUseCase(repository)

    @Provides
    @Singleton
    fun provideGetProgressUseCase(repository: ProgressRepository) =
        GetProgressUseCase(repository)

    @Provides
    @Singleton
    fun provideGetInsightsUseCase(repository: ProgressRepository) =
        GetInsightsUseCase(repository)

    @Provides
    @Singleton
    fun provideAuthUseCase(repository: UserRepository) =
        AuthUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdatePreferencesUseCase(repository: UserRepository) =
        UpdatePreferencesUseCase(repository)
}