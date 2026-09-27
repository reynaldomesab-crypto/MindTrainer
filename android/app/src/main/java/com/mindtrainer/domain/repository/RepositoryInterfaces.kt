package com.mindtrainer.domain.repository

import com.mindtrainer.data.api.dto.*
import com.mindtrainer.domain.model.*
import java.util.UUID

interface ExerciseRepository {
    suspend fun getSchulteDaily(size: Int): Result<SchulteDaily>
    suspend fun getSchultePackage(count: Int, size: Int): Result<SchultePackage>
    suspend fun submitSchulte(request: SchulteSubmitRequest): Result<SchulteResult>
    
    suspend fun getBlindfoldDaily(language: String): Result<BlindfoldDaily>
    suspend fun submitBlindfold(request: BlindfoldSubmitRequest): Result<BlindfoldResult>
    
    suspend fun getNonDomDaily(language: String): Result<NonDomDaily>
    suspend fun submitNonDom(request: NonDomSubmitRequest): Result<NonDomResult>
    
    suspend fun getStroopDaily(mode: String, language: String): Result<StroopDaily>
    suspend fun getStroopModes(): Result<List<String>>
    suspend fun getStroopWordSets(language: String): Result<StroopWordSets>
    suspend fun submitStroop(request: StroopSubmitRequest): Result<StroopResult>
    
    suspend fun downloadCorpus(exerciseType: String, language: String, params: Map<String, String>): Result<CorpusPackage>
}

interface ProgressRepository {
    suspend fun getDashboard(): Result<ProgressDashboard>
    suspend fun getExerciseProgress(exerciseType: String, days: Int): Result<ExerciseProgress>
    suspend fun getInsights(): Result<List<Insight>>
}

interface UserRepository {
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserProfile>
    suspend fun getPreferences(): Result<UserPreferences>
    suspend fun updatePreferences(request: UpdatePreferencesRequest): Result<UserPreferences>
    suspend fun getReminders(): Result<ReminderSettings>
    suspend fun updateReminders(request: ReminderSettings): Result<ReminderSettings>
    suspend fun getDifficultyPreferences(): Result<DifficultyPreferences>
    suspend fun updateDifficultyPreferences(request: DifficultyPreferences): Result<DifficultyPreferences>
    suspend fun register(request: RegisterRequest): Result<AuthResult>
    suspend fun login(request: LoginRequest): Result<AuthResult>
    suspend fun oauthLogin(provider: String, token: String): Result<AuthResult>
    suspend fun refreshToken(refreshToken: String): Result<AuthResult>
    suspend fun logout(): Result<Unit>
}