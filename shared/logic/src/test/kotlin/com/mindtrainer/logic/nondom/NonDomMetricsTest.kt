package com.mindtrainer.logic.nondom

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NonDomMetricsTest {

    @Test
    fun `evaluateTracing detects perfect trace`() {
        val idealPath = listOf(
            Point(0.1, 0.5), Point(0.3, 0.5), Point(0.5, 0.5), Point(0.7, 0.5), Point(0.9, 0.5)
        )
        val actualPath = idealPath.map { it.copy() }
        val timestamps = listOf(0L, 250L, 500L, 750L, 1000L)
        
        val result = NonDomMetrics.evaluateTracing(idealPath, actualPath, timestamps, 1000)
        
        assertEquals(0.0, result.rmse, 0.001)
        assertEquals(1.0, result.smoothness, 0.1)
        assertEquals(1.0, result.cornerAccuracy, 0.1)
        assertEquals(1.0, result.timeRatio, 0.1)
    }

    @Test
    fun `evaluateTracing detects deviation`() {
        val idealPath = listOf(
            Point(0.1, 0.5), Point(0.3, 0.5), Point(0.5, 0.5), Point(0.7, 0.5), Point(0.9, 0.5)
        )
        val actualPath = listOf(
            Point(0.1, 0.5), Point(0.3, 0.55), Point(0.5, 0.5), Point(0.7, 0.45), Point(0.9, 0.5)
        )
        val timestamps = listOf(0L, 250L, 500L, 750L, 1000L)
        
        val result = NonDomMetrics.evaluateTracing(idealPath, actualPath, timestamps, 1000)
        
        assertTrue(result.rmse > 0.0)
        assertTrue(result.rmse < 0.1)
    }

    @Test
    fun `evaluateTracing calculates time ratio`() {
        val idealPath = listOf(Point(0.1, 0.5), Point(0.9, 0.5))
        val actualPath = idealPath.map { it.copy() }
        val fastTimestamps = listOf(0L, 500L)
        val slowTimestamps = listOf(0L, 2000L)
        
        val fastResult = NonDomMetrics.evaluateTracing(idealPath, actualPath, fastTimestamps, 1000)
        val slowResult = NonDomMetrics.evaluateTracing(idealPath, actualPath, slowTimestamps, 1000)
        
        assertTrue(fastResult.timeRatio < slowResult.timeRatio)
    }

    @Test
    fun `evaluateTapping detects perfect taps`() {
        val targetGrid = listOf(
            Point(0.1, 0.1), Point(0.5, 0.1), Point(0.9, 0.1),
            Point(0.1, 0.5), Point(0.5, 0.5), Point(0.9, 0.5),
            Point(0.1, 0.9), Point(0.5, 0.9), Point(0.9, 0.9)
        )
        val targetSequence = listOf(0, 4, 8)
        val actualTaps = listOf(targetGrid[0], targetGrid[4], targetGrid[8])
        val tapTimestamps = listOf(0L, 500L, 1000L)
        
        val result = NonDomMetrics.evaluateTapping(
            targetGrid, actualTaps, tapTimestamps, targetSequence, listOf(0, 4, 8), 3
        )
        
        assertEquals(1.0, result.spatialAccuracy, 0.01)
        assertEquals(1.0, result.sequenceAccuracy, 0.01)
    }

    @Test
    fun `evaluateTapping detects spatial errors`() {
        val targetGrid = listOf(
            Point(0.1, 0.1), Point(0.5, 0.1), Point(0.9, 0.1),
            Point(0.1, 0.5), Point(0.5, 0.5), Point(0.9, 0.5),
            Point(0.1, 0.9), Point(0.5, 0.9), Point(0.9, 0.9)
        )
        val targetSequence = listOf(0, 4, 8)
        val actualTaps = listOf(
            Point(0.15, 0.15), // Slightly off target 0
            Point(0.5, 0.5),   // Perfect target 4
            Point(0.85, 0.85)  // Slightly off target 8
        )
        val tapTimestamps = listOf(0L, 500L, 1000L)
        
        val result = NonDomMetrics.evaluateTapping(
            targetGrid, actualTaps, tapTimestamps, targetSequence, listOf(0, 4, 8), 3
        )
        
        assertTrue(result.spatialAccuracy < 1.0)
        assertEquals(1.0, result.sequenceAccuracy, 0.01)
    }

    @Test
    fun `evaluateTapping detects sequence errors`() {
        val targetGrid = listOf(
            Point(0.1, 0.1), Point(0.5, 0.1), Point(0.9, 0.1),
            Point(0.1, 0.5), Point(0.5, 0.5), Point(0.9, 0.5),
            Point(0.1, 0.9), Point(0.5, 0.9), Point(0.9, 0.9)
        )
        val targetSequence = listOf(0, 4, 8)
        val actualTaps = listOf(targetGrid[0], targetGrid[8], targetGrid[4]) // Wrong order
        val tapTimestamps = listOf(0L, 500L, 1000L)
        
        val result = NonDomMetrics.evaluateTapping(
            targetGrid, actualTaps, tapTimestamps, targetSequence, listOf(0, 8, 4), 3
        )
        
        assertEquals(1.0, result.spatialAccuracy, 0.01)
        assertTrue(result.sequenceAccuracy < 1.0)
    }

    @Test
    fun `evaluateTapping calculates rhythm consistency`() {
        val targetGrid = listOf(
            Point(0.1, 0.1), Point(0.5, 0.1), Point(0.9, 0.1),
            Point(0.1, 0.5), Point(0.5, 0.5), Point(0.9, 0.5)
        )
        val targetSequence = listOf(0, 1, 2, 3, 4, 5)
        val actualTaps = listOf(
            targetGrid[0], targetGrid[1], targetGrid[2], targetGrid[3], targetGrid[4], targetGrid[5]
        )
        // Perfect rhythm: 200ms intervals
        val perfectTimestamps = listOf(0L, 200L, 400L, 600L, 800L, 1000L)
        // Variable rhythm
        val variableTimestamps = listOf(0L, 150L, 500L, 600L, 900L, 1000L)
        
        val perfectResult = NonDomMetrics.evaluateTapping(
            targetGrid, actualTaps, perfectTimestamps, targetSequence, targetSequence, 3
        )
        val variableResult = NonDomMetrics.evaluateTapping(
            targetGrid, actualTaps, variableTimestamps, targetSequence, targetSequence, 3
        )
        
        assertTrue(perfectResult.rhythmConsistency > variableResult.rhythmConsistency)
    }

    @Test
    fun `calculateConsistency returns 1 for constant values`() {
        val consistent = NonDomMetrics.calculateConsistency(listOf(100.0, 100.0, 100.0, 100.0))
        assertEquals(1.0, consistent, 0.001)
    }

    @Test
    fun `calculateConsistency returns low for variable values`() {
        val variable = NonDomMetrics.calculateConsistency(listOf(100.0, 200.0, 100.0, 200.0))
        assertTrue(variable < 0.5)
    }

    @Test
    fun `calculateFatigue detects increasing hold times`() {
        val increasing = listOf(100.0, 120.0, 140.0, 160.0, 180.0, 200.0, 220.0, 240.0, 260.0, 280.0)
        val constant = listOf(150.0, 150.0, 150.0, 150.0, 150.0, 150.0, 150.0, 150.0, 150.0, 150.0)
        
        val increasingFatigue = NonDomMetrics.calculateFatigue(increasing)
        val constantFatigue = NonDomMetrics.calculateFatigue(constant)
        
        assertTrue(increasingFatigue > 0.0)
        assertEquals(0.0, constantFatigue, 0.001)
    }
}