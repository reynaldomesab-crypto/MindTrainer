import { 
  StroopGenerator, 
  StroopStimulus, 
  StroopMode, 
  StroopCondition, 
  StroopConfig,
  StroopWordSets 
} from '../stroop/StroopGenerator';
import { StroopScorer, StroopTrial } from '../stroop/StroopScorer';

describe('StroopGenerator', () => {
  const wordSets: StroopWordSets = {
    language: 'en',
    colorWords: ['RED', 'BLUE', 'GREEN', 'YELLOW', 'PURPLE', 'ORANGE'],
    neutralWords: ['DOG', 'CAT'],
    emotionalWords: ['ANXIETY', 'FEAR'],
    allWords: ['RED', 'BLUE', 'GREEN', 'YELLOW', 'PURPLE', 'ORANGE', 'DOG', 'CAT', 'ANXIETY', 'FEAR']
  };

  const baseConfig: StroopConfig = {
    mode: 'CLASSIC',
    colorCount: 4,
    incongruentRatio: 0.5,
    stimulusDurationMs: 1000,
    switchFrequency: 0.3,
    sequenceLength: 3,
    trialCount: 20,
    difficultyLevel: 0.5
  };

  describe('generateSession', () => {
    it('creates correct number of stimuli for CLASSIC', () => {
      const config = { ...baseConfig, mode: 'CLASSIC' as StroopMode };
      const stimuli = StroopGenerator.generateSession('CLASSIC', config, wordSets, 12345);
      
      expect(stimuli.length).toBe(20);
    });

    it('creates correct conditions for CLASSIC', () => {
      const config = { ...baseConfig, mode: 'CLASSIC' as StroopMode, incongruentRatio: 0.5 };
      const stimuli = StroopGenerator.generateSession('CLASSIC', config, wordSets, 12345);
      
      const congruentCount = stimuli.filter(s => s.condition === 'CONGRUENT').length;
      const incongruentCount = stimuli.filter(s => s.condition === 'INCONGRUENT').length;
      
      expect(congruentCount).toBe(10);
      expect(incongruentCount).toBe(10);
    });

    it('creates SWITCH stimuli with task types', () => {
      const config = { ...baseConfig, mode: 'SWITCH' as StroopMode };
      const stimuli = StroopGenerator.generateSession('SWITCH', config, wordSets, 12345);
      
      const switchTrials = stimuli.filter(s => s.isSwitchTrial).length;
      const colorTasks = stimuli.filter(s => s.taskType === 'COLOR').length;
      const wordTasks = stimuli.filter(s => s.taskType === 'WORD').length;
      
      expect(switchTrials).toBeGreaterThan(0);
      expect(colorTasks).toBeGreaterThan(0);
      expect(wordTasks).toBeGreaterThan(0);
    });

    it('creates SPATIAL stimuli with positions', () => {
      const config = { ...baseConfig, mode: 'SPATIAL' as StroopMode };
      const stimuli = StroopGenerator.generateSession('SPATIAL', config, wordSets, 12345);
      
      const leftCount = stimuli.filter(s => s.position === 'LEFT').length;
      const rightCount = stimuli.filter(s => s.position === 'RIGHT').length;
      
      expect(leftCount).toBeGreaterThan(0);
      expect(rightCount).toBeGreaterThan(0);
    });

    it('creates SPEED stimuli with high incongruent ratio', () => {
      const config = { ...baseConfig, mode: 'SPEED' as StroopMode, incongruentRatio: 0.8 };
      const stimuli = StroopGenerator.generateSession('SPEED', config, wordSets, 12345);
      
      const incongruentCount = stimuli.filter(s => s.condition === 'INCONGRUENT').length;
      expect(incongruentCount / stimuli.length).toBeGreaterThanOrEqual(0.7);
    });

    it('creates SEQUENCE stimuli with targets', () => {
      const config = { ...baseConfig, mode: 'SEQUENCE' as StroopMode };
      const stimuli = StroopGenerator.generateSession('SEQUENCE', config, wordSets, 12345);
      
      const targetCount = stimuli.filter(s => s.isTarget).length;
      expect(targetCount).toBeGreaterThan(0);
    });

    it('getDailySeed is deterministic', () => {
      const date = new Date('2024-01-15');
      const seed1 = StroopGenerator.getDailySeed(date);
      const seed2 = StroopGenerator.getDailySeed(date);
      
      expect(seed1).toBe(seed2);
    });
  });

  describe('StroopScorer', () => {
    const createTrial = (
      word: string,
      inkColor: string,
      rtMs: number,
      correct: boolean,
      condition: StroopCondition,
      taskType: 'COLOR' | 'WORD' = 'COLOR',
      isSwitchTrial = false
    ): StroopTrial => ({
      stimulus: {
        word,
        inkColor,
        condition,
        position: 'CENTER',
        taskType,
        isSwitchTrial,
        isTarget: false,
        stimulusDurationMs: 1000
      },
      response: correct ? inkColor : (inkColor === 'RED' ? 'BLUE' : 'RED'),
      rtMs,
      correct,
      isSwitchTrial,
      condition
    });

    it('calculates interference effect for CLASSIC', () => {
      const trials: StroopTrial[] = [
        createTrial('RED', 'RED', 600, true, 'CONGRUENT'),
        createTrial('RED', 'BLUE', 850, true, 'INCONGRUENT'),
        createTrial('BLUE', 'BLUE', 620, true, 'CONGRUENT'),
        createTrial('BLUE', 'RED', 900, true, 'INCONGRUENT'),
        createTrial('GREEN', 'GREEN', 580, true, 'CONGRUENT'),
        createTrial('GREEN', 'YELLOW', 880, true, 'INCONGRUENT'),
        createTrial('YELLOW', 'YELLOW', 610, true, 'CONGRUENT'),
        createTrial('YELLOW', 'GREEN', 920, true, 'INCONGRUENT'),
      ];

      const config = {
        mode: 'CLASSIC' as StroopMode,
        colorCount: 4,
        incongruentRatio: 0.5,
        stimulusDurationMs: 1000,
        switchFrequency: 0,
        sequenceLength: 3,
        trialCount: 20,
        difficultyLevel: 0.5
      };

      const result = StroopScorer.scoreSession('CLASSIC', trials, config);

      expect(result.meanRTIncongruent).toBeGreaterThan(result.meanRTCongruent);
      expect(result.interferenceEffect).toBeGreaterThan(0.0);
      expect(result.interferenceRatio).toBeGreaterThan(1.0);
      expect(result.accuracyCongruent).toBe(1.0);
      expect(result.accuracyIncongruent).toBe(1.0);
      expect(result.inhibitionIndex).toBeGreaterThanOrEqual(0.0);
      expect(result.inhibitionIndex).toBeLessThanOrEqual(1.0);
    });

    it('calculates switch cost for SWITCH', () => {
      const trials = [
        createTrial('RED', 'RED', 600, true, 'CONGRUENT', 'COLOR', false),
        createTrial('BLUE', 'BLUE', 750, true, 'CONGRUENT', 'WORD', true),
        createTrial('GREEN', 'GREEN', 620, true, 'CONGRUENT', 'WORD', false),
        createTrial('YELLOW', 'YELLOW', 800, true, 'CONGRUENT', 'COLOR', true),
      ];

      const config = {
        mode: 'SWITCH' as StroopMode,
        colorCount: 4,
        incongruentRatio: 0.5,
        stimulusDurationMs: 1000,
        switchFrequency: 0.3,
        sequenceLength: 3,
        trialCount: 20,
        difficultyLevel: 0.5
      };

      const result = StroopScorer.scoreSession('SWITCH', trials, config);

      expect(result.switchCost).toBeGreaterThan(0.0);
      expect(result.flexibilityIndex).toBeGreaterThanOrEqual(0.0);
      expect(result.flexibilityIndex).toBeLessThanOrEqual(1.0);
    });

    it('calculates throughput for SPEED', () => {
      const trials = Array.from({ length: 20 }, (_, i) => {
        const correct = i % 5 !== 0;
        const rt = correct ? 350 + (i * 10) : 500;
        return { 
          stimulus: { word: 'RED', inkColor: 'BLUE', condition: 'INCONGRUENT', position: 'CENTER', taskType: 'COLOR' as const, isSwitchTrial: false, isTarget: false, stimulusDurationMs: 1000 },
          response: correct ? 'BLUE' : 'RED',
          rtMs: rt,
          correct,
          isSwitchTrial: false,
          condition: 'INCONGRUENT' as const
        };
      });

      const config = {
        mode: 'SPEED' as StroopMode,
        colorCount: 6,
        incongruentRatio: 0.8,
        stimulusDurationMs: 400,
        switchFrequency: 0,
        sequenceLength: 3,
        trialCount: 30,
        difficultyLevel: 0.8
      };

      const result = StroopScorer.scoreSession('SPEED', trials, config);

      expect(result.throughput).toBeGreaterThan(0.0);
      expect(result.accuracyIncongruent).toBeCloseTo(0.8, 1);
    });

    it('inhibitionIndex is higher for lower interference', () => {
      const lowInterferenceTrials = [
        createTrial('RED', 'RED', 600, true, 'CONGRUENT'),
        createTrial('RED', 'BLUE', 650, true, 'INCONGRUENT'),
      ];
      const highInterferenceTrials = [
        createTrial('RED', 'RED', 600, true, 'CONGRUENT'),
        createTrial('RED', 'BLUE', 1000, true, 'INCONGRUENT'),
      ];

      const config = {
        mode: 'CLASSIC' as StroopMode,
        colorCount: 4,
        incongruentRatio: 0.5,
        stimulusDurationMs: 1000,
        switchFrequency: 0,
        sequenceLength: 3,
        trialCount: 2,
        difficultyLevel: 0.5
      };

      const lowResult = StroopScorer.scoreSession('CLASSIC', lowInterferenceTrials, config);
      const highResult = StroopScorer.scoreSession('CLASSIC', highInterferenceTrials, config);

      expect(lowResult.inhibitionIndex).toBeGreaterThan(highResult.inhibitionIndex);
    });

    it('calculates next level correctly', () => {
      const config = {
        mode: 'CLASSIC' as StroopMode,
        colorCount: 4,
        incongruentRatio: 0.5,
        stimulusDurationMs: 1000,
        switchFrequency: 0,
        sequenceLength: 3,
        trialCount: 20,
        difficultyLevel: 0.5
      };

      const goodTrials = Array.from({ length: 10 }, (_, i) => 
        createTrial('RED', 'BLUE', 700 + i * 10, true, 'INCONGRUENT')
      ).concat(Array.from({ length: 10 }, (_, i) => 
        createTrial('RED', 'RED', 600 + i * 10, true, 'CONGRUENT')
      ));

      const result = StroopScorer.scoreSession('CLASSIC', goodTrials, config);

      expect(result.nextLevel).toBeGreaterThan(config.difficultyLevel);
    });
  });
});