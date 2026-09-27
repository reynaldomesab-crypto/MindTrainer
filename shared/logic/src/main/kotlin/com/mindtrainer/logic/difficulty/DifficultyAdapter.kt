package com.mindtrainer.logic.difficulty

import kotlin.math.max
import kotlin.math.min

/**
 * Adaptive Difficulty Engine
 * Adjusts exercise difficulty based on user performance with plateau detection.
 */
object DifficultyAdapter {

    /**
     * Difficulty preferences that affect adaptation behavior.
     */
    enum class Preference {
        CHALLENGE,  // Aggressive progression, tolerate more failure
        MAINTAIN,   // Balanced progression
        RELAX       // Conservative, prioritize success and comfort
    }

    /**
     * Current difficulty state for an exercise type.
     */
    data class DifficultyState(
        val exerciseType: String,
        var currentLevel: Double = 0.0,        // 0.0 to 1.0
        var recentScores: MutableList<Int> = mutableListOf(), // Last 20 sessions
        var recentAccuracies: MutableList<Double> = mutableListOf(),
        var recentRTs: MutableList<Double> = mutableListOf(),
        var sessionsSinceChange: Int = 0,
        var plateauDetected: Boolean = false,
        var totalSessions: Int = 0,
        var preference: Preference = Preference.MAINTAIN
    ) {
        val isAtMinimum: Boolean
            get() = currentLevel <= 0.0
        
        val isAtMaximum: Boolean
            get() = currentLevel >= 1.0
        
        val windowSize: Int = 7 // sessions for trend calculation
    }

    /**
     * Updates difficulty based on session performance.
     * Returns the new level and whether it changed.
     */
    fun updateDifficulty(
        state: DifficultyState,
        score: Int,              // 0-100 composite score
        accuracy: Double,        // 0.0-1.0
        rtMs: Double,            // Reaction time in ms
        preference: Preference = Preference.MAINTAIN
    ): Pair<Double, Boolean> {
        state.preference = preference
        state.totalSessions++
        state.recentScores.add(score)
        state.recentAccuracies.add(accuracy)
        state.recentRTs.add(rtMs)
        
        // Keep rolling window
        val window = 20
        if (state.recentScores.size > window) {
            state.recentScores.removeAt(0)
            state.recentAccuracies.removeAt(0)
            state.recentRTs.removeAt(0)
        }
        
        // Calculate performance trend
        val trend = calculateTrend(state)
        val isImproving = trend > 0.02
        val isDeclining = trend < -0.02
        val isStable = !isImproving && !isDeclining
        
        // Plateau detection: no improvement for 14+ sessions
        if (isImproving) {
            state.sessionsSinceChange = 0
            state.plateauDetected = false
        } else {
            state.sessionsSinceChange++
            if (state.sessionsSinceChange >= 14 && state.recentScores.average() > 70) {
                state.plateauDetected = true
            }
        }
        
        // Determine level adjustment
        val adjustment = when {
            isImproving -> calculateIncrease(preference, state.currentLevel, accuracy)
            isDeclining -> calculateDecrease(preference, state.currentLevel, accuracy)
            state.plateauDetected -> calculatePlateauAdjustment(preference, state.currentLevel)
            else -> 0.0
        }
        
        val newLevel = (state.currentLevel + adjustment).coerceIn(0.0, 1.0)
        val changed = abs(newLevel - state.currentLevel) > 0.001
        state.currentLevel = newLevel
        
        if (changed) state.sessionsSinceChange = 0
        
        return newLevel to changed
    }

    private fun calculateTrend(state: DifficultyState): Double {
        val scores = state.recentScores
        if (scores.size < 3) return 0.0
        
        // Linear regression slope over recent sessions
        val n = scores.size.toDouble()
        val x = (0 until scores.size).map { it.toDouble() }
        val y = scores.map { it.toDouble() }
        
        val sumX = x.sum()
        val sumY = y.sum()
        val sumXY = x.zip(y).map { (xi, yi) -> xi * yi }.sum()
        val sumX2 = x.map { it * it }.sum()
        
        val slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
        return slope / 100.0 // Normalize to 0-1 scale
    }

    private fun calculateIncrease(preference: Preference, currentLevel: Double, accuracy: Double): Double {
        val baseStep = when (preference) {
            Preference.CHALLENGE -> 0.06
            Preference.MAINTAIN -> 0.04
            Preference.RELAX -> 0.02
        }
        
        // Reduce step size at higher levels
        val levelFactor = 1.0 - currentLevel * 0.5
        // Increase step if accuracy is very high
        val accuracyFactor = if (accuracy > 0.95) 1.5 else if (accuracy > 0.9) 1.2 else 1.0
        
        return baseStep * levelFactor * accuracyFactor
    }

    private fun calculateDecrease(preference: Preference, currentLevel: Double, accuracy: Double): Double {
        val baseStep = when (preference) {
            Preference.CHALLENGE -> 0.04
            Preference.MAINTAIN -> 0.03
            Preference.RELAX -> 0.02
        }
        
        // Larger decrease if accuracy is very low
        val accuracyFactor = if (accuracy < 0.5) 1.5 else if (accuracy < 0.7) 1.2 else 1.0
        
        return -(baseStep * accuracyFactor)
    }

    private fun calculatePlateauAdjustment(preference: Preference, currentLevel: Double): Double {
        // Small nudge to break plateau
        return when (preference) {
            Preference.CHALLENGE -> 0.02
            Preference.MAINTAIN -> 0.01
            Preference.RELAX -> 0.0
        }
    }

