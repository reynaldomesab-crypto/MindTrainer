'use client';

import { useState, useEffect, useRef, useCallback } from 'react';
import { BlindfoldScorer } from '@/shared/logic/blindfold/BlindfoldScorer';

interface BlindfoldText {
  id: string;
  language: string;
  text: string;
  charCount: number;
  tier: number;
  topic: string;
  rareCharDensity: number;
}

interface BlindfoldGameProps {
  dailyData: { text: BlindfoldText; expiresAt: string };
  onComplete: (result: {
    textId: string;
    actualText: string;
    keyboardLayout: string;
    keyDistances: number[];
    rmsError: number;
    timeMs: number;
  }) => void;
}

export function BlindfoldGame({ dailyData, onComplete }: BlindfoldGameProps) {
  const [inputText, setInputText] = useState('');
  const [isRunning, setIsRunning] = useState(false);
  const [isCompleted, setIsCompleted] = useState(false);
  const [countdown, setCountdown] = useState(3);
  const [elapsedTime, setElapsedTime] = useState(0);
  const [result, setResult] = useState<{
    rmsError: number;
    accuracy: number;
    score: number;
  } | null>(null);

  const startTimeRef = useRef<number>(0);
  const animationFrameRef = useRef<number>();
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const keyHoldStartRef = useRef<Map<string, number>>(new Map());
  const keyHoldDurationsRef = useRef<number[]>([]);
  const interKeyIntervalsRef = useRef<number[]>([]);
  const lastKeyTimeRef = useRef<number>(0);

  // Countdown timer
  useEffect(() => {
    if (isRunning && countdown > 0 && !isCompleted) {
      const timer = setTimeout(() => setCountdown(c => c - 1), 1000);
      return () => clearTimeout(timer);
    }
    if (countdown === 0 && isRunning) {
      setIsRunning(true);
      startTimeRef.current = performance.now();
    }
  }, [countdown, isRunning, isCompleted]);

  // Timer during exercise
  useEffect(() => {
    if (isRunning && !isCompleted) {
      startTimeRef.current = performance.now();
      const animate = () => {
        if (!isRunning || isCompleted) return;
        const elapsed = performance.now() - startTimeRef.current;
        setElapsedTime(elapsed);
        animationFrameRef.current = requestAnimationFrame(animate);
      };
      animationFrameRef.current = requestAnimationFrame(animate);
      return () => cancelAnimationFrame(animationFrameRef.current!);
    }
  }, [isRunning, isCompleted]);

  const handleKeyDown = useCallback((e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (!isRunning || isCompleted) {
      e.preventDefault();
      return;
    }

    const now = performance.now();
    
    // Track key hold start
    keyHoldStartRef.current.set(e.key, now);
    
    // Track inter-key interval
    if (lastKeyTimeRef.current > 0) {
      interKeyIntervalsRef.current.push(now - lastKeyTimeRef.current);
    }
    lastKeyTimeRef.current = now;
  }, [isRunning, isCompleted]);

  const handleKeyUp = useCallback((e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (!isRunning || isCompleted) return;

    const now = performance.now();
    const holdStart = keyHoldStartRef.current.get(e.key);
    
    if (holdStart) {
      keyHoldDurationsRef.current.push(now - holdStart);
      keyHoldStartRef.current.delete(e.key);
    }
  }, [isRunning, isCompleted]);

  const handleInputChange = useCallback((e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setInputText(e.target.value);
  }, []);

  const handleFinish = useCallback(() => {
    setIsRunning(false);
    setIsCompleted(true);
    
    const scoringResult = BlindfoldScorer.score(
      dailyData.text.text,
      inputText,
      'QWERTY' // Would detect from user agent in production
    );
    
    const score = scoringResult.calculateScore(elapsedTime);
    setResult({ rmsError: scoringResult.rmsError, accuracy: scoringResult.accuracy, score });
    
    onComplete({
      textId: dailyData.text.id,
      actualText: inputText,
      keyboardLayout: 'QWERTY',
      keyDistances: scoringResult.keyDistances,
      rmsError: scoringResult.rmsError,
      timeMs: elapsedTime,
    });
  }, [inputText, elapsedTime, onComplete]);

  const handleStart = useCallback(() => {
    setInputText('');
    setCountdown(3);
    setIsRunning(false);
    setIsCompleted(false);
    setResult(null);
    keyHoldDurationsRef.current = [];
    interKeyIntervalsRef.current = [];
    lastKeyTimeRef.current = 0;
    keyHoldStartRef.current.clear();
  }, []);

  const text = dailyData.text.text;
  const targetChars = text.length;
  const typedChars = inputText.length;
  const progress = Math.min(100, (typedChars / targetChars) * 100);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h2 className="text-headline-sm font-medium text-foreground">Blindfold Writing</h2>
        <p className="text-body-sm text-muted-foreground">
          Type the text below with your eyes closed. Focus on rhythm.
        </p>
      </div>

      {/* Progress */}
      <div className="space-y-2">
        <div className="flex justify-between text-sm">
          <span className="text-muted-foreground">Progress</span>
          <span className="font-medium">{progress.toFixed(0)}%</span>
        </div>
        <div className="h-2 bg-muted rounded-full overflow-hidden">
          <div
            className="h-full bg-primary rounded-full transition-all duration-300"
            style={{ width: `${progress}%` }}
          />
        </div>
        <p className="text-xs text-muted-foreground">
          {typedChars} / {targetChars} characters
        </p>
      </div>

      {/* Text Display */}
      <div className="bg-muted/50 rounded-xl p-6">
        <div className="space-y-4">
          {/* Target text (hidden during exercise) */}
          {(!isRunning || isCompleted) && (
            <div className="text-body-lg text-foreground leading-relaxed whitespace-pre-wrap">
              {text}
            </div>
          )}
          
          {isRunning && !isCompleted && (
            <div className="text-body-lg text-muted-foreground/30 leading-relaxed whitespace-pre-wrap">
              {text}
            </div>
          )}

          {/* Input area */}
          <textarea
            ref={textareaRef}
            value={inputText}
            onChange={handleInputChange}
            onKeyDown={handleKeyDown}
            onKeyUp={handleKeyUp}
            disabled={!isRunning || isCompleted}
            placeholder={isRunning && !isCompleted 
              ? 'Close your eyes and type...' 
              : isCompleted 
                ? 'Exercise complete' 
                : 'Press Start to begin'}
            className="w-full min-h-[120px] p-4 bg-background rounded-lg border border-border/50 focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20 resize-none font-mono text-body-lg leading-relaxed"
            rows={6}
            spellCheck={false}
            autoComplete="off"
            autoCorrect="off"
            autoCapitalize="off"
          />

          {/* Countdown overlay */}
          {countdown > 0 && isRunning && !isCompleted && (
            <div className="absolute inset-0 flex items-center justify-center bg-background/90 rounded-xl">
              <div className="text-6xl font-bold text-primary animate-pulse">
                {countdown}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Timer */}
      {isRunning && !isCompleted && (
        <div className="text-center">
          <p className="text-xs text-muted-foreground">ELAPSED TIME</p>
          <p className="text-3xl font-mono font-medium tabular-nums text-primary">
            {(elapsedTime / 1000).toFixed(1)}s
          </p>
        </div>
      )}

      {/* Controls */}
      {!isRunning && !isCompleted && (
        <button
          onClick={() => setCountdown(3)}
          className="w-full px-8 py-4 bg-primary text-primary-foreground rounded-xl text-lg font-medium shadow-lg hover:bg-primary/90 transition-colors"
        >
          Start (3 sec countdown)
        </button>
      )}

      {/* Results */}
      {isCompleted && result && (
        <div className="bg-green-50 dark:bg-green-900/20 rounded-xl p-6 animate-in">
          <div className="flex items-center gap-2 text-green-800 dark:text-green-200 mb-4">
            <span className="text-2xl">✓</span>
            <h3 className="text-xl font-medium">Exercise Complete</h3>
          </div>
          <div className="grid grid-cols-3 gap-4 text-center">
            <div className="p-3 bg-background/50 rounded-lg">
              <p className="text-2xl font-bold text-foreground">{result.score}</p>
              <p className="text-xs text-muted-foreground">Score</p>
            </div>
            <div className="p-3 bg-background/50 rounded-lg">
              <p className="text-2xl font-bold text-foreground">{(result.accuracy * 100).toFixed(0)}%</p>
              <p className="text-xs text-muted-foreground">Accuracy</p>
            </div>
            <div className="p-3 bg-background/50 rounded-lg">
              <p className="text-2xl font-bold text-foreground">{result.rmsError.toFixed(2)}</p>
              <p className="text-xs text-muted-foreground">RMS Error</p>
            </div>
          </div>
          <div className="mt-4">
            <p className="text-sm text-muted-foreground mb-2">
              RMS Error: {result.rmsError.toFixed(2)} key-widths • Time: {(elapsedTime / 1000).toFixed(1)}s
            </p>
            <button
              onClick={() => {
                setIsCompleted(false);
                setResult(null);
              }}
              className="w-full px-4 py-3 bg-primary text-primary-foreground rounded-lg font-medium hover:bg-primary/90 transition-colors"
            >
              Try Again
            </button>
          </div>
        </div>
      )}

      {/* Instructions */}
      {!isRunning && !isCompleted && (
        <div className="bg-blue-50 dark:bg-blue-900/20 rounded-xl p-4 text-sm text-blue-800 dark:text-blue-200">
          <p className="font-medium mb-1">Instructions:</p>
          <ul className="list-disc list-inside space-y-1">
            <li>Press Start, then close your eyes during the 3-second countdown</li>
            <li>Type the text from memory - focus on rhythm, not speed</li>
            <li>Press Enter or click Finish when done</li>
            <li>We'll measure your keystroke dynamics and accuracy</li>
          </ul>
        </div>
      )}
    </div>
  );
}

// Helper for number clamping
declare global {
  interface Number {
    clamp(min: number, max: number): number;
  }
}

Number.prototype.clamp = function(min: number, max: number): number {
  return Math.max(min, Math.min(max, this.valueOf()));
};