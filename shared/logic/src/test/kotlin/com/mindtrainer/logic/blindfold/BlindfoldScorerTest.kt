package com.mindtrainer.logic.blindfold

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BlindfoldScorerTest {

    @Test
    fun `score returns perfect accuracy for exact match`() {
        val expected = "The quick brown fox jumps over the lazy dog."
        val actual = expected
        
        val result = BlindfoldScorer.score(expected, actual, "QWERTY")
        
        assertEquals(1.0, result.accuracy)
        assertEquals(0.0, result.rmsError)
        assertEquals(0, result.substitutions)
        assertEquals(0, result.insertions)
        assertEquals(0, result.deletions)
    }

    @Test
    fun `score detects single substitution`() {
        val expected = "The quick brown fox jumps over the lazy dog."
        val actual = "The quick brown fox jumps over the lazy cat."
        
        val result = BlindfoldScorer.score(expected, actual, "QWERTY")
        
        assertTrue(result.accuracy < 1.0)
        assertEquals(1, result.substitutions)
        assertEquals(0, result.insertions)
        assertEquals(0, result.deletions)
        assertTrue(result.rmsError > 0.0)
    }

    @Test
    fun `score detects insertion`() {
        val expected = "The quick brown fox."
        val actual = "The quick brown fox jumps."
        
        val result = BlindfoldScorer.score(expected, actual, "QWERTY")
        
        assertEquals(1, result.insertions)
        assertEquals(0, result.deletions)
    }

    @Test
    fun `score detects deletion`() {
        val expected = "The quick brown fox jumps."
        val actual = "The quick fox jumps."
        
        val result = BlindfoldScorer.score(expected, actual, "QWERTY")
        
        assertEquals(1, result.deletions)
        assertEquals(0, result.insertions)
    }

    @Test
    fun `score calculates key distances for QWERTY`() {
        val expected = "asdf"
        val actual = "asdg" // 'g' is next to 'f' on QWERTY
        
        val result = BlindfoldScorer.score(expected, actual, "QWERTY")
        
        assertEquals(1, result.substitutions)
        assertTrue(result.rmsError > 0.0)
        assertTrue(result.rmsError < 1.0) // Adjacent key distance < 1 key-width
    }

    @Test
    fun `score handles AZERTY layout`() {
        val expected = "azerty"
        val actual = "aqerty" // 'q' next to 'z' on AZERTY
        
        val result = BlindfoldScorer.score(expected, actual, "AZERTY")
        
        assertEquals(1, result.substitutions)
        assertTrue(result.rmsError > 0.0)
    }

    @Test
    fun `calculateScore returns 100 for perfect match`() {
        val result = BlindfoldScorer.score("hello", "hello", "QWERTY")
        val score = result.calculateScore(5000)
        
        assertEquals(100, score)
    }

    @Test
    fun `calculateScore penalizes errors`() {
        val result = BlindfoldScorer.score("hello world", "hella world", "QWERTY")
        val score = result.calculateScore(5000)
        
        assertTrue(score < 100)
    }

    @Test
    fun `lengthFactor decreases for longer texts`() {
        val short = BlindfoldScorer.score("short", "short", "QWERTY")
        val long = BlindfoldScorer.score("This is a much longer text for testing length factor", "This is a much longer text for testing length factor", "QWERTY")
        
        assertTrue(long.lengthFactor < short.lengthFactor)
    }

    @Test
    fun `score handles case insensitivity`() {
        val result1 = BlindfoldScorer.score("HELLO", "hello", "QWERTY")
        val result2 = BlindfoldScorer.score("hello", "HELLO", "QWERTY")
        
        assertEquals(result1.accuracy, result2.accuracy)
        assertEquals(result1.rmsError, result2.rmsError)
    }

    @Test
    fun `score handles empty strings`() {
        val result = BlindfoldScorer.score("", "", "QWERTY")
        
        assertEquals(1.0, result.accuracy)
        assertEquals(0.0, result.rmsError)
        assertEquals(0, result.totalChars)
    }
}