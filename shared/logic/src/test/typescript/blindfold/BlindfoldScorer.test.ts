import { BlindfoldScorer, BlindfoldResult } from '../blindfold/BlindfoldScorer';

describe('BlindfoldScorer', () => {
  describe('score', () => {
    it('returns perfect accuracy for exact match', () => {
      const expected = 'The quick brown fox jumps over the lazy dog.';
      const actual = expected;
      
      const result: BlindfoldResult = BlindfoldScorer.score(expected, actual, 'QWERTY');
      
      expect(result.accuracy).toBe(1.0);
      expect(result.rmsError).toBe(0.0);
      expect(result.substitutions).toBe(0);
      expect(result.insertions).toBe(0);
      expect(result.deletions).toBe(0);
    });

    it('detects single substitution', () => {
      const expected = 'The quick brown fox jumps over the lazy dog.';
      const actual = 'The quick brown fox jumps over the lazy cat.';
      
      const result = BlindfoldScorer.score(expected, actual, 'QWERTY');
      
      expect(result.accuracy).toBeLessThan(1.0);
      expect(result.substitutions).toBe(1);
      expect(result.insertions).toBe(0);
      expect(result.deletions).toBe(0);
      expect(result.rmsError).toBeGreaterThan(0.0);
    });

    it('detects insertion', () => {
      const expected = 'The quick brown fox.';
      const actual = 'The quick brown fox jumps.';
      
      const result = BlindfoldScorer.score(expected, actual, 'QWERTY');
      
      expect(result.insertions).toBe(1);
      expect(result.deletions).toBe(0);
    });

    it('detects deletion', () => {
      const expected = 'The quick brown fox jumps.';
      const actual = 'The quick fox jumps.';
      
      const result = BlindfoldScorer.score(expected, actual, 'QWERTY');
      
      expect(result.deletions).toBe(1);
      expect(result.insertions).toBe(0);
    });

    it('calculates key distances for QWERTY', () => {
      const expected = 'asdf';
      const actual = 'asdg'; // 'g' is next to 'f' on QWERTY
      
      const result = BlindfoldScorer.score(expected, actual, 'QWERTY');
      
      expect(result.substitutions).toBe(1);
      expect(result.rmsError).toBeGreaterThan(0.0);
      expect(result.rmsError).toBeLessThan(1.0); // Adjacent key distance < 1 key-width
    });

    it('handles AZERTY layout', () => {
      const expected = 'azerty';
      const actual = 'aqerty'; // 'q' next to 'z' on AZERTY
      
      const result = BlindfoldScorer.score(expected, actual, 'AZERTY');
      
      expect(result.substitutions).toBe(1);
      expect(result.rmsError).toBeGreaterThan(0.0);
    });

    it('handles case insensitivity', () => {
      const result1 = BlindfoldScorer.score('HELLO', 'hello', 'QWERTY');
      const result2 = BlindfoldScorer.score('hello', 'HELLO', 'QWERTY');
      
      expect(result1.accuracy).toBe(result2.accuracy);
      expect(result1.rmsError).toBe(result2.rmsError);
    });

    it('handles empty strings', () => {
      const result = BlindfoldScorer.score('', '', 'QWERTY');
      
      expect(result.accuracy).toBe(1.0);
      expect(result.rmsError).toBe(0.0);
      expect(result.totalChars).toBe(0);
    });
  });

  describe('calculateScore', () => {
    it('returns 100 for perfect match', () => {
      const result = BlindfoldScorer.score('hello', 'hello', 'QWERTY');
      const score = result.calculateScore(5000);
      
      expect(score).toBe(100);
    });

    it('penalizes errors', () => {
      const result = BlindfoldScorer.score('hello world', 'hella world', 'QWERTY');
      const score = result.calculateScore(5000);
      
      expect(score).toBeLessThan(100);
    });

    it('lengthFactor decreases for longer texts', () => {
      const short = BlindfoldScorer.score('short', 'short', 'QWERTY');
      const long = BlindfoldScorer.score(
        'This is a much longer text for testing length factor', 
        'This is a much longer text for testing length factor', 
        'QWERTY'
      );
      
      expect(long.lengthFactor).toBeLessThan(short.lengthFactor);
    });

    it('time factor rewards faster typing', () => {
      const result = BlindfoldScorer.score('hello', 'hello', 'QWERTY');
      const fastScore = result.calculateScore(2000); // 60 WPM
      const slowScore = result.calculateScore(10000); // 12 WPM
      
      expect(fastScore).toBeGreaterThan(slowScore);
    });
  });
});