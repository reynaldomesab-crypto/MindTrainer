package com.mindtrainer.logic.stroop

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StroopGeneratorTest {

    @Test
    fun `generateSession creates correct number of stimuli for CLASSIC`() {
        val config = StroopGenerator.StroopConfig(
            mode = StroopGenerator.StroopMode.CLASSIC,
            colorCount = 4,
            incongruentRatio = 0.5,
            stimulusDurationMs = 1000,
            switchFrequency = 0.0,
            sequenceLength = 3,
            trialCount = 20,
            difficultyLevel = 0.5
        )
        val wordSets = StroopGenerator.StroopWordSets(
            language = "en",
            colorWords = listOf("RED", "BLUE", "GREEN", "YELLOW"),
            neutralWords = listOf("DOG", "CAT"),
            emotionalWords = listOf(),
            allWords = listOf("RED", "BLUE", "GREEN", "YELLOW", "DOG", "CAT")
        )
        
        val stimuli = StroopGenerator.generateSession(
            StroopGenerator.StroopMode.CLASSIC, config, "en", wordSets, 12345L
        )
        
        assertEquals(20, stimuli.size)
    }

    @Test
    fun `generateSession creates correct conditions for CLASSIC`() {
        val config = StroopGenerator.StroopConfig(
            mode = StroopGenerator.StroopMode.CLASSIC,
            colorCount = 4,
            incongruentRatio = 0.5,
            stimulusDurationMs = 1000,
            switchFrequency = 0.0,
            sequenceLength = 3,
            trialCount = 20,
            difficultyLevel = 0.5
        )
        val wordSets = StroopGenerator.StroopWordSets(
            language = "en",
            colorWords = listOf("RED", "BLUE", "GREEN", "YELLOW"),
            neutralWords = listOf("DOG", "CAT"),
            emotionalWords = listOf(),
            allWords = listOf("RED", "BLUE", "GREEN", "YELLOW", "DOG", "CAT")
        )
        
        val stimuli = StroopGenerator.generateSession(
            StroopGenerator.StroopMode.CLASSIC, config, "en", wordSets, 12345L
        )
        
        val congruentCount = stimuli.count { it.condition == StroopGenerator.StroopCondition.CONGRUENT }
        val incongruentCount = stimuli.count { it.condition == StroopGenerator.StroopCondition.INCONGRUENT }
        
        assertEquals(10, congruentCount)
        assertEquals(10, incongruentCount)
    }

    @Test
    fun `generateSession creates SWITCH stimuli with task types`() {
        val config = StroopGenerator.StroopConfig(
            mode = StroopGenerator.StroopMode.SWITCH,
            colorCount = 4,
            incongruentRatio = 0.5,
            stimulusDurationMs = 1000,
            switchFrequency = 0.3,
            sequenceLength = 3,
            trialCount = 20,
            difficultyLevel = 0.5
        )
        val wordSets = StroopGenerator.StroopWordSets(
            language = "en",
            colorWords = listOf("RED", "BLUE", "GREEN", "YELLOW"),
            neutralWords = listOf("DOG", "CAT"),
            emotionalWords = listOf(),
            allWords = listOf("RED", "BLUE", "GREEN", "YELLOW", "DOG", "CAT")
        )
        
        val stimuli = StroopGenerator.generateSession(
            StroopGenerator.StroopMode.SWITCH, config, "en", wordSets, 12345L
        )
        
        val switchTrials = stimuli.count { it.isSwitchTrial }
        val colorTasks = stimuli.count { it.taskType == StroopGenerator.TaskType.COLOR }
        val wordTasks = stimuli.count { it.taskType == StroopGenerator.TaskType.WORD }
        
        assertTrue(switchTrials > 0)
        assertTrue(colorTasks > 0)
        assertTrue(wordTasks > 0)
    }

    @Test
    fun `generateSession creates SPATIAL stimuli with positions`() {
        val config = StroopGenerator.StroopConfig(
            mode = StroopGenerator.StroopMode.SPATIAL,
            colorCount = 4,
            incongruentRatio = 0.5,
            stimulusDurationMs = 1000,
            switchFrequency = 0.0,
            sequenceLength = 3,
            trialCount = 20,
            difficultyLevel = 0.5
        )
        val wordSets = StroopGenerator.StroopWordSets(
            language = "en",
            colorWords = listOf("RED", "BLUE", "GREEN", "YELLOW"),
            neutralWords = listOf("DOG", "CAT"),
            emotionalWords = listOf(),
            allWords = listOf("RED", "BLUE", "GREEN", "YELLOW", "DOG", "CAT")
        )
        
        val stimuli = StroopGenerator.generateSession(
            StroopGenerator.StroopMode.SPATIAL, config, "en", wordSets, 12345L
        )
        
        val leftCount = stimuli.count { it.position == "LEFT" }
        val rightCount = stimuli.count { it.position == "RIGHT" }
        
        assertTrue(leftCount > 0)
        assertTrue(rightCount > 0)
    }

    @Test
    fun `generateSession creates SPEED stimuli with high incongruent ratio`() {
        val config = StroopGenerator.StroopConfig(
            mode = StroopGenerator.StroopMode.SPEED,
            colorCount = 6,
            incongruentRatio = 0.8,
            stimulusDurationMs = 400,
            switchFrequency = 0.0,
            sequenceLength = 3,
            trialCount = 30,
            difficultyLevel = 0.8
        )
        val wordSets = StroopGenerator.StroopWordSets(
            language = "en",
            colorWords = listOf("RED", "BLUE", "GREEN", "YELLOW", "PURPLE", "ORANGE"),
            neutralWords = listOf("DOG", "CAT"),
            emotionalWords = listOf(),
            allWords = listOf("RED", "BLUE", "GREEN", "YELLOW", "PURPLE", "ORANGE", "DOG", "CAT")
        )
        
        val stimuli = StroopGenerator.generateSession(
            StroopGenerator.StroopMode.SPEED, config, "en", wordSets, 12345L
        )
        
        val incongruentCount = stimuli.count { it.condition == StroopGenerator.StroopCondition.INCONGRUENT }
        assertTrue(incongruentCount.toDouble() / stimuli.size >= 0.7)
    }

    @Test
    fun `generateSession creates SEQUENCE stimuli with targets`() {
        val config = StroopGenerator.StroopConfig(
            mode = StroopGenerator.StroopMode.SEQUENCE,
            colorCount = 4,
            incongruentRatio = 0.5,
            stimulusDurationMs = 1000,
            switchFrequency = 0.0,
            sequenceLength = 3,
            trialCount = 15,
            difficultyLevel = 0.5
        )
        val wordSets = StroopGenerator.StroopWordSets(
            language = "en",
            colorWords = listOf("RED", "BLUE", "GREEN", "YELLOW"),
            neutralWords = listOf("DOG", "CAT"),
            emotionalWords = listOf(),
            allWords = listOf("RED", "BLUE", "GREEN", "YELLOW", "DOG", "CAT")
        )
        
        val stimuli = StroopGenerator.generateSession(
            StroopGenerator.StroopMode.SEQUENCE, config, "en", wordSets, 12345L
        )
        
        val targetCount = stimuli.count { it.isTarget }
        assertTrue(targetCount > 0)
    }

    @Test
    fun `getDailySeed is deterministic`() {
        val date = java.time.LocalDate.of(2024, 1, 15)
        val seed1 = StroopGenerator.getDailySeed(date)
        val seed2 = StroopGenerator.getDailySeed(date)
        
        assertEquals(seed1, seed2)
    }
}

