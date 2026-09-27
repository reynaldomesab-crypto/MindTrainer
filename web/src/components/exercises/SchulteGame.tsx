'use client';

import { useState, useEffect, useRef, useCallback } from 'react';
import { SchulteConfig, SchulteDaily, SchulteGenerator } from '@/shared/logic/schulte/SchulteGenerator';

interface SchulteGameProps {
  dailyData: SchulteDaily;
  onComplete: (result: {
    completionTimeMs: number;
    tappedSequence: number[];
    accuracy: number;
  }) => void;
}

export function SchulteGame({ dailyData, onComplete }: SchulteGameProps) {
  const [numbers, setNumbers] = useState<number[]>(dailyData.numbers);
  const [targetNumber, setTargetNumber] = useState(1);
  const [tappedSequence, setTappedSequence] = useState<number[]>([]);
  const [elapsedTime, setElapsedTime] = useState(0);
  const [isRunning, setIsRunning] = useState(false);
  const [isCompleted, setIsCompleted] = useState(false);
  const [accuracy, setAccuracy] = useState(0);

  const startTimeRef = useRef<number>(0);
  const animationFrameRef = useRef<number>();
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const gridSize = dailyData.config.size;
  const cellSizeRef = useRef<number>(0);

  // Timer
  useEffect(() => {
    if (isRunning && !isCompleted) {
      startTimeRef.current = performance.now();
      const animate = () => {
        if (!isRunning || isCompleted) return;
        const elapsed = performance.now() - startTimeRef.current;
        setElapsedTime(elapsed);
        if (elapsed >= dailyData.config.timeLimitMs) {
          handleFinish();
        } else {
          animationFrameRef.current = requestAnimationFrame(animate);
        }
      };
      animationFrameRef.current = requestAnimationFrame(animate);
      return () => cancelAnimationFrame(animationFrameRef.current!);
    }
  }, [isRunning, isCompleted]);

  const handleFinish = useCallback(() => {
    setIsRunning(false);
    setIsCompleted(true);
    
    const validation = SchulteGenerator.validateSequence(dailyData.numbers, tappedSequence);
    setAccuracy(validation.accuracy);
    
    onComplete({
      completionTimeMs: elapsedTime,
      tappedSequence,
      accuracy: validation.accuracy,
    });
  }, [tappedSequence, elapsedTime, onComplete]);

  const handleCanvasClick = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    if (!isRunning || isCompleted) return;

    const canvas = canvasRef.current;
    if (!canvas) return;

    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    const cellSize = cellSizeRef.current;
    if (cellSize === 0) return;

    const col = Math.floor(x / cellSize).clamp(0, gridSize - 1);
    const row = Math.floor(y / cellSize).clamp(0, gridSize - 1);
    const index = row * gridSize + col;

    if (index < numbers.length) {
      const tappedNumber = numbers[index];
      setTappedSequence(prev => [...prev, tappedNumber]);

      if (tappedNumber === targetNumber) {
        setTargetNumber(prev => prev + 1);
        if (targetNumber >= numbers.length) {
          // Will finish on next render
        }
      }
    }
  }, [numbers, targetNumber, tappedSequence]);

  const handleStart = useCallback(() => {
    setIsRunning(true);
    setTargetNumber(1);
    setTappedSequence([]);
    setElapsedTime(0);
    setAccuracy(0);
    setIsCompleted(false);
  }, []);

  const gridSize = dailyData.config.size;
  const timeLimit = dailyData.config.timeLimitMs;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-headline-sm font-medium text-foreground">Schulte Tables</h2>
          <p className="text-body-sm text-muted-foreground">
            Find numbers 1-{numbers.length} in order
          </p>
        </div>
        <div className="flex items-center gap-4">
          <div className="text-right">
            <p className="text-xs text-muted-foreground">TIME</p>
            <p className={`text-2xl font-mono font-medium tabular-nums ${
              elapsedTime > timeLimit * 0.8 ? 'text-destructive' : 'text-primary'
            }`}>
              {(elapsedTime / 1000).toFixed(1)}s
            </p>
          </div>
          <div className="text-right">
            <p className="text-xs text-muted-foreground">TARGET</p>
            <p className="text-2xl font-mono font-medium text-foreground">{targetNumber}</p>
          </div>
        </div>
      </div>

      {/* Canvas */}
      <div className="relative">
        <canvas
          ref={canvasRef}
          className="w-full max-w-[500px] mx-auto rounded-lg border bg-card"
          onClick={handleCanvasClick}
          style={{
            width: '100%',
            maxWidth: 500,
            aspectRatio: '1/1',
          }}
        />
        
        {!isRunning && !isCompleted && (
          <div className="absolute inset-0 flex items-center justify-center bg-background/80 rounded-lg">
            <button
              onClick={handleStart}
              className="px-8 py-4 bg-primary text-primary-foreground rounded-xl text-lg font-medium shadow-lg hover:bg-primary/90 transition-colors"
            >
              Start Exercise ({timeLimit / 1000}s)
            </button>
          </div>
        )}

        {isCompleted && (
          <div className="absolute inset-0 flex items-center justify-center bg-background/90 rounded-lg p-6">
            <div className="text-center max-w-sm">
              <div className="text-4xl mb-2">✓</div>
              <h3 className="text-2xl font-medium text-foreground mb-2">Complete!</h3>
              <div className="space-y-1 text-muted-foreground mb-4">
                <p>Time: {(elapsedTime / 1000).toFixed(1)}s</p>
                <p>Accuracy: {(accuracy * 100).toFixed(0)}%</p>
                <p>Score: {Math.round(1000 * accuracy * (1 - elapsedTime / timeLimit))}</p>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Instructions */}
      {!isRunning && !isCompleted && (
        <p className="text-sm text-muted-foreground text-center">
          Tap numbers 1 through {numbers.length} in order as fast as you can.
        </p>
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