package com.mindtrainer.logic.stroop

import kotlin.math.abs
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Stroop Challenge Scorer
 * Calculates interference effects, switch costs, and composite indices.
 */
object StroopScorer {

    /**
     * Scores a complete Stroop session.
     */
    fun scoreSession(
        mode: StroopGenerator.StroopMode,
        trials: List<StroopTrial>,
        config: StroopGenerator.StroopConfig
    ): StroopResult {
        // Filter valid trials (with responses)
        val validTrials = trials.filter { it.response.isNotBlank() }
        
        // Separate by condition
        val congruentTrials = validTrials.filter { it.condition == StroopGenerator.StroopCondition.CONGRUENT }
        val incongruentTrials = validTrials.filter { it.condition == StroopGenerator.StroopCondition.INCONGRUENT }
        val neutralTrials = validTrials.filter { it.condition == StroopGenerator.StroopCondition.NEUTRAL }
        
        // Calculate mean RTs and accuracies
        val meanRTCongruent = meanRT(congruentTrials)
        val meanRTIncongruent = meanRT(incongruentTrials)
        val meanRTNeutral = meanRT(neutralTrials)
        
        val accCongruent = accuracy(congruentTrials)
        val accIncongruent = accuracy(incongruentTrials)
        val accNeutral = accuracy(neutralTrials)
        
        // Interference effect (classic Stroop effect)
        val interferenceEffect = meanRTIncongruent - meanRTCongruent
        val interferenceRatio = if (meanRTCongruent > 0) meanRTIncongruent / meanRTCongruent else 1.0
        
        // Mode-specific metrics
        val switchCost = when (mode) {
            StroopGenerator.StroopMode.SWITCH -> calculateSwitchCost(validTrials)
            else -> 0.0
        }
        
        val throughput = when (mode) {
            StroopGenerator.StroopMode.SPEED -> calculateThroughput(validTrials, config)
            else -> 0.0
        }
        
        // Inhibition Index: 1 - (interference / baseline)
        // Higher = better inhibition
        val baselineRT = max(meanRTCongruent, meanRTNeutral, 1.0)
        val inhibitionIndex = max(0.0, 1.0 - (interferenceEffect / baselineRT))
        
        // Flexibility Index: 1 - (switch cost / mean RT)
        val flexibilityIndex = when (mode) {
            StroopGenerator.StroopMode.SWITCH -> max(0.0, 1.0 - (switchCost / max(meanRTIncongruent, 1.0)))
            else -> 1.0
        }
        
        // Consistency: inverse of coefficient of variation
        val allRTs = validTrials.map { it.rtMs.toDouble() }
        val consistency = if (allRTs.size > 1) {
            val mean = allRTs.average()
            val stdDev = sqrt(allRTs.map { (it - mean).pow(2) }.average())
            if (mean > 0) 1.0 - (stdDev / mean).coerceIn(0.0, 1.0) else 0.5
        } else 0.5
        
        // Composite score
        val compositeScore = calculateCompositeScore(
            inhibitionIndex, flexibilityIndex, throughput,
            accIncongruent, consistency, mode
        )
        
        // Difficulty level calculation for adaptive progression
        val nextLevel = calculateNextLevel(
            compositeScore, config.difficultyLevel, accIncongruent
        )
        
        return StroopResult(
            mode = mode,
            totalTrials = validTrials.size,
            meanRTCongruent = meanRTCongruent,
            meanRTIncongruent = meanRTIncongruent,
            meanRTNeutral = meanRTNeutral,
            interferenceEffect = interferenceEffect,
            interferenceRatio = interferenceRatio,
            accuracyCongruent = accCongruent,
            accuracyIncongruent = accIncongruent,
            accuracyNeutral = accNeutral,
            switchCost = switchCost,
            throughput = throughput,
            inhibitionIndex = inhibitionIndex,
            flexibilityIndex = flexibilityIndex,
            consistency = consistency,
            compositeScore = compositeScore,
            currentLevel = config.difficultyLevel,
            nextLevel = nextLevel
        )
    }

    private fun meanRT(trials: List<StroopTrial>): Double {
        return if (trials.isNotEmpty()) trials.map { it.rtMs.toDouble() }.average() else 0.0
    }

    private fun accuracy(trials: List<StroopTrial>): Double {
        return if (trials.isNotEmpty()) trials.count { it.correct }.toDouble() / trials.size else 0.0
    }

    private fun calculateSwitchCost(trials: List<StroopTrial>): Double {
        val switchTrials = trials.filter { it.isSwitchTrial }
        val repeatTrials = trials.filter { !it.isSwitchTrial }
        
        val meanSwitchRT = meanRT(switchTrials)
        val meanRepeatRT = meanRT(repeatTrials)
        
        return max(0.0, meanSwitchRT - meanRepeatRT)
    }

    private fun calculateThroughput(trials: List<StroopTrial>, config: StroopGenerator.StroopConfig): Double {
        // Correct responses per second
        val correctCount = trials.count { it.correct }
        val totalTimeSec = trials.sumOf { it.rtMs.toDouble() } / 1000.0
        return if (totalTimeSec > 0) correctCount / totalTimeSec else 0.0
    }

    private fun calculateCompositeScore(
        inhibitionIndex: Double,
        flexibilityIndex: Double,
        throughput: Double,
        accuracy: Double,
        consistency: Double,
        mode: StroopGenerator.StroopMode
    ): Int {
        val weights = when (mode) {
            StroopGenerator.StroopMode.CLASSIC -> tripleOf(0.5, 0.0, 0.0)
            StroopGenerator.StroopMode.REVERSE -> tripleOf(0.5, 0.0, 0.0)
            StroopGenerator.StroopMode.SWITCH -> tripleOf(0.3, 0.3, 0.0)
            StroopGenerator.StroopMode.SPATIAL -> tripleOf(0.4, 0.0, 0.0)
            StroopGenerator.StroopMode.SPEED -> tripleOf(0.2, 0.0, 0.4)
            StroopGenerator.StroopMode.SEQUENCE -> tripleOf(0.4, 0.1, 0.1)
        }
        
        val throughputNormalized = min(1.0, throughput / 3.0) // normalize to ~3/sec max
        
        val score = (inhibitionIndex * weights.first * 100 +
            flexibilityIndex * weights.second * 100 +
            throughputNormalized * weights.third * 100 +
            accuracy * 0.2 * 100 +
            consistency * 0.1 * 100)
        
        return score.toInt().coerceIn(0, 100)
    }

    private fun calculateNextLevel(
        compositeScore: Int,
        currentLevel: Double,
        accuracy: Double
    ): Double {
        // Adaptive difficulty adjustment
        return when {
            compositeScore >= 85 && accuracy >= 0.9 -> (currentLevel + 0.05).coerceAtMost(1.0)
            compositeScore >= 70 && accuracy >= 0.8 -> (currentLevel + 0.03).coerceAtMost(1.0)
            compositeScore < 50 || accuracy < 0.6 -> (currentLevel - 0.03).coerceAtLeast(0.0)
            else -> currentLevel
        }
    }
}

/**
 * Individual trial data for scoring.
 */
data class StroopTrial(
    val stimulus: StroopGenerator.StroopStimulus,
    val response: String,           // User's response (color name or position)
    val rtMs: Long,                 // Reaction time in milliseconds
    val correct: Boolean,           // Whether response was correct
    val isSwitchTrial: Boolean = false, // For SWITCH mode
    val condition: StroopGenerator.StroopCondition = StroopGenerator.StroopCondition.CONGRUENT
)

/**
 * Complete session scoring result.
 */
data class StroopResult(
    val mode: StroopGenerator.StroopMode,
    val totalTrials: Int,
    val meanRTCongruent: Double,
    val meanRTIncongruent: Double,
    val meanRTNeutral: Double,
    val interferenceEffect: Double,      // ms
    val interferenceRatio: Double,       // ratio
    val accuracyCongruent: Double,
    val accuracyIncongruent: Double,
    val accuracyNeutral: Double,
    val switchCost: Double,              // ms (for SWITCH mode)
    val throughput: Double,              // correct/sec (for SPEED mode)
    val inhibitionIndex: Double,         // 0-1
    val flexibilityIndex: Double,        // 0-1
    val consistency: Double,             // 0-1
    val compositeScore: Int,             // 0-100
    val currentLevel: Double,
    val nextLevel: Double
) {
    /**
     * Human-readable summary.
     */
    fun summary(): String {
        return buildString {
            append("Stroop (${mode.name}) Results:\n")
            append("  Trials: $totalTrials\n")
            append("  Congruent RT: ${meanRTCongruent.toInt()}ms (${(accuracyCongruent * 100).toInt()}%)\n")
            append("  Incongruent RT: ${meanRTIncongruent.toInt()}ms (${(accuracyIncongruent * 100).toInt()}%)\n")
            append("  Interference: ${interferenceEffect.toInt()}ms (ratio: ${String.format("%.2f", interferenceRatio)})\n")
            append("  Inhibition Index: ${String.format("%.2f", inhibitionIndex)}\n")
            if (switchCost > 0) append("  Switch Cost: ${switchCost.toInt()}ms\n")
            if (throughput > 0) append("  Throughput: ${String.format("%.2f", throughput)}/sec\n")
            append("  Composite Score: $compositeScore/100\n")
            append("  Level: ${String.format("%.2f", currentLevel)} → ${String.format("%.2f", nextLevel)}")
        }
    }
}