class StroopScorerTest {

    @Test
    fun `scoreSession calculates interference effect for CLASSIC`() {
        val mode = StroopGenerator.StroopMode.CLASSIC
        val config = StroopGenerator.StroopConfig(
            mode = mode, colorCount = 4, incongruentRatio = 0.5,
            stimulusDurationMs = 1000, switchFrequency = 0.0, sequenceLength = 3,
            trialCount = 20, difficultyLevel = 0.5
        )
        
        val trials = listOf(
            createTrial("RED", "RED", 600, true, StroopGenerator.StroopCondition.CONGRUENT),
            createTrial("RED", "BLUE", 850, true, StroopGenerator.StroopCondition.INCONGRUENT),
            createTrial("BLUE", "BLUE", 620, true, StroopGenerator.StroopCondition.CONGRUENT),
            createTrial("BLUE", "RED", 900, true, StroopGenerator.StroopCondition.INCONGRUENT),
            createTrial("GREEN", "GREEN", 580, true, StroopGenerator.StroopCondition.CONGRUENT),
            createTrial("GREEN", "YELLOW", 880, true, StroopGenerator.StroopCondition.INCONGRUENT),
            createTrial("YELLOW", "YELLOW", 610, true, StroopGenerator.StroopCondition.CONGRUENT),
            createTrial("YELLOW", "GREEN", 920, true, StroopGenerator.StroopCondition.INCONGRUENT),
        )
        
        val result = StroopScorer.scoreSession(mode, trials, config)
        
        assertTrue(result.meanRTIncongruent > result.meanRTCongruent)
        assertTrue(result.interferenceEffect > 0.0)
        assertTrue(result.interferenceRatio > 1.0)
        assertEquals(1.0, result.accuracyCongruent, 0.01)
        assertEquals(1.0, result.accuracyIncongruent, 0.01)
        assertTrue(result.inhibitionIndex >= 0.0 && result.inhibitionIndex <= 1.0)
    }

    @Test
    fun `scoreSession calculates switch cost for SWITCH`() {
        val mode = StroopGenerator.StroopMode.SWITCH
        val config = StroopGenerator.StroopConfig(
            mode = mode, colorCount = 4, incongruentRatio = 0.5,
            stimulusDurationMs = 1000, switchFrequency = 0.3, sequenceLength = 3,
            trialCount = 20, difficultyLevel = 0.5
        )
        
        val trials = listOf(
            createTrial("RED", "RED", 600, true, StroopGenerator.StroopCondition.CONGRUENT, taskType = StroopGenerator.TaskType.COLOR, isSwitchTrial = false),
            createTrial("BLUE", "BLUE", 750, true, StroopGenerator.StroopCondition.CONGRUENT, taskType = StroopGenerator.TaskType.WORD, isSwitchTrial = true),
            createTrial("GREEN", "GREEN", 620, true, StroopGenerator.StroopCondition.CONGRUENT, taskType = StroopGenerator.TaskType.WORD, isSwitchTrial = false),
            createTrial("YELLOW", "YELLOW", 800, true, StroopGenerator.StroopCondition.CONGRUENT, taskType = StroopGenerator.TaskType.COLOR, isSwitchTrial = true),
        )
        
        val result = StroopScorer.scoreSession(mode, trials, config)
        
        assertTrue(result.switchCost > 0.0)
        assertTrue(result.flexibilityIndex >= 0.0 && result.flexibilityIndex <= 1.0)
    }

    @Test
    fun `scoreSession calculates throughput for SPEED`() {
        val mode = StroopGenerator.StroopMode.SPEED
        val config = StroopGenerator.StroopConfig(
            mode = mode, colorCount = 6, incongruentRatio = 0.8,
            stimulusDurationMs = 400, switchFrequency = 0.0, sequenceLength = 3,
            trialCount = 30, difficultyLevel = 0.8
        )
        
        val trials = (1..20).map { i ->
            val correct = i % 5 != 0 // 80% accuracy
            val rt = if (correct) 350 + (i * 10) else 500
            createTrial("RED", "BLUE", rt.toLong(), correct, StroopGenerator.StroopCondition.INCONGRUENT)
        }.toList()
        
        val result = StroopScorer.scoreSession(mode, trials, config)
        
        assertTrue(result.throughput > 0.0)
        assertEquals(0.8, result.accuracyIncongruent, 0.05)
    }

    @Test
    fun `inhibitionIndex is higher for lower interference`() {
        val lowInterferenceTrials = listOf(
            createTrial("RED", "RED", 600, true, StroopGenerator.StroopCondition.CONGRUENT),
            createTrial("RED", "BLUE", 650, true, StroopGenerator.StroopCondition.INCONGRUENT),
        )
        val highInterferenceTrials = listOf(
            createTrial("RED", "RED", 600, true, StroopGenerator.StroopCondition.CONGRUENT),
            createTrial("RED", "BLUE", 1000, true, StroopGenerator.StroopCondition.INCONGRUENT),
        )
        
        val config = StroopGenerator.StroopConfig(
            mode = StroopGenerator.StroopMode.CLASSIC, colorCount = 4, incongruentRatio = 0.5,
            stimulusDurationMs = 1000, switchFrequency = 0.0, sequenceLength = 3,
            trialCount = 2, difficultyLevel = 0.5
        )
        
        val lowResult = StroopScorer.scoreSession(StroopGenerator.StroopMode.CLASSIC, lowInterferenceTrials, config)
        val highResult = StroopScorer.scoreSession(StroopGenerator.StroopMode.CLASSIC, highInterferenceTrials, config)
        
        assertTrue(lowResult.inhibitionIndex > highResult.inhibitionIndex)
    }

    @Test
    fun `calculateNextLevel increases for good performance`() {
        val config = StroopGenerator.StroopConfig(
            mode = StroopGenerator.StroopMode.CLASSIC, colorCount = 4, incongruentRatio = 0.5,
            stimulusDurationMs = 1000, switchFrequency = 0.0, sequenceLength = 3,
            trialCount = 20, difficultyLevel = 0.5
        )
        
        val goodTrials = (1..10).map { i ->
            createTrial("RED", "BLUE", 700 + i * 10, true, StroopGenerator.StroopCondition.INCONGRUENT)
        } + (1..10).map { i ->
            createTrial("RED", "RED", 600 + i * 10, true, StroopGenerator.StroopCondition.CONGRUENT)
        }
        
        val result = StroopScorer.scoreSession(StroopGenerator.StroopMode.CLASSIC, goodTrials.toList(), config)
        
        assertTrue(result.nextLevel > config.difficultyLevel)
    }

    private fun createTrial(
        word: String,
        inkColor: String,
        rtMs: Long,
        correct: Boolean,
        condition: StroopGenerator.StroopCondition,
        taskType: StroopGenerator.TaskType = StroopGenerator.TaskType.COLOR,
        isSwitchTrial: Boolean = false
    ): StroopScorer.StroopTrial {
        return StroopScorer.StroopTrial(
            stimulus = StroopGenerator.StroopStimulus(
                word = word,
                inkColor = inkColor,
                condition = condition,
                position = "CENTER",
                taskType = taskType,
                isSwitchTrial = isSwitchTrial,
                isTarget = false,
                stimulusDurationMs = 1000
            ),
            response = if (correct) inkColor else (if (inkColor == "RED") "BLUE" else "RED"),
            rtMs = rtMs,
            correct = correct,
            isSwitchTrial = isSwitchTrial,
            condition = condition
        )
    }
}