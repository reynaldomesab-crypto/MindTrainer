package com.mindtrainer.logic.difficulty

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DifficultyAdapterTest {

    @Test
    fun `updateDifficulty increases level for improving performance`() {
        val state = DifficultyAdapter.DifficultyState(
            exerciseType = "SCHULTE",
            currentLevel = 0.3,
            recentScores = mutableListOf(60, 65, 70, 75, 80),
            recentAccuracies = mutableListOf(0.7, 0.75, 0.8, 0.85, 0.9),
            recentRTs = mutableListOf(25000.0, 23000.0, 21000.0, 19000.0, 17000.0),
            preference = DifficultyAdapter.Preference.MAINTAIN
        )
        
        val (newLevel, changed) = DifficultyAdapter.updateDifficulty(state, 85, 0.9, 16000.0)
        
        assertTrue(changed)
        assertTrue(newLevel > state.currentLevel)
        assertTrue(newLevel <= 1.0)
    }

    @Test
    fun `updateDifficulty decreases level for declining performance`() {
        val state = DifficultyAdapter.DifficultyState(
            exerciseType = "SCHULTE",
            currentLevel = 0.7,
            recentScores = mutableListOf(80, 75, 70, 65, 60),
            recentAccuracies = mutableListOf(0.9, 0.85, 0.8, 0.75, 0.7),
            recentRTs = mutableListOf(17000.0, 19000.0, 21000.0, 23000.0, 25000.0),
            preference = DifficultyAdapter.Preference.MAINTAIN
        )
        
        val (newLevel, changed) = DifficultyAdapter.updateDifficulty(state, 55, 0.6, 28000.0)
        
        assertTrue(changed)
        assertTrue(newLevel < state.currentLevel)
        assertTrue(newLevel >= 0.0)
    }

    @Test
    fun `updateDifficulty maintains level for stable performance`() {
        val state = DifficultyAdapter.DifficultyState(
            exerciseType = "SCHULTE",
            currentLevel = 0.5,
            recentScores = mutableListOf(70, 72, 71, 73, 71),
            recentAccuracies = mutableListOf(0.8, 0.81, 0.79, 0.82, 0.8),
            recentRTs = mutableListOf(20000.0, 20500.0, 19800.0, 20200.0, 20100.0),
            preference = DifficultyAdapter.Preference.MAINTAIN
        )
        
        val (newLevel, changed) = DifficultyAdapter.updateDifficulty(state, 71, 0.8, 20000.0)
        
        assertFalse(changed)
        assertEquals(state.currentLevel, newLevel, 0.001)
    }

    @Test
    fun `CHALLENGE preference increases faster`() {
        val baseState = DifficultyAdapter.DifficultyState(
            exerciseType = "SCHULTE",
            currentLevel = 0.3,
            recentScores = mutableListOf(70, 75, 80, 85, 90),
            recentAccuracies = mutableListOf(0.8, 0.85, 0.9, 0.92, 0.95),
            recentRTs = mutableListOf(25000.0, 22000.0, 20000.0, 18000.0, 16000.0),
            preference = DifficultyAdapter.Preference.CHALLENGE
        )
        
        val maintainState = baseState.copy(preference = DifficultyAdapter.Preference.MAINTAIN)
        val relaxState = baseState.copy(preference = DifficultyAdapter.Preference.RELAX)
        
        val (challengeLevel, _) = DifficultyAdapter.updateDifficulty(baseState, 90, 0.95, 15000.0)
        val (maintainLevel, _) = DifficultyAdapter.updateDifficulty(maintainState, 90, 0.95, 15000.0)
        val (relaxLevel, _) = DifficultyAdapter.updateDifficulty(relaxState, 90, 0.95, 15000.0)
        
        assertTrue(challengeLevel > maintainLevel)
        assertTrue(maintainLevel > relaxLevel)
    }

    @Test
    fun `RELAX preference decreases slower`() {
        val baseState = DifficultyAdapter.DifficultyState(
            exerciseType = "SCHULTE",
            currentLevel = 0.7,
            recentScores = mutableListOf(60, 55, 50, 45, 40),
            recentAccuracies = mutableListOf(0.7, 0.65, 0.6, 0.55, 0.5),
            recentRTs = mutableListOf(25000.0, 27000.0, 29000.0, 31000.0, 33000.0),
            preference = DifficultyAdapter.Preference.RELAX
        )
        
        val challengeState = baseState.copy(preference = DifficultyAdapter.Preference.CHALLENGE)
        val maintainState = baseState.copy(preference = DifficultyAdapter.Preference.MAINTAIN)
        
        val (relaxLevel, _) = DifficultyAdapter.updateDifficulty(baseState, 40, 0.45, 35000.0)
        val (maintainLevel, _) = DifficultyAdapter.updateDifficulty(maintainState, 40, 0.45, 35000.0)
        val (challengeLevel, _) = DifficultyAdapter.updateDifficulty(challengeState, 40, 0.45, 35000.0)
        
        assertTrue(relaxLevel > maintainLevel)
        assertTrue(maintainLevel > challengeLevel)
    }

    @Test
    fun `detects plateau after 14 sessions without improvement`() {
        val state = DifficultyAdapter.DifficultyState(
            exerciseType = "SCHULTE",
            currentLevel = 0.5,
            recentScores = (1..15).map { 72 }.toMutableList(), // Flat scores
            recentAccuracies = (1..15).map { 0.8 }.toMutableList(),
            recentRTs = (1..15).map { 20000.0 }.toMutableList(),
            preference = DifficultyAdapter.Preference.MAINTAIN
        )
        state.sessionsSinceChange = 14
        
        val (newLevel, changed) = DifficultyAdapter.updateDifficulty(state, 72, 0.8, 20000.0)
        
        assertTrue(state.plateauDetected)
        // Plateau adjustment should be small
        assertTrue(changed)
        assertTrue(newLevel > state.currentLevel)
    }

    @Test
    fun `getExerciseParams returns correct Schulte params`() {
        val params = DifficultyAdapter.getExerciseParams("SCHULTE", 0.0)
        assertEquals(4, params["size"])
        assertEquals(60000, params["timeLimitMs"])
        
        params = DifficultyAdapter.getExerciseParams("SCHULTE", 0.3)
        assertEquals(5, params["size"])
        
        params = DifficultyAdapter.getExerciseParams("SCHULTE", 0.8)
        assertEquals(7, params["size"])
    }

    @Test
    fun `getExerciseParams returns correct Blindfold params`() {
        val params = DifficultyAdapter.getExerciseParams("BLINDFOLD", 0.0)
        assertEquals(1, params["tier"])
        assertEquals(1.5, params["maxRmsError"])
        
        params = DifficultyAdapter.getExerciseParams("BLINDFOLD", 0.5)
        assertEquals(2, params["tier"])
        
        params = DifficultyAdapter.getExerciseParams("BLINDFOLD", 0.9)
        assertEquals(3, params["tier"])
    }

    @Test
    fun `getExerciseParams returns correct NonDominant params`() {
        val params = DifficultyAdapter.getExerciseParams("NON_DOMINANT", 0.0)
        assertEquals(listOf("COPYWRITING"), params["tasks"])
        
        params = DifficultyAdapter.getExerciseParams("NON_DOMINANT", 0.3)
        assertEquals(listOf("COPYWRITING", "TRACING"), params["tasks"])
        
        params = DifficultyAdapter.getExerciseParams("NON_DOMINANT", 0.9)
        assertEquals(listOf("COPYWRITING", "TRACING", "TAPPING"), params["tasks"])
    }

    @Test
    fun `getExerciseParams returns correct Stroop params`() {
        val params = DifficultyAdapter.getExerciseParams("STROOP", 0.0)
        assertEquals(4, params["colorCount"])
        assertEquals(0.2, params["incongruentRatio"])
        assertEquals("CLASSIC", params["mode"])
        
        params = DifficultyAdapter.getExerciseParams("STROOP", 0.5)
        assertEquals(5, params["colorCount"])
        assertEquals("SWITCH", params["mode"])
        
        params = DifficultyAdapter.getExerciseParams("STROOP", 0.9)
        assertEquals(6, params["colorCount"])
        assertEquals("SPEED", params["mode"])
    }

    @Test
    fun `createInitialState sets correct initial levels`() {
        val challengeState = DifficultyAdapter.createInitialState("SCHULTE", DifficultyAdapter.Preference.CHALLENGE)
        val maintainState = DifficultyAdapter.createInitialState("SCHULTE", DifficultyAdapter.Preference.MAINTAIN)
        val relaxState = DifficultyAdapter.createInitialState("SCHULTE", DifficultyAdapter.Preference.RELAX)
        
        assertEquals(0.3, challengeState.currentLevel, 0.001)
        assertEquals(0.1, maintainState.currentLevel, 0.001)
        assertEquals(0.0, relaxState.currentLevel, 0.001)
    }

    @Test
    fun `serialize and deserialize state`() {
        val original = DifficultyAdapter.DifficultyState(
            exerciseType = "SCHULTE",
            currentLevel = 0.42,
            recentScores = mutableListOf(70, 75, 80),
            recentAccuracies = mutableListOf(0.8, 0.85, 0.9),
            recentRTs = mutableListOf(20000.0, 19000.0, 18000.0),
            sessionsSinceChange = 3,
            plateauDetected = false,
            totalSessions = 10,
            preference = DifficultyAdapter.Preference.MAINTAIN
        )
        
        val json = DifficultyAdapter.serialize(original)
        val deserialized = DifficultyAdapter.deserialize(json)
        
        assertEquals(original.exerciseType, deserialized.exerciseType)
        assertEquals(original.currentLevel, deserialized.currentLevel, 0.001)
        assertEquals(original.preference, deserialized.preference)
    }
}