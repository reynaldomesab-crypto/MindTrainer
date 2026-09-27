import { NonDomMetrics, Point } from '../nondom/NonDomMetrics';

describe('NonDomMetrics', () => {
  describe('evaluateTracing', () => {
    it('detects perfect trace', () => {
      const idealPath: Point[] = [
        { x: 0.1, y: 0.5 }, { x: 0.3, y: 0.5 }, 
        { x: 0.5, y: 0.5 }, { x: 0.7, y: 0.5 }, 
        { x: 0.9, y: 0.5 }
      ];
      const actualPath: Point[] = idealPath.map(p => ({ ...p }));
      const timestamps = [0, 250, 500, 750, 1000];
      
      const result = NonDomMetrics.evaluateTracing(idealPath, actualPath, timestamps, 1000);
      
      expect(result.rmse).toBeCloseTo(0.0, 3);
      expect(result.smoothness).toBeCloseTo(1.0, 1);
      expect(result.cornerAccuracy).toBeCloseTo(1.0, 1);
      expect(result.timeRatio).toBeCloseTo(1.0, 1);
    });

    it('detects deviation', () => {
      const idealPath: Point[] = [
        { x: 0.1, y: 0.5 }, { x: 0.3, y: 0.5 }, 
        { x: 0.5, y: 0.5 }, { x: 0.7, y: 0.5 }, 
        { x: 0.9, y: 0.5 }
      ];
      const actualPath: Point[] = [
        { x: 0.1, y: 0.5 }, { x: 0.3, y: 0.55 }, 
        { x: 0.5, y: 0.5 }, { x: 0.7, y: 0.45 }, 
        { x: 0.9, y: 0.5 }
      ];
      const timestamps = [0, 250, 500, 750, 1000];
      
      const result = NonDomMetrics.evaluateTracing(idealPath, actualPath, timestamps, 1000);
      
      expect(result.rmse).toBeGreaterThan(0.0);
      expect(result.rmse).toBeLessThan(0.1);
    });

    it('calculates time ratio', () => {
      const idealPath: Point[] = [{ x: 0.1, y: 0.5 }, { x: 0.9, y: 0.5 }];
      const actualPath: Point[] = [{ x: 0.1, y: 0.5 }, { x: 0.9, y: 0.5 }];
      const fastTimestamps = [0, 500];
      const slowTimestamps = [0, 2000];
      
      const fastResult = NonDomMetrics.evaluateTracing(idealPath, idealPath, fastTimestamps, 1000);
      const slowResult = NonDomMetrics.evaluateTracing(idealPath, idealPath, slowTimestamps, 1000);
      
      expect(fastResult.timeRatio).toBeLessThan(slowResult.timeRatio);
    });
  });

  describe('evaluateTapping', () => {
    const targetGrid: Point[] = [
      { x: 0.1, y: 0.1 }, { x: 0.5, y: 0.1 }, { x: 0.9, y: 0.1 },
      { x: 0.1, y: 0.5 }, { x: 0.5, y: 0.5 }, { x: 0.9, y: 0.5 },
      { x: 0.1, y: 0.9 }, { x: 0.5, y: 0.9 }, { x: 0.9, y: 0.9 }
    ];
    const targetSequence = [0, 4, 8];
    const actualTaps = [targetGrid[0], targetGrid[4], targetGrid[8]];
    const tapTimestamps = [0, 500, 1000];

    it('detects perfect taps', () => {
      const result = NonDomMetrics.evaluateTapping(
        targetGrid, actualTaps, tapTimestamps, targetSequence, [0, 4, 8], 3
      );
      
      expect(result.spatialAccuracy).toBeCloseTo(1.0, 2);
      expect(result.sequenceAccuracy).toBeCloseTo(1.0, 2);
    });

    it('detects spatial errors', () => {
      const actualTapsOff = [
        { x: 0.15, y: 0.15 }, // Slightly off target 0
        { x: 0.5, y: 0.5 },   // Perfect target 4
        { x: 0.85, y: 0.85 }  // Slightly off target 8
      ];
      const tapTimestamps = [0, 500, 1000];
      
      const result = NonDomMetrics.evaluateTapping(
        targetGrid, actualTapsOff, tapTimestamps, targetSequence, [0, 4, 8], 3
      );
      
      expect(result.spatialAccuracy).toBeLessThan(1.0);
      expect(result.sequenceAccuracy).toBeCloseTo(1.0, 2);
    });

    it('detects sequence errors', () => {
      const actualTapsWrongOrder = [targetGrid[0], targetGrid[8], targetGrid[4]];
      const tapTimestamps = [0, 500, 1000];
      
      const result = NonDomMetrics.evaluateTapping(
        targetGrid, actualTapsWrongOrder, tapTimestamps, targetSequence, [0, 8, 4], 3
      );
      
      expect(result.spatialAccuracy).toBeCloseTo(1.0, 2);
      expect(result.sequenceAccuracy).toBeLessThan(1.0);
    });

    it('calculates rhythm consistency', () => {
      const perfectTimestamps = [0, 200, 400, 600, 800, 1000];
      const variableTimestamps = [0, 150, 500, 600, 900, 1000];
      const targetGrid2: Point[] = [
        { x: 0.1, y: 0.1 }, { x: 0.5, y: 0.1 }, { x: 0.9, y: 0.1 },
        { x: 0.1, y: 0.5 }, { x: 0.5, y: 0.5 }, { x: 0.9, y: 0.5 }
      ];
      const targetSequence2 = [0, 1, 2, 3, 4, 5];
      const actualTaps2 = targetGrid2;
      
      const perfectResult = NonDomMetrics.evaluateTapping(
        targetGrid2, actualTaps2, perfectTimestamps, [0,1,2,3,4,5], [0,1,2,3,4,5], 3
      );
      const variableResult = NonDomMetrics.evaluateTapping(
        targetGrid2, actualTaps2, variableTimestamps, [0,1,2,3,4,5], [0,1,2,3,4,5], 3
      );
      
      expect(perfectResult.rhythmConsistency).toBeGreaterThan(variableResult.rhythmConsistency);
    });
  });

  describe('calculateConsistency', () => {
    it('returns 1 for constant values', () => {
      const consistent = NonDomMetrics.calculateConsistency([100, 100, 100, 100]);
      expect(consistent).toBeCloseTo(1.0, 3);
    });

    it('returns low for variable values', () => {
      const variable = NonDomMetrics.calculateConsistency([100, 200, 100, 200]);
      expect(variable).toBeLessThan(0.5);
    });
  });

  describe('calculateFatigue', () => {
    it('detects increasing hold times', () => {
      const increasing = [100, 120, 140, 160, 180, 200, 220, 240, 260, 280];
      const constant = [150, 150, 150, 150, 150, 150, 150, 150, 150, 150];
      
      const increasingFatigue = NonDomMetrics.calculateFatigue(increasing);
      const constantFatigue = NonDomMetrics.calculateFatigue(constant);
      
      expect(increasingFatigue).toBeGreaterThan(0.0);
      expect(constantFatigue).toBeCloseTo(0.0, 3);
    });
  });

  describe('calculateSmoothness', () => {
    it('returns high for smooth velocities', () => {
      const smoothVelocities = [1.0, 1.1, 1.0, 1.1, 1.0, 1.1];
      const smoothness = NonDomMetrics.calculateSmoothness(smoothVelocities);
      expect(smoothness).toBeGreaterThan(0.5);
    });

    it('returns low for jerky velocities', () => {
      const jerkyVelocities = [1.0, 5.0, 1.0, 5.0, 1.0, 5.0];
      const smoothness = NonDomMetrics.calculateSmoothness(jerkyVelocities);
      expect(smoothness).toBeLessThan(0.5);
    });
  });
});