    /**
     * Gets difficulty parameters for a specific exercise at a given level.
     */
    fun getExerciseParams(exerciseType: String, level: Double): Map<String, Any> {
        return when (exerciseType) {
            "SCHULTE" -> getSchulteParams(level)
            "BLINDFOLD" -> getBlindfoldParams(level)
            "NON_DOMINANT" -> getNonDominantParams(level)
            "STROOP" -> getStroopParams(level)
            else -> emptyMap()
        }
    }

    private fun getSchulteParams(level: Double): Map<String, Any> {
        val size = when {
            level < 0.2 -> 4
            level < 0.4 -> 5
            level < 0.6 -> 5
            level < 0.8 -> 6
            else -> 7
        }
        
        val timeLimit = when {
            level < 0.2 -> 60000
            level < 0.4 -> 45000
            level < 0.6 -> 30000
            level < 0.8 -> 30000
            else -> 20000
        }
        
        return mapOf(
            "size" to size,
            "timeLimitMs" to timeLimit,
            "showNumbers" to true
        )
    }

    private fun getBlindfoldParams(level: Double): Map<String, Any> {
        val tier = when {
            level < 0.33 -> 1
            level < 0.66 -> 2
            else -> 3
        }
        
        val charRange = when (tier) {
            1 -> 200..250
            2 -> 250..300
            else -> 300..350
        }
        
        val maxRmsError = when (tier) {
            1 -> 1.5
            2 -> 1.2
            else -> 1.0
        }
        
        return mapOf(
            "tier" to tier,
            "charRange" to charRange,
            "maxRmsError" to maxRmsError
        )
    }

    private fun getNonDominantParams(level: Double): Map<String, Any> {
        val tasks = when {
            level < 0.25 -> listOf("COPYWRITING")
            level < 0.5 -> listOf("COPYWRITING", "TRACING")
            else -> listOf("COPYWRITING", "TRACING", "TAPPING")
        }
        
        val copyTier = when {
            level < 0.33 -> 1
            level < 0.66 -> 2
            else -> 3
        }
        
        val traceComplexity = when {
            level < 0.3 -> 1
            level < 0.6 -> 2
            level < 0.8 -> 3
            else -> 4
        }
        
        val tapGridSize = when {
            level < 0.4 -> 3
            level < 0.7 -> 4
            else -> 5
        }
        
        return mapOf(
            "tasks" to tasks,
            "copyTier" to copyTier,
            "traceComplexity" to traceComplexity,
            "tapGridSize" to tapGridSize
        )
    }

    private fun getStroopParams(level: Double): Map<String, Any> {
        val colorCount = when {
            level < 0.3 -> 4
            level < 0.6 -> 5
            else -> 6
        }
        
        val incongruentRatio = when {
            level < 0.2 -> 0.2
            level < 0.4 -> 0.4
            level < 0.6 -> 0.5
            level < 0.8 -> 0.6
            else -> 0.8
        }
        
        val stimulusDuration = when {
            level < 0.3 -> 1000
            level < 0.5 -> 800
            level < 0.7 -> 600
            else -> 400
        }
        
        val switchFrequency = when {
            level < 0.4 -> 0.2
            level < 0.7 -> 0.3
            else -> 0.4
        }
        
        return mapOf(
            "colorCount" to colorCount,
            "incongruentRatio" to incongruentRatio,
            "stimulusDurationMs" to stimulusDuration,
            "switchFrequency" to switchFrequency,
            "mode" to when {
                level < 0.3 -> "CLASSIC"
                level < 0.6 -> "SWITCH"
                else -> "SPEED"
            }
        )
    }

    /**
     * Creates initial difficulty state for a new user.
     */
    fun createInitialState(exerciseType: String, preference: Preference = Preference.MAINTAIN): DifficultyState {
        val initialLevel = when (preference) {
            Preference.CHALLENGE -> 0.3
            Preference.MAINTAIN -> 0.1
            Preference.RELAX -> 0.0
        }
        
        return DifficultyState(
            exerciseType = exerciseType,
            currentLevel = initialLevel,
            preference = preference
        )
    }

    /**
     * Serializes state for storage.
     */
    fun serialize(state: DifficultyState): String {
        return """
            {
                "exerciseType": "${state.exerciseType}",
                "currentLevel": ${state.currentLevel},
                "recentScores": [${state.recentScores.joinToString(",")}],
                "recentAccuracies": [${state.recentAccuracies.joinToString(",")}],
                "recentRTs": [${state.recentRTs.joinToString(",")}],
                "sessionsSinceChange": ${state.sessionsSinceChange},
                "plateauDetected": ${state.plateauDetected},
                "totalSessions": ${state.totalSessions},
                "preference": "${state.preference.name}"
            }
        """.trimIndent()
    }

    /**
     * Deserializes state from storage.
     */
    fun deserialize(json: String): DifficultyState {
        // Simple parsing - in production use a proper JSON parser
        val level = json.substringAfter("currentLevel": ).substringBefore(",").toDoubleOrNull() ?: 0.0
        val prefStr = json.substringAfter("preference\": \"").substringBefore("\"")
        val preference = Preference.valueOf(prefStr)
        
        return DifficultyState(
            exerciseType = json.substringAfter("exerciseType\": \"").substringBefore("\""),
            currentLevel = level,
            preference = preference
        )
    }
}