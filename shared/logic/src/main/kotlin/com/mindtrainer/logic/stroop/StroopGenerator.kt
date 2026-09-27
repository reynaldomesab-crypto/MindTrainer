package com.mindtrainer.logic.stroop

import kotlin.random.Random
import kotlin.math.abs

/**
 * Stroop Challenge Generator
 * Creates stimuli for multiple Stroop modes with balanced conditions.
 */
object StroopGenerator {

    /**
     * Generates a session of Stroop stimuli for a given mode and difficulty.
     */
    fun generateSession(
        mode: StroopMode,
        config: StroopConfig,
        language: String,
        wordSets: StroopWordSets,
        seed: Long = System.currentTimeMillis()
    ): List<StroopStimulus> {
        val random = Random(seed)
        val stimuli = mutableListOf<StroopStimulus>()
        
        val colorWords = wordSets.colorWords
        val neutralWords = wordSets.neutralWords
        val allWords = colorWords + neutralWords
        val colors = listOf("RED", "BLUE", "GREEN", "YELLOW", "PURPLE", "ORANGE")
            .take(config.colorCount)
        
        val trialCount = config.trialCount
        val incongruentCount = (trialCount * config.incongruentRatio).toInt()
        val congruentCount = trialCount - incongruentCount
        
        when (mode) {
            StroopMode.CLASSIC -> {
                // Classic: name ink color, ignore word
                val incongruentStimuli = generateIncongruentStimuli(
                    incongruentCount, colors, colorWords, random, config
                )
                val congruentStimuli = generateCongruentStimuli(
                    congruentCount, colors, colorWords, random, config
                )
                stimuli.addAll(incongruentStimuli + congruentStimuli)
            }
            StroopMode.REVERSE -> {
                // Reverse: name word meaning, ignore ink color
                val incongruentStimuli = generateIncongruentStimuli(
                    incongruentCount, colors, colorWords, random, config
                )
                val congruentStimuli = generateCongruentStimuli(
                    congruentCount, colors, colorWords, random, config
                )
                stimuli.addAll(incongruentStimuli + congruentStimuli)
            }
            StroopMode.SWITCH -> {
                // Switch: alternate between WORD and COLOR tasks
                val switchStimuli = generateSwitchStimuli(
                    trialCount, colors, colorWords, neutralWords, random, config
                )
                stimuli.addAll(switchStimuli)
            }
            StroopMode.SPATIAL -> {
                // Spatial: word appears left/right, respond to position
                val spatialStimuli = generateSpatialStimuli(
                    trialCount, colors, allWords, random, config
                )
                stimuli.addAll(spatialStimuli)
            }
            StroopMode.SPEED -> {
                // Speed: rapid fire, max throughput
                val speedStimuli = generateSpeedStimuli(
                    trialCount, colors, colorWords, random, config
                )
                stimuli.addAll(speedStimuli)
            }
            StroopMode.SEQUENCE -> {
                // Sequence: remember color sequence, respond to matching
                val sequenceStimuli = generateSequenceStimuli(
                    trialCount, colors, colorWords, config.sequenceLength, random, config
                )
                stimuli.addAll(sequenceStimuli)
            }
        }
        
        // Shuffle for mixed presentation (except SEQUENCE which has order)
        if (mode != StroopMode.SEQUENCE) {
            random.nextInt() // consume one for shuffle seed
            stimuli.shuffle()
        }
        
        return stimuli
    }

    private fun generateIncongruentStimuli(
        count: Int,
        colors: List<String>,
        colorWords: List<String>,
        random: Random,
        config: StroopConfig
    ): List<StroopStimulus> {
        return (0 until count).map {
            val word = colorWords.random(random)
            val inkColor = colors.filter { it != word }.random(random)
            val position = when (config.mode) {
                StroopMode.SPATIAL -> listOf("LEFT", "RIGHT").random(random)
                else -> "CENTER"
            }
            StroopStimulus(
                word = word,
                inkColor = inkColor,
                condition = StroopCondition.INCONGRUENT,
                position = position,
                stimulusDurationMs = config.stimulusDurationMs
            )
        }
    }

    private fun generateCongruentStimuli(
        count: Int,
        colors: List<String>,
        colorWords: List<String>,
        random: Random,
        config: StroopConfig
    ): List<StroopStimulus> {
        return (0 until count).map {
            val word = colorWords.random(random)
            val position = when (config.mode) {
                StroopMode.SPATIAL -> listOf("LEFT", "RIGHT").random(random)
                else -> "CENTER"
            }
            StroopStimulus(
                word = word,
                inkColor = word, // congruent: word == ink color
                condition = StroopCondition.CONGRUENT,
                position = position,
                stimulusDurationMs = config.stimulusDurationMs
            )
        }
    }

    private fun generateSwitchStimuli(
        count: Int,
        colors: List<String>,
        colorWords: List<String>,
        neutralWords: List<String>,
        random: Random,
        config: StroopConfig
    ): List<StroopStimulus> {
        val stimuli = mutableListOf<StroopStimulus>()
        var lastTask: TaskType? = null
        var switchCount = 0
        
        for (i in 0 until count) {
            val shouldSwitch = lastTask != null && random.nextDouble() < config.switchFrequency
            val task = if (shouldSwitch) {
                switchCount++
                if (lastTask == TaskType.COLOR) TaskType.WORD else TaskType.COLOR
            } else {
                lastTask ?: (if (random.nextBoolean()) TaskType.COLOR else TaskType.WORD)
            }
            lastTask = task
            
            val isCongruent = random.nextDouble() > config.incongruentRatio
            val word = if (isCongruent) colorWords.random(random) else (colorWords + neutralWords).random(random)
            val inkColor = if (isCongruent) word else colors.filter { it != word }.random(random)
            
            stimuli.add(StroopStimulus(
                word = word,
                inkColor = inkColor,
                condition = if (isCongruent) StroopCondition.CONGRUENT else StroopCondition.INCONGRUENT,
                taskType = task,
                isSwitchTrial = shouldSwitch,
                stimulusDurationMs = config.stimulusDurationMs
            ))
        }
        return stimuli
    }

    private fun generateSpatialStimuli(
        count: Int,
        colors: List<String>,
        allWords: List<String>,
        random: Random,
        config: StroopConfig
    ): List<StroopStimulus> {
        return (0 until count).map {
            val position = listOf("LEFT", "RIGHT").random(random)
            val word = allWords.random(random)
            val inkColor = colors.random(random)
            val isCongruent = colors.contains(word) && word == inkColor
            
            StroopStimulus(
                word = word,
                inkColor = inkColor,
                condition = if (isCongruent) StroopCondition.CONGRUENT else StroopCondition.INCONGRUENT,
                position = position,
                stimulusDurationMs = config.stimulusDurationMs
            )
        }
    }

    private fun generateSpeedStimuli(
        count: Int,
        colors: List<String>,
        colorWords: List<String>,
        random: Random,
        config: StroopConfig
    ): List<StroopStimulus> {
        // Speed mode: mostly incongruent, short duration
        return (0 until count).map {
            val isCongruent = random.nextDouble() < 0.2 // 80% incongruent
            val word = colorWords.random(random)
            val inkColor = if (isCongruent) word else colors.filter { it != word }.random(random)
            
            StroopStimulus(
                word = word,
                inkColor = inkColor,
                condition = if (isCongruent) StroopCondition.CONGRUENT else StroopCondition.INCONGRUENT,
                stimulusDurationMs = config.stimulusDurationMs
            )
        }
    }

    private fun generateSequenceStimuli(
        count: Int,
        colors: List<String>,
        colorWords: List<String>,
        sequenceLength: Int,
        random: Random,
        config: StroopConfig
    ): List<StroopStimulus> {
        // Generate a target sequence to remember
        val targetSequence = (0 until sequenceLength).map { colors.random(random) }
        
        return (0 until count).map { i ->
            val isTarget = i % 3 == 0 // Every 3rd stimulus is from target sequence
            val word = if (isTarget) targetSequence[i % sequenceLength] else colorWords.random(random)
            val inkColor = colors.random(random)
            val isCongruent = word == inkColor
            
            StroopStimulus(
                word = word,
                inkColor = inkColor,
                condition = if (isCongruent) StroopCondition.CONGRUENT else StroopCondition.INCONGRUENT,
                isTarget = isTarget,
                stimulusDurationMs = config.stimulusDurationMs
            )
        }
    }

    /**
     * Generates daily seed for consistent daily challenge.
     */
    fun getDailySeed(date: java.time.LocalDate = java.time.LocalDate.now()): Long {
        return 2_000_000 + date.toEpochDay()
    }
}

/**
 * Stroop task types for SWITCH mode.
 */
enum class TaskType {
    COLOR,  // Name the ink color
    WORD    // Read the word
}

/**
 * Stroop modes available.
 */
enum class StroopMode {
    CLASSIC,    // Name ink color, ignore word
    REVERSE,    // Name word meaning, ignore ink color
    SWITCH,     // Alternate between COLOR and WORD tasks
    SPATIAL,    // Respond to spatial position (LEFT/RIGHT)
    SPEED,      // Maximum throughput in 30 seconds
    SEQUENCE    // Working memory + inhibition
}

/**
 * Stroop stimulus conditions.
 */
enum class StroopCondition {
    CONGRUENT,    // Word == Ink color
    INCONGRUENT,  // Word != Ink color
    NEUTRAL       // Non-color word (e.g., "DOG")
}

/**
 * Configuration for Stroop session generation.
 */
data class StroopConfig(
    val mode: StroopMode = StroopMode.CLASSIC,
    val colorCount: Int = 4,           // 4, 5, or 6 colors
    val incongruentRatio: Double = 0.5, // 0.0 to 1.0
    val stimulusDurationMs: Int = 1000, // ms per stimulus
    val switchFrequency: Double = 0.3,  // for SWITCH mode
    val sequenceLength: Int = 3,        // for SEQUENCE mode
    val trialCount: Int = 60,           // total trials
    val difficultyLevel: Double = 0.5   // 0.0 to 1.0
)

/**
 * Individual Stroop stimulus.
 */
data class StroopStimulus(
    val word: String,
    val inkColor: String,
    val condition: StroopCondition = StroopCondition.CONGRUENT,
    val position: String = "CENTER",    // LEFT, RIGHT, CENTER
    val taskType: TaskType? = null,     // for SWITCH mode
    val isSwitchTrial: Boolean = false, // for SWITCH mode
    val isTarget: Boolean = false,      // for SEQUENCE mode
    val stimulusDurationMs: Int = 1000
)

/**
 * Word sets for different languages.
 */
data class StroopWordSets(
    val language: String,
    val colorWords: List<String>,
    val neutralWords: List<String>,
    val emotionalWords: List<String>
) {
    val allWords: List<String>
        get() = colorWords + neutralWords
}