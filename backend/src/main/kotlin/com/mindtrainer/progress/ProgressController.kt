package com.mindtrainer.progress

import com.mindtrainer.common.security.CurrentUser
import com.mindtrainer.data.api.dto.*
import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.usecase.GetProgressUseCase
import com.mindtrainer.domain.usecase.GetInsightsUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/progress")
@Tag(name = "Progress", description = "User progress and analytics")
class ProgressController(
    private val getProgressUseCase: GetProgressUseCase,
    private val getInsightsUseCase: GetInsightsUseCase
) {

    @GetMapping("/dashboard")
    @Operation(summary = "Get progress dashboard")
    fun getDashboard(
        @AuthenticationPrincipal user: CurrentUser
    ): ResponseEntity<ProgressDashboardResponse> {
        val result = getProgressUseCase()
        
        return when (result) {
            is Result.Success -> {
                val dashboard = result.data as ProgressDashboard
                ResponseEntity.ok(mapToResponse(dashboard))
            }
            is Result.Error -> ResponseEntity.status(500).build()
        }
    }

    @GetMapping("/exercise/{type}")
    @Operation(summary = "Get exercise-specific progress")
    fun getExerciseProgress(
        @AuthenticationPrincipal user: CurrentUser,
        @PathVariable type: String,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<ExerciseProgressResponse> {
        val result = getProgressUseCase(type, days)
        
        return when (result) {
            is Result.Success -> {
                val progress = result.data as ExerciseProgress
                ResponseEntity.ok(mapToResponse(progress))
            }
            is Result.Error -> ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/insights")
    @Operation(summary = "Get AI-generated insights")
    fun getInsights(
        @AuthenticationPrincipal user: CurrentUser
    ): ResponseEntity<List<InsightResponse>> {
        val result = getInsightsUseCase()
        
        return when (result) {
            is Result.Success -> ResponseEntity.ok(result.data.map { mapToResponse(it) })
            is Result.Error -> ResponseEntity.ok(emptyList())
        }
    }

    private fun mapToResponse(dashboard: ProgressDashboard): ProgressDashboardResponse {
        return ProgressDashboardResponse(
            todayStatus = dashboard.todayStatus.map { mapToResponse(it) },
            exerciseProgress = dashboard.exerciseProgress.mapValues { (_, v) -> mapToResponse(v) },
            globalMetrics = mapToResponse(dashboard.globalMetrics),
            streaks = mapToResponse(dashboard.streaks),
            insights = dashboard.insights.map { mapToResponse(it) }
        )
    }

    private fun mapToResponse(status: DailyExerciseStatus): DailyExerciseStatusResponse {
        return DailyExerciseStatusResponse(
            exerciseType = status.exerciseType.name,
            completed = status.completed,
            completedAt = status.completedAt?.toString(),
            sessionId = status.sessionId?.toString()
        )
    }

    private fun mapToResponse(progress: ExerciseProgress): ExerciseProgressResponse {
        return ExerciseProgressResponse(
            exerciseType = progress.exerciseType.name,
            currentLevel = progress.currentLevel,
            sessionsCompleted = progress.sessionsCompleted,
            lastSession = progress.lastSession?.toString(),
            trend = progress.trend.name,
            personalBests = progress.personalBests,
            history = progress.history.map { mapToResponse(it) }
        )
    }

    private fun mapToResponse(history: SessionHistoryItem): SessionHistoryItemResponse {
        return SessionHistoryItemResponse(
            date = history.date.toString(),
            score = history.score,
            level = history.level,
            metrics = history.metrics
        )
    }

    private fun mapToResponse(metrics: GlobalMetrics): GlobalMetricsResponse {
        return GlobalMetricsResponse(
            cognitiveAge = metrics.cognitiveAge,
            consistencyScore = metrics.consistencyScore,
            focusIndex = metrics.focusIndex,
            motorSymmetry = metrics.motorSymmetry,
            weeklyActiveDays = metrics.weeklyActiveDays
        )
    }

    private fun mapToResponse(streaks: StreakData): StreakDataResponse {
        return StreakDataResponse(
            current = streaks.current,
            longest = streaks.longest,
            lastActiveDate = streaks.lastActiveDate.toString(),
            streakFreezeUsed = streaks.streakFreezeUsed
        )
    }

    private fun mapToResponse(insight: Insight): InsightResponse {
        return InsightResponse(
            id = insight.id,
            type = insight.type,
            title = insight.title,
            description = insight.description,
            generatedAt = insight.generatedAt.toString(),
            readAt = insight.readAt?.toString()
        )
    }
}