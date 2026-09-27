'use client';

import { useState, useEffect, useRef, useCallback } from 'react';
import { StroopGenerator, StroopStimulus, StroopMode, StroopConfig } from '@/shared/logic/stroop/StroopGenerator';
import { StroopScorer, StroopTrial } from '@/shared/logic/stroop/StroopScorer';

interface StroopGameProps {
  dailyData: {
    mode: StroopMode;
    config: StroopConfig;
    stimuli: StroopStimulus[];
    expiresAt: string;
  };
  onComplete: (result: {
    mode: string;
    trials: Array<{
      stimulus: StroopStimulus;
      response: string;
      rtMs: number;
      correct: boolean;
      switchTrial: boolean;
      condition: 'CONGRUENT' | 'INCONGRUENT' | 'NEUTRAL';
    }>;
  }) => void;
}

export function StroopGame({ dailyData, onComplete }: StroopGameProps) {
  const [stimuli, setStimuli] = useState<StroopStimulus[]>(dailyData.stimuli);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isRunning, setIsRunning] = useState(false);
  const [isCompleted, setIsCompleted] = useState(false);
  const [showStimulus, setShowStimulus] = useState(false);
  const [stimulusStartTime, setStimulusStartTime] = useState<number>(0);
  const [trials, setTrials] = useState<{
    stimulus: StroopStimulus;
    response: string;
    rtMs: number;
    correct: boolean;
    switchTrial: boolean;
    condition: 'CONGRUENT' | 'INCONGRUENT' | 'NEUTRAL';
  }[]>([]);
  const [results, setResults] = useState<{
    interferenceEffect: number;
    interferenceRatio: number;
    inhibitionIndex: number;
    compositeScore: number;
    throughput: number;
    accuracyCongruent: number;
    accuracyIncongruent: number;
  } | null>(null);

  const stimulusTimerRef = useRef<number>();
  const stimulusDuration = dailyData.config.stimulusDurationMs;

  const currentStimulus = stimuli[currentIndex];

  // Stimulus display timer
  useEffect(() => {
    if (isRunning && showStimulus && !isCompleted) {
      stimulusTimerRef.current = setTimeout(() => {
        // Time's up - record as incorrect with max RT
        recordResponse('', false);
      }, stimulusDuration);
      return () => clearTimeout(stimulusTimerRef.current);
    }
  }, [showStimulus, isRunning, isCompleted]);

  const handleStart = useCallback(() => {
    setIsRunning(true);
    setShowStimulus(true);
    setStimulusStartTime(performance.now());
  }, []);

  const handleKeyPress = useCallback((e: React.KeyboardEvent) => {
    if (!isRunning || isCompleted || !showStimulus) return;

    const key = e.key.toUpperCase();
    const colorKeys = ['R', 'B', 'G', 'Y', 'P', 'O']; // R=Red, B=Blue, G=Green, Y=Yellow, P=Purple, O=Orange
    
    if (colorKeys.includes(key)) {
      const colorMap: Record<string, string> = {
        'R': 'RED', 'B': 'BLUE', 'G': 'GREEN', 
        'Y': 'YELLOW', 'P': 'PURPLE', 'O': 'ORANGE'
      };
      recordResponse(colorMap[key], true);
    }
  }, []);

  const recordResponse = useCallback((response: string, validKey: boolean) => {
    if (!validKey || !showStimulus) return;

    const rtMs = performance.now() - stimulusStartTime;
    const stimulus = stimuli[currentIndex];
    
    // Determine correct response based on mode
    let correct = false;
    const mode = dailyData.mode;
    
    if (mode === 'CLASSIC' || mode === 'SPATIAL') {
      // Name the ink color
      correct = response === stimulus.inkColor;
    } else if (mode === 'REVERSE') {
      // Name the word meaning
      correct = response === stimulus.word;
    } else if (mode === 'SWITCH') {
      // Depends on task type
      correct = stimulus.taskType === 'COLOR' 
        ? response === stimulus.inkColor 
        : response === stimulus.word;
    } else if (mode === 'SPEED') {
      correct = response === stimulus.inkColor;
    } else if (mode === 'SEQUENCE') {
      correct = response === stimulus.inkColor && stimulus.isTarget;
    }

    const trial: StroopTrial = {
      stimulus,
      response,
      rtMs,
      correct,
      isSwitchTrial: stimulus.isSwitchTrial || false,
      condition: stimulus.condition,
    };

    setTrials(prev => [...prev, trial]);
    setShowStimulus(false);

    // Move to next stimulus after brief delay
    setTimeout(() => {
      if (currentIndex < stimuli.length - 1) {
        setCurrentIndex(prev => prev + 1);
        setShowStimulus(true);
        setStimulusStartTime(performance.now());
      } else {
        finishGame();
      }
    }, 300);
  }, [currentIndex, stimuli]);

  const finishGame = useCallback(() => {
    setIsRunning(false);
    setIsCompleted(true);
    
    // Calculate results using scorer
    const scoringResult = StroopScorer.scoreSession(
      dailyData.mode as any,
      trials.map(t => ({
        stimulus: t.stimulus,
        response: t.response,
        rtMs: t.rtMs,
        correct: t.correct,
        isSwitchTrial: t.isSwitchTrial,
        condition: t.condition,
      })),
      dailyData.config
    );

    setResults({
      interferenceEffect: scoringResult.interferenceEffect,
      interferenceRatio: scoringResult.interferenceRatio,
      inhibitionIndex: scoringResult.inhibitionIndex,
      compositeScore: scoringResult.compositeScore,
      throughput: scoringResult.throughput,
      accuracyCongruent: scoringResult.accuracyCongruent,
      accuracyIncongruent: scoringResult.accuracyIncongruent,
    });

    // Submit to backend
    onComplete({
      mode: dailyData.mode,
      trials: trials.map(t => ({
        stimulus: t.stimulus,
        response: t.response,
        rtMs: t.rtMs,
        correct: t.correct,
        switchTrial: t.isSwitchTrial,
        condition: t.condition,
      })),
    });
  }, [trials, onComplete]);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h2 className="text-headline-sm font-medium text-foreground">
          Stroop Challenge - {dailyData.mode}
        </h2>
        <p className="text-body-sm text-muted-foreground">
          {dailyData.mode === 'CLASSIC' && 'Name the INK COLOR, ignore the word'}
          {dailyData.mode === 'REVERSE' && 'Name the WORD MEANING, ignore the ink color'}
          {dailyData.mode === 'SWITCH' && 'Switch between COLOR and WORD tasks as cued'}
          {dailyData.mode === 'SPATIAL' && 'Tap the side where the word appears (LEFT/RIGHT)'}
          {dailyData.mode === 'SPEED' && 'Respond as fast as possible for 30 seconds'}
          {dailyData.mode === 'SEQUENCE' && 'Remember the sequence, respond only to those colors'}
        </p>
      </div>

      {/* Progress */}
      <div className="flex items-center justify-between">
        <div className="flex-1 h-2 bg-muted rounded-full overflow-hidden">
          <div
            className="h-full bg-primary rounded-full transition-all duration-200"
            style={{ width: `${((currentIndex + 1) / stimuli.length) * 100}%` }}
          />
        </div>
        <span className="ml-4 text-sm text-muted-foreground">
          {currentIndex + 1} / {stimuli.length}
        </span>
      </div>

      {/* Stimulus Display */}
      <div className="relative">
        <div
          className={`w-full max-w-md mx-auto aspect-square rounded-xl border-4 flex items-center justify-center ${
            showStimulus ? 'bg-background' : 'bg-muted/50'
          }`}
          style={{ minHeight: 300 }}
        >
          {showStimulus && currentStimulus && (
            <div className="text-center">
              <span
                className="text-7xl font-bold"
                style={{ color: currentStimulus.inkColor }}
              >
                {currentStimulus.word}
              </span>
              {dailyData.mode === 'SWITCH' && currentStimulus.taskType && (
                <div className="mt-4 text-sm text-muted-foreground">
                  Task: {currentStimulus.taskType}
                </div>
              )}
              {currentStimulus.isSwitchTrial && (
                <div className="mt-2 text-sm text-amber-500 font-medium">⇄ SWITCH</div>
              )}
            </div>
          )}

          {!showStimulus && !isCompleted && (
            <div className="absolute inset-0 flex items-center justify-center">
              <div className="text-center text-muted-foreground">
                <p className="text-lg mb-2">Get ready...</p>
                <p className="text-sm">Next stimulus in 0.3s</p>
              </div>
            </div>
          )}

          {!isRunning && !isCompleted && (
            <div className="absolute inset-0 flex items-center justify-center">
              <button
                onClick={() => {
                  setIsRunning(true);
                  setShowStimulus(true);
                }}
                className="px-8 py-4 bg-primary text-primary-foreground rounded-xl text-lg font-medium shadow-lg hover:bg-primary/90 transition-colors"
              >
                Start {dailyData.mode}
              </button>
            </div>
          )}

          {isCompleted && (
            <div className="absolute inset-0 flex items-center justify-center bg-background/95 rounded-xl p-6">
              <div className="text-center max-w-sm">
                <div className="text-4xl mb-2">✓</div>
                <h3 className="text-2xl font-medium text-foreground mb-2">Complete!</h3>
                <div className="space-y-2 text-muted-foreground mb-4">
                  <p>Interference: {results?.interferenceEffect?.toFixed(0)}ms</p>
                  <p>Inhibition Index: {(results?.inhibitionIndex * 100).toFixed(0)}%</p>
                  <p>Score: {results?.compositeScore}/100</p>
                  {results?.throughput && <p>Throughput: {results.throughput.toFixed(1)}/sec</p>}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Color Buttons (for CLASSIC, REVERSE, SPEED modes) */}
      {(dailyData.mode === 'CLASSIC' || dailyData.mode === 'REVERSE' || dailyData.mode === 'SPEED') && (
        <div className="grid grid-cols-3 gap-2 max-w-md mx-auto">
          {['RED', 'BLUE', 'GREEN', 'YELLOW', 'PURPLE', 'ORANGE']
            .slice(0, dailyData.config.colorCount)
            .map((color, i) => (
              <button
                key={color}
                onClick={() => recordResponse(color)}
                disabled={!isRunning || isCompleted || !showStimulus}
                className={`h-14 rounded-xl font-medium text-lg font-medium transition-colors ${
                  getColorClass(color)
                }`}
                style={{ backgroundColor: getColorHex(color) }}
              >
                {color}
              </button>
            ))}
        </div>
      )}

      {/* Spatial buttons (for SPATIAL mode) */}
      {dailyData.mode === 'SPATIAL' && (
        <div className="flex gap-4 max-w-md mx-auto">
          {['LEFT', 'RIGHT'].map(side => (
            <button
              key={side}
              onClick={() => recordResponse(side)}
              disabled={!isRunning || isCompleted || !showStimulus}
              className="flex-1 h-16 rounded-xl font-medium text-xl bg-muted text-foreground hover:bg-muted/80 transition-colors disabled:opacity-50"
            >
              {side}
            </button>
          ))}
        </div>
      )}

      {!isRunning && !isCompleted && (
        <div className="text-center">
          <button
            onClick={() => setIsRunning(true)}
            className="px-8 py-4 bg-primary text-primary-foreground rounded-xl text-lg font-medium shadow-lg hover:bg-primary/90 transition-colors"
          >
            Start {dailyData.mode}
          </button>
        </div>
      )}

      {isCompleted && results && (
        <div className="bg-gradient-to-r from-primary/10 to-secondary/10 rounded-xl p-6 text-center animate-in">
          <div className="text-4xl mb-2">✓</div>
          <h3 className="text-2xl font-medium text-foreground mb-2">Complete!</h3>
          <div className="grid grid-cols-2 gap-4 mb-4">
            <div className="p-3 bg-background/50 rounded-lg">
              <p className="text-2xl font-bold text-foreground">{results.compositeScore}</p>
              <p className="text-xs text-muted-foreground">Score</p>
            </div>
            <div className="p-3 bg-background/50 rounded-lg">
              <p className="text-2xl font-bold text-foreground">{results.interferenceEffect.toFixed(0)}ms</p>
              <p className="text-xs text-muted-foreground">Interference</p>
            </div>
            <div className="p-3 bg-background/50 rounded-lg">
              <p className="text-2xl font-bold text-foreground">{(results.inhibitionIndex * 100).toFixed(0)}%</p>
              <p className="text-xs text-muted-foreground">Inhibition</p>
            </div>
            <div className="p-3 bg-background/50 rounded-lg">
              <p className="text-2xl font-bold text-foreground">{(results.accuracyIncongruent * 100).toFixed(0)}%</p>
              <p className="text-xs text-muted-foreground">Incong. Acc.</p>
            </div>
          </div>
          <p className="text-sm text-muted-foreground mb-4">
            Congruent: {(results.accuracyCongruent * 100).toFixed(0)}% • Incongruent: {(results.accuracyIncongruent * 100).toFixed(0)}%
          </p>
        </div>
      )}
    </div>
  );
}

function getColorHex(color: string): string {
  const colors: Record<string, string> = {
    RED: '#C0392B',
    BLUE: '#2980B9',
    GREEN: '#27AE60',
    YELLOW: '#F39C12',
    PURPLE: '#8E44AD',
    ORANGE: '#E67E22',
  };
  return colors[color] || '#6B7C93';
}

function getColorClass(color: string): string {
  const lightColors = ['YELLOW'];
  return lightColors.includes(color) 
    ? 'text-gray-900 dark:text-gray-100' 
    : 'text-white';
}

function recordResponse(response: string) {
  // This would be connected to the main component's recordResponse
  // In a real implementation, this would use a context or prop callback
}