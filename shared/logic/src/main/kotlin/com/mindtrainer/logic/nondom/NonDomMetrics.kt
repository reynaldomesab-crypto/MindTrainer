package com.mindtrainer.logic.nondom

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Non-Dominant Hand Metrics Calculator
 * Evaluates motor control performance across three task types:
 * copywriting, shape tracing, and tap sequences.
 */
object NonDomMetrics {

    /**
     * Evaluates copywriting performance with non-dominant hand.
     * Uses similar keyboard geometry as blindfold but with different weighting.
     */
    fun evaluateCopywriting(
        expected: String,
        actual: String,
        keyHoldDurations: List<Long>, // ms per character
        interKeyIntervals: List<Long>, // ms between keystrokes
        keyboardLayout: String = "QWERTY"
    ): CopywritingResult {
        val blindfoldResult = BlindfoldScorer.score(expected, actual, keyboardLayout)
        
        // Rhythm consistency: coefficient of variation of inter-key intervals
        val rhythmConsistency = if (interKeyIntervals.size > 1) {
            val mean = interKeyIntervals.average()
            val stdDev = sqrt(interKeyIntervals.map { (it - mean).pow(2) }.average())
            if (mean > 0) 1.0 - (stdDev / mean).coerceIn(0.0, 1.0) else 0.5
        } else 0.5
        
        // Hesitation index: proportion of long holds (>500ms)
        val hesitationIndex = if (keyHoldDurations.isNotEmpty()) {
            keyHoldDurations.count { it > 500 } / keyHoldDurations.size.toDouble()
        } else 0.0
        
        // Fatigue detection: increasing hold durations over time
        val fatigueIndex = if (keyHoldDurations.size > 10) {
            val firstHalf = keyHoldDurations.take(keyHoldDurations.size / 2).average()
            val secondHalf = keyHoldDurations.drop(keyHoldDurations.size / 2).average()
            if (firstHalf > 0) ((secondHalf - firstHalf) / firstHalf).coerceIn(-1.0, 1.0) else 0.0
        } else 0.0
        
        // Composite score
        val accuracyWeight = 0.5
        val rhythmWeight = 0.2
        val hesitationWeight = 0.15
        val fatigueWeight = 0.15
        
        val score = (blindfoldResult.accuracy * accuracyWeight +
            rhythmConsistency * rhythmWeight +
            (1.0 - hesitationIndex) * hesitationWeight +
            (1.0 - max(0.0, fatigueIndex)) * fatigueWeight) * 100
        
        return CopywritingResult(
            accuracy = blindfoldResult.accuracy,
            rmsError = blindfoldResult.rmsError,
            rhythmConsistency = rhythmConsistency,
            hesitationIndex = hesitationIndex,
            fatigueIndex = fatigueIndex,
            score = score.toInt().coerceIn(0, 100),
            errorBreakdown = blindfoldResult
        )
    }

    /**
     * Evaluates shape tracing performance.
     * Measures deviation from ideal path, velocity smoothness, and corner handling.
     */
    fun evaluateTracing(
        idealPath: List<Point>,
        actualPath: List<Point>,
        timestamps: List<Long>, // ms since start
        targetTimeMs: Long
    ): TracingResult {
        // Resample paths to same number of points for comparison
        val resampledIdeal = resamplePath(idealPath, 100)
        val resampledActual = resamplePath(actualPath, 100)
        
        // Path deviation (RMSE)
        var totalDeviation = 0.0
        for (i in resampledIdeal.indices) {
            val ideal = resampledIdeal[i]
            val actualPt = resampledActual[i]
            totalDeviation += hypot(ideal.x - actualPt.x, ideal.y - actualPt.y)
        }
        val rmse = totalDeviation / resampledIdeal.size
        
        // Velocity smoothness (jerk minimization)
        val velocities = calculateVelocities(resampledActual, timestamps)
        val smoothness = calculateSmoothness(velocities)
        
        // Completion time vs target
        val actualTime = if (timestamps.isNotEmpty()) timestamps.last() else targetTimeMs
        val timeRatio = (actualTime.toDouble() / targetTimeMs).coerceIn(0.5, 2.0)
        val timeScore = 1.0 / timeRatio // faster is better, but not too fast
        
        // Corner cutting vs overshooting
        val cornerAccuracy = evaluateCorners(idealPath, actualPath)
        
        // Composite metrics
        val deviationScore = max(0.0, 1.0 - rmse * 10) // normalize
        val score = (deviationScore * 0.5 + smoothness * 0.2 + timeScore * 0.2 + cornerAccuracy * 0.1) * 100
        
        return TracingResult(
            rmse = rmse,
            smoothness = smoothness,
            timeRatio = timeRatio,
            cornerAccuracy = cornerAccuracy,
            score = score.toInt().coerceIn(0, 100),
            completionTimeMs = actualTime
        )
    }

    /**
     * Evaluates tap sequence performance.
     * Measures spatial accuracy, timing, and sequence order.
     */
    fun evaluateTapping(
        targetGrid: List<Point>, // target positions (normalized 0-1)
        actualTaps: List<Point>,
        tapTimestamps: List<Long>, // ms since start
        targetSequence: List<Int>, // indices of targets in order
        actualSequence: List<Int>, // user's tap order
        gridSize: Int
    ): TappingResult {
        // Spatial accuracy: distance from target centers
        var totalDistance = 0.0
        var correctTargets = 0
        
        for (i in actualTaps.indices) {
            if (i < targetGrid.size) {
                val target = targetGrid[targetSequence[i]]
                val actual = actualTaps[i]
                val distance = hypot(target.x - actual.x, target.y - actual.y)
                totalDistance += distance
                
                // Consider correct if within 15% of grid cell size
                val cellSize = 1.0 / gridSize
                if (distance < cellSize * 0.15) correctTargets++
            }
        }
        
        val spatialAccuracy = if (actualTaps.isNotEmpty()) {
            1.0 - (totalDistance / actualTaps.size) / (1.0 / gridSize)
        } else 0.0
        
        // Sequence accuracy
        val sequenceAccuracy = if (targetSequence.isNotEmpty()) {
            var matches = 0
            for (i in targetSequence.indices) {
                if (i < actualSequence.size && actualSequence[i] == targetSequence[i]) matches++
            }
            matches.toDouble() / targetSequence.size
        } else 0.0
        
        // Timing consistency
        val intervals = if (tapTimestamps.size > 1) {
            (1 until tapTimestamps.size).map { tapTimestamps[it] - tapTimestamps[it - 1] }
        } else emptyList()
        
        val rhythmConsistency = if (intervals.size > 1) {
            val mean = intervals.average()
            val stdDev = sqrt(intervals.map { (it - mean).pow(2) }.average())
            if (mean > 0) 1.0 - (stdDev / mean).coerceIn(0.0, 1.0) else 0.5
        } else 0.5
        
        // Reaction time (first tap)
        val reactionTime = if (tapTimestamps.isNotEmpty()) tapTimestamps[0] else 0L
        
        // Composite score
        val score = (spatialAccuracy * 0.4 + sequenceAccuracy * 0.4 + rhythmConsistency * 0.2) * 100
        
        return TappingResult(
            spatialAccuracy = spatialAccuracy.coerceIn(0.0, 1.0),
            sequenceAccuracy = sequenceAccuracy,
            rhythmConsistency = rhythmConsistency,
            reactionTimeMs = reactionTime,
            avgIntervalMs = if (intervals.isNotEmpty()) intervals.average() else 0.0,
            score = score.toInt().coerceIn(0, 100)
        )
    }

    // ============ Helper Functions ============

    private fun resamplePath(path: List<Point>, targetCount: Int): List<Point> {
        if (path.size <= 1) return List(targetCount) { path.firstOrNull() ?: Point(0.0, 0.0) }
        
        // Calculate cumulative distances
        val distances = mutableListOf(0.0)
        for (i in 1 until path.size) {
            val d = hypot(path[i].x - path[i-1].x, path[i].y - path[i-1].y)
            distances.add(distances.last() + d)
        }
        
        val totalLength = distances.last()
        if (totalLength == 0.0) return List(targetCount) { path[0] }
        
        return (0 until targetCount).map { i ->
            val targetDist = (i / (targetCount - 1).toDouble()) * totalLength
            // Find segment
            var seg = 0
            while (seg < distances.size - 1 && distances[seg + 1] < targetDist) seg++
            
            if (seg >= path.size - 1) return@map path.last()
            
            val segStart = distances[seg]
            val segEnd = distances[seg + 1]
            val t = if (segEnd > segStart) (targetDist - segStart) / (segEnd - segStart) else 0.0
            
            Point(
                path[seg].x + (path[seg + 1].x - path[seg].x) * t,
                path[seg].y + (path[seg + 1].y - path[seg].y) * t
            )
        }
    }

    private fun calculateVelocities(path: List<Point>, timestamps: List<Long>): List<Double> {
        if (path.size != timestamps.size || path.size < 2) return emptyList()
        
        return (1 until path.size).map { i ->
            val dt = (timestamps[i] - timestamps[i-1]).toDouble() / 1000.0 // seconds
            if (dt <= 0) return@map 0.0
            val dist = hypot(path[i].x - path[i-1].x, path[i].y - path[i-1].y)
            dist / dt // units per second
        }
    }

    private fun calculateSmoothness(velocities: List<Double>): Double {
        if (velocities.size < 3) return 0.5
        
        // Calculate jerk (rate of change of acceleration)
        var totalJerk = 0.0
        for (i in 2 until velocities.size) {
            val accel1 = velocities[i-1] - velocities[i-2]
            val accel2 = velocities[i] - velocities[i-1]
            val jerk = abs(accel2 - accel1)
            totalJerk += jerk
        }
        
        val avgJerk = totalJerk / (velocities.size - 2)
        // Normalize: lower jerk = smoother
        return (1.0 / (1.0 + avgJerk * 10)).coerceIn(0.0, 1.0)
    }

    private fun evaluateCorners(idealPath: List<Point>, actualPath: List<Point>): Double {
        // Simplified: detect sharp turns in ideal path and check if actual follows
        if (idealPath.size < 3) return 1.0
        
        var correctCorners = 0
        var totalCorners = 0
        
        for (i in 1 until idealPath.size - 1) {
            val v1 = Point(idealPath[i].x - idealPath[i-1].x, idealPath[i].y - idealPath[i-1].y)
            val v2 = Point(idealPath[i+1].x - idealPath[i].x, idealPath[i+1].y - idealPath[i].y)
            
            val angle = kotlin.math.atan2(v1.x * v2.y - v1.y * v2.x, v1.x * v2.x + v1.y * v2.y)
            
            // Sharp turn threshold (~90 degrees)
            if (abs(angle) > 1.0) { // ~57 degrees
                totalCorners++
                // Check if actual path has similar turn nearby
                val actualIdx = (i * actualPath.size / idealPath.size).coerceIn(1, actualPath.size - 2)
                val av1 = Point(actualPath[actualIdx].x - actualPath[actualIdx-1].x, actualPath[actualIdx].y - actualPath[actualIdx-1].y)
                val av2 = Point(actualPath[actualIdx+1].x - actualPath[actualIdx].x, actualPath[actualIdx+1].y - actualPath[actualIdx].y)
                val aAngle = kotlin.math.atan2(av1.x * av2.y - av1.y * av2.x, av1.x * av2.x + av1.y * av2.y)
                
                if (abs(aAngle) > 0.5 && (angle > 0) == (aAngle > 0)) {
                    correctCorners++
                }
            }
        }
        
        return if (totalCorners > 0) correctCorners.toDouble() / totalCorners else 1.0
    }
}

/**
 * 2D Point for path coordinates.
 */
data class Point(val x: Double, val y: Double)

/**
 * Copywriting evaluation result.
 */
data class CopywritingResult(
    val accuracy: Double,
    val rmsError: Double,
    val rhythmConsistency: Double,
    val hesitationIndex: Double,
    val fatigueIndex: Double,
    val score: Int,
    val errorBreakdown: BlindfoldScorer.BlindfoldResult
)

/**
 * Tracing evaluation result.
 */
data class TracingResult(
    val rmse: Double,
    val smoothness: Double,
    val timeRatio: Double,
    val cornerAccuracy: Double,
    val score: Int,
    val completionTimeMs: Long
)

/**
 * Tapping evaluation result.
 */
data class TappingResult(
    val spatialAccuracy: Double,
    val sequenceAccuracy: Double,
    val rhythmConsistency: Double,
    val reactionTimeMs: Long,
    val avgIntervalMs: Double,
    val score: Int
)