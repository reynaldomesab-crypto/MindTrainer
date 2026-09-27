package com.mindtrainer.data.repository

import com.mindtrainer.data.api.MindTrainerApi
import com.mindtrainer.data.api.dto.*
import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.model.Result
import com.mindtrainer.domain.repository.ProgressRepository
import java.util.UUID
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val api: MindTrainerApi
) : ProgressRepository {

    override suspend fun getDashboard(): Result<ProgressDashboard> {
        return try {
            val response = api.getDashboard()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapDashboard(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to get progress dashboard")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun getExerciseProgress(exerciseType: String, days: Int): Result<ExerciseProgress> {
        return try {
            val response = api.getExerciseProgress(exerciseType, days)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(mapExerciseProgress(body))
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to get exercise progress")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    override suspend fun getInsights(): Result<List<Insight>> {
        return try {
            val response = api.getInsights()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.Success(body.map { mapInsight(it) })
            } else {
                Result.Error(Exception("API Error: ${response.code()}"), "Failed to get insights")
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }

    private fun mapDashboard(dto: ProgressDashboardResponse): ProgressDashboard {
        return ProgressDashboard(
            todayStatus = dto.todayStatus.map { mapDailyStatus(it) },
            exerciseProgress = dto.exerciseProgress.mapValues { (_, v) -> mapExerciseProgress(v) },
            globalMetrics = mapGlobalMetrics(dto.globalMetrics),
            streaks = mapStreakData(dto.streaks),
            insights = dto.insights.map { mapInsight(it) }
        )
    }

    private fun mapDailyStatus(dto: DailyExerciseStatus): DailyExerciseStatus {
        return DailyExerciseStatus(
            exerciseType = dto.exerciseType,
            completed = dto.completed,
            completedAt = dto.completedAt?.let { Instant.parse(it) },
            sessionId = dto.sessionId?.let { UUID.fromString(it) }
        )
    }

    private fun mapExerciseProgress(dto: ExerciseProgressResponse): ExerciseProgress {
        return ExerciseProgress(
            exerciseType = dto.exerciseType,
            currentLevel = dto.currentLevel,
            sessionsCompleted = dto.sessionsCompleted,
            lastSession = dto.lastSession?.let { Instant.parse(it) },
            trend = TrendDirection.valueOf(dto.trend),
            personalBests = dto.personalBests,
            history = dto.history.map { mapHistoryItem(it) }
        )
    }

    private fun mapHistoryItem(dto: SessionHistoryItem): SessionHistoryItem {
        return SessionHistoryItem(
            date = java.time.LocalDate.parse(dto.date),
            score = dto.score,
            level = dto.level,
            metrics = dto.metrics
        )
    }

    private fun mapGlobalMetrics(dto: GlobalMetricsResponse): GlobalMetrics {
        return GlobalMetrics(
            cognitiveAge = dto.cognitiveAge,
            consistencyScore = dto.consistencyScore,
            focusIndex = dto.focusIndex,
            motorSymmetry = dto.motorSymmetry,
            weeklyActiveDays = dto.weeklyActiveDays
        )
    }

    private fun mapStreakData(dto: StreakDataResponse): StreakData {
        return StreakData(
            current = dto.current,
            longest = dto.longest,
            lastActiveDate = java.time.LocalDate.parse(dto.lastActiveDate),
            streakFreezeUsed = dto.streakFreezeUsed
        )
    }

    private fun mapInsight(dto: InsightResponse): Insight {
        return Insight(
            id = dto.id,
            type = dto.type,
            title = dto.title,
            description = dto.description,
            generatedAt = Instant.parse(dto.generatedAt),
            readAt = dto.readAt?.let { Instant.parse(it) }
        )
    }
}