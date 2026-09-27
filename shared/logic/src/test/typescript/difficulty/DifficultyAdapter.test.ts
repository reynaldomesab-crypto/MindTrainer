import { DifficultyAdapter, DifficultyState, Preference } from '../difficulty/DifficultyAdapter';

describe('DifficultyAdapter', () => {
  const createBaseState = (overrides: Partial<DifficultyState> = {}): DifficultyState => ({
    exerciseType: 'SCHULTE',
    currentLevel: 0.3,
    recentScores: [60, 65, 70, 75, 80],
    recentAccuracies: [0.7, 0.75, 0.8, 0.85, 0.9],
    recentRTs: [25000, 23000, 21000, 19000, 17000],
    sessionsSinceChange: 0,
    plateauDetected: false,
    totalSessions: 5,
    preference: 'MAINTAIN',
    ...overrides
  });

  describe('updateDifficulty', () => {
    it('increases level for improving performance', () => {
      const state = createBaseState();
      const { newLevel, changed } = DifficultyAdapter.updateDifficulty(state, 85, 0.9, 16000);
      
      expect(changed).toBe(true);
      expect(newLevel).toBeGreaterThan(state.currentLevel);
      expect(newLevel).toBeLessThanOrEqual(1.0);
    });

    it('decreases level for declining performance', () => {
      const state = createBaseState({
        currentLevel: 0.7,
        recentScores: [80, 75, 70, 65, 60],
        recentAccuracies: [0.9, 0.85, 0.8, 0.75, 0.7],
        recentRTs: [17000, 19000, 21000, 23000, 25000],
      });
      
      const { newLevel, changed } = DifficultyAdapter.updateDifficulty(state, 55, 0.6, 28000);
      
      expect(changed).toBe(true);
      expect(newLevel).toBeLessThan(state.currentLevel);
      expect(newLevel).toBeGreaterThanOrEqual(0.0);
    });

    it('maintains level for stable performance', () => {
      const state = createBaseState({
        currentLevel: 0.5,
        recentScores: [70, 72, 71, 73, 71],
        recentAccuracies: [0.8, 0.81, 0.79, 0.82, 0.8],
        recentRTs: [20000, 20500, 19800, 20200, 20100],
      });
      
      const { newLevel, changed } = DifficultyAdapter.updateDifficulty(state, 71, 0.8, 20000);
      
      expect(changed).toBe(false);
      expect(newLevel).toBeCloseTo(state.currentLevel, 3);
    });

    it('CHALLENGE preference increases faster', () => {
      const baseState = createBaseState({
        recentScores: [70, 75, 80, 85, 90],
        recentAccuracies: [0.8, 0.85, 0.9, 0.92, 0.95],
        recentRTs: [25000, 22000, 20000, 18000, 16000],
      });
      
      const maintainState = { ...baseState, preference: 'MAINTAIN' as Preference };
      const relaxState = { ...baseState, preference: 'RELAX' as Preference };
      
      const { newLevel: challengeLevel } = DifficultyAdapter.updateDifficulty(baseState, 90, 0.95, 15000);
      const { newLevel: maintainLevel } = DifficultyAdapter.updateDifficulty(maintainState, 90, 0.95, 15000);
      const { newLevel: relaxLevel } = DifficultyAdapter.updateDifficulty(relaxState, 90, 0.95, 15000);
      
      expect(challengeLevel).toBeGreaterThan(maintainLevel);
      expect(maintainLevel).toBeGreaterThan(relaxLevel);
    });

    it('RELAX preference decreases slower', () => {
      const baseState = createBaseState({
        currentLevel: 0.7,
        recentScores: [60, 55, 50, 45, 40],
        recentAccuracies: [0.7, 0.65, 0.6, 0.55, 0.5],
        recentRTs: [25000, 27000, 29000, 31000, 33000],
        preference: 'RELAX' as Preference,
      });
      
      const challengeState = { ...baseState, preference: 'CHALLENGE' as Preference };
      const maintainState = { ...baseState, preference: 'MAINTAIN' as Preference };
      
      const { newLevel: relaxLevel } = DifficultyAdapter.updateDifficulty(baseState, 40, 0.45, 35000);
      const { newLevel: maintainLevel } = DifficultyAdapter.updateDifficulty(maintainState, 40, 0.45, 35000);
      const { newLevel: challengeLevel } = DifficultyAdapter.updateDifficulty(challengeState, 40, 0.45, 35000);
      
      expect(relaxLevel).toBeGreaterThan(maintainLevel);
      expect(maintainLevel).toBeGreaterThan(challengeLevel);
    });

    it('detects plateau after 14 sessions without improvement', () => {
      const state = createBaseState({
        currentLevel: 0.5,
        recentScores: Array(15).fill(72),
        recentAccuracies: Array(15).fill(0.8),
        recentRTs: Array(15).fill(20000),
      });
      state.sessionsSinceChange = 14;
      
      const { newLevel, changed } = DifficultyAdapter.updateDifficulty(state, 72, 0.8, 20000);
      
      expect(state.plateauDetected).toBe(true);
      expect(changed).toBe(true);
      expect(newLevel).toBeGreaterThan(state.currentLevel);
    });
  });

  describe('getExerciseParams', () => {
    it('returns correct Schulte params', () => {
      let params = DifficultyAdapter.getExerciseParams('SCHULTE', 0.0);
      expect(params.size).toBe(4);
      expect(params.timeLimitMs).toBe(60000);
      
      params = DifficultyAdapter.getExerciseParams('SCHULTE', 0.3);
      expect(params.size).toBe(5);
      
      params = DifficultyAdapter.getExerciseParams('SCHULTE', 0.8);
      expect(params.size).toBe(7);
    });

    it('returns correct Blindfold params', () => {
      let params = DifficultyAdapter.getExerciseParams('BLINDFOLD', 0.0);
      expect(params.tier).toBe(1);
      expect(params.maxRmsError).toBe(1.5);
      
      params = DifficultyAdapter.getExerciseParams('BLINDFOLD', 0.5);
      expect(params.tier).toBe(2);
      
      params = DifficultyAdapter.getExerciseParams('BLINDFOLD', 0.9);
      expect(params.tier).toBe(3);
    });

    it('returns correct NonDominant params', () => {
      let params = DifficultyAdapter.getExerciseParams('NON_DOMINANT', 0.0);
      expect(params.tasks).toEqual(['COPYWRITING']);
      
      params = DifficultyAdapter.getExerciseParams('NON_DOMINANT', 0.3);
      expect(params.tasks).toEqual(['COPYWRITING', 'TRACING']);
      
      params = DifficultyAdapter.getExerciseParams('NON_DOMINANT', 0.9);
      expect(params.tasks).toEqual(['COPYWRITING', 'TRACING', 'TAPPING']);
    });

    it('returns correct Stroop params', () => {
      let params = DifficultyAdapter.getExerciseParams('STROOP', 0.0);
      expect(params.colorCount).toBe(4);
      expect(params.incongruentRatio).toBe(0.2);
      expect(params.mode).toBe('CLASSIC');
      
      params = DifficultyAdapter.getExerciseParams('STROOP', 0.5);
      expect(params.colorCount).toBe(5);
      expect(params.mode).toBe('SWITCH');
      
      params = DifficultyAdapter.getExerciseParams('STROOP', 0.9);
      expect(params.colorCount).toBe(6);
      expect(params.mode).toBe('SPEED');
    });
  });

  describe('createInitialState', () => {
    it('sets correct initial levels', () => {
      const challengeState = DifficultyAdapter.createInitialState('SCHULTE', 'CHALLENGE');
      const maintainState = DifficultyAdapter.createInitialState('SCHULTE', 'MAINTAIN');
      const relaxState = DifficultyAdapter.createInitialState('SCHULTE', 'RELAX');
      
      expect(challengeState.currentLevel).toBeCloseTo(0.3, 1);
      expect(maintainState.currentLevel).toBeCloseTo(0.1, 1);
      expect(relaxState.currentLevel).toBeCloseTo(0.0, 1);
    });
  });

  describe('serialize/deserialize', () => {
    it('round-trips correctly', () => {
      const original: DifficultyState = {
        exerciseType: 'SCHULTE',
        currentLevel: 0.42,
        recentScores: [70, 75, 80],
        recentAccuracies: [0.8, 0.85, 0.9],
        recentRTs: [20000, 19000, 18000],
        sessionsSinceChange: 3,
        plateauDetected: false,
        totalSessions: 10,
        preference: 'MAINTAIN',
      };
      
      const json = DifficultyAdapter.serialize(original);
      const deserialized = DifficultyAdapter.deserialize(json);
      
      expect(deserialized.exerciseType).toBe(original.exerciseType);
      expect(deserialized.currentLevel).toBeCloseTo(original.currentLevel, 1);
      expect(deserialized.preference).toBe(original.preference);
    });
  });
});