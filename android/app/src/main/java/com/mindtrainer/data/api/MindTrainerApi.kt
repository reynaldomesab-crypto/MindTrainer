package com.mindtrainer.data.api

import com.mindtrainer.data.api.dto.*
import retrofit2.Response
import retrofit2.http.*

interface MindTrainerApi {

    // Auth
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/oauth/{provider}")
    suspend fun oauthLogin(
        @Path("provider") provider: String,
        @Body request: OAuthRequest
    ): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    // User
    @GET("users/me")
    suspend fun getProfile(): Response<UserProfileResponse>

    @PATCH("users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>

    @GET("users/me/preferences")
    suspend fun getPreferences(): Response<UserPreferencesResponse>

    @PATCH("users/me/preferences")
    suspend fun updatePreferences(@Body request: UserPreferencesRequest): Response<UserPreferencesResponse>

    @GET("users/me/reminders")
    suspend fun getReminders(): Response<ReminderSettingsResponse>

    @PATCH("users/me/reminders")
    suspend fun updateReminders(@Body request: ReminderSettingsRequest): Response<ReminderSettingsResponse>

    @GET("users/me/difficulty-preference")
    suspend fun getDifficultyPreferences(): Response<DifficultyPreferencesResponse>

    @PATCH("users/me/difficulty-preference")
    suspend fun updateDifficultyPreferences(@Body request: DifficultyPreferencesRequest): Response<DifficultyPreferencesResponse>

    // Exercises - Schulte
    @GET("exercises/schulte/daily")
    suspend fun getSchulteDaily(@Query("size") size: Int = 5): Response<SchulteDailyResponse>

    @GET("exercises/schulte/package")
    suspend fun getSchultePackage(
        @Query("count") count: Int = 500,
        @Query("size") size: Int = 5
    ): Response<SchultePackageResponse>

    @POST("exercises/schulte/submit")
    suspend fun submitSchulte(@Body request: SchulteSessionRequest): Response<SchulteSessionResponse>

    // Exercises - Blindfold
    @GET("exercises/blindfold/daily")
    suspend fun getBlindfoldDaily(@Query("lang") lang: String = "en"): Response<BlindfoldDailyResponse>

    @POST("exercises/blindfold/submit")
    suspend fun submitBlindfold(@Body request: BlindfoldSessionRequest): Response<BlindfoldSessionResponse>

    // Exercises - Non-Dominant
    @GET("exercises/nondom/daily")
    suspend fun getNonDomDaily(@Query("lang") lang: String = "en"): Response<NonDomDailyResponse>

    @POST("exercises/nondom/submit")
    suspend fun submitNonDom(@Body request: NonDomSessionRequest): Response<NonDomSessionResponse>

    // Exercises - Stroop
    @GET("exercises/stroop/daily")
    suspend fun getStroopDaily(
        @Query("mode") mode: String = "CLASSIC",
        @Query("lang") lang: String = "en"
    ): Response<StroopDailyResponse>

    @GET("exercises/stroop/modes")
    suspend fun getStroopModes(): Response<List<String>>

    @GET("exercises/stroop/wordsets")
    suspend fun getStroopWordSets(@Query("lang") lang: String): Response<StroopWordSetsResponse>

    @POST("exercises/stroop/submit")
    suspend fun submitStroop(@Body request: StroopSessionRequest): Response<StroopSessionResponse>

    // Progress
    @GET("progress/dashboard")
    suspend fun getDashboard(): Response<ProgressDashboardResponse>

    @GET("progress/exercise/{type}")
    suspend fun getExerciseProgress(
        @Path("type") type: String,
        @Query("days") days: Int = 30
    ): Response<ExerciseProgressResponse>

    @GET("progress/insights")
    suspend fun getInsights(): Response<List<InsightResponse>>

    // Corpus
    @GET("corpus/schulte/package")
    suspend fun getSchulteCorpus(
        @Query("lang") lang: String,
        @Query("count") count: Int = 500,
        @Query("size") size: Int = 5
    ): Response<CorpusPackageResponse>

    @GET("corpus/blindfold/texts")
    suspend fun getBlindfoldCorpus(
        @Query("lang") lang: String,
        @Query("tier") tier: Int? = null,
        @Query("limit") limit: Int = 50
    ): Response<CorpusPackageResponse>

    @GET("corpus/nondom/texts")
    suspend fun getNonDomCorpus(
        @Query("lang") lang: String,
        @Query("limit") limit: Int = 50
    ): Response<CorpusPackageResponse>

    @GET("corpus/nondom/paths")
    suspend fun getNonDomPaths(): Response<CorpusPackageResponse>

    @GET("corpus/nondom/sequences")
    suspend fun getNonDomSequences(): Response<CorpusPackageResponse>
}