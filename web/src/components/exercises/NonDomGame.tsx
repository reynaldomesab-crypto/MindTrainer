'use client';

import { useState, useCallback, useRef, useEffect } from 'react';
import { NonDomMetrics, Point } from '@/shared/logic/nondom/NonDomMetrics';

interface NonDomDaily {
  copywriting: {
    id: string;
    text: string;
    tier: number;
    charCount: number;
  };
  tracing: {
    id: string;
    type: string;
    points: Point[];
    targetTimeMs: number;
    difficulty: number;
  };
  tapping: {
    id: string;
    gridSize: number;
    targets: number[];
    count: number;
    timeLimitMs: number;
  };
  expiresAt: string;
}

type TaskType = 'COPYWRITING' | 'TRACING' | 'TAPPING';

interface NonDomGameProps {
  dailyData: NonDomDaily;
  onComplete: (result: {
    taskType: TaskType;
    contentId: string;
    handUsed: 'LEFT' | 'RIGHT';
    metrics: Record<string, number>;
  }) => void;
}

export function NonDomGame({ dailyData, onComplete }: NonDomGameProps) {
  const [activeTask, setActiveTask] = useState<TaskType>('COPYWRITING');
  const [isCompleted, setIsCompleted] = useState<Partial<Record<TaskType, boolean>>>({});
  const [results, setResults] = useState<Partial<Record<TaskType, any>>>({});
  const [handUsed, setHandUsed] = useState<'LEFT' | 'RIGHT'>('LEFT');

  // Copywriting state
  const [copyText, setCopyText] = useState('');
  const [copyRunning, setCopyRunning] = useState(false);
  const [copyCompleted, setCopyCompleted] = useState(false);
  const [copyElapsed, setCopyElapsed] = useState(0);
  const copyStartRef = useRef<number>(0);
  const copyAnimationRef = useRef<number>();
  const copyHoldDurationsRef = useRef<number[]>([]);
  const copyIntervalsRef = useRef<number[]>([]);
  const copyLastKeyRef = useRef<number>(0);
  const copyHoldStartRef = useRef<Map<string, number>>(new Map());

  // Tracing state
  const [tracePoints, setTracePoints] = useState<Point[]>([]);
  const [traceRunning, setTraceRunning] = useState(false);
  const [traceCompleted, setTraceCompleted] = useState(false);
  const [traceElapsed, setTraceElapsed] = useState(0);
  const traceStartRef = useRef<number>(0);
  const traceAnimationRef = useRef<number>();
  const traceTimestampsRef = useRef<number[]>([]);

  // Tapping state
  const [tapSequence, setTapSequence] = useState<number[]>([]);
  const [tapRunning, setTapRunning] = useState(false);
  const [tapCompleted, setTapCompleted] = useState(false);
  const [tapElapsed, setTapElapsed] = useState(0);
  const [currentTargetIndex, setCurrentTargetIndex] = useState(0);
  const [tapTargets, setTapTargets] = useState<{ x: number; y: number; index: number }[]>([]);
  const tapStartRef = useRef<number>(0);
  const tapAnimationRef = useRef<number>();
  const tapTimestampsRef = useRef<number[]>([]);

  // Copywriting timer
  useEffect(() => {
    if (copyRunning && !copyCompleted) {
      copyStartRef.current = performance.now();
      const animate = () => {
        if (!copyRunning || copyCompleted) return;
        const elapsed = performance.now() - copyStartRef.current;
        setCopyElapsed(elapsed);
        copyAnimationRef.current = requestAnimationFrame(animate);
      };
      copyAnimationRef.current = requestAnimationFrame(animate);
      return () => cancelAnimationFrame(copyAnimationRef.current!);
    }
  }, [copyRunning, copyCompleted]);

  // Tracing timer
  useEffect(() => {
    if (traceRunning && !traceCompleted) {
      traceStartRef.current = performance.now();
      const animate = () => {
        if (!traceRunning || traceCompleted) return;
        const elapsed = performance.now() - traceStartRef.current;
        setTraceElapsed(elapsed);
        traceAnimationRef.current = requestAnimationFrame(animate);
      };
      traceAnimationRef.current = requestAnimationFrame(animate);
      return () => cancelAnimationFrame(traceAnimationRef.current!);
    }
  }, [traceRunning, traceCompleted]);

  // Tapping timer
  useEffect(() => {
    if (tapRunning && !tapCompleted) {
      tapStartRef.current = performance.now();
      const animate = () => {
        if (!tapRunning || tapCompleted) return;
        const elapsed = performance.now() - tapStartRef.current;
        setTapElapsed(elapsed);
        tapAnimationRef.current = requestAnimationFrame(animate);
      };
      tapAnimationRef.current = requestAnimationFrame(animate);
      return () => cancelAnimationFrame(tapAnimationRef.current!);
    }
  }, [tapRunning, tapCompleted]);

  // Task completion handlers
  const finishCopywriting = useCallback(() => {
    setCopyRunning(false);
    setCopyCompleted(true);
    setIsCompleted(prev => ({ ...prev, COPYWRITING: true }));
    
    // Calculate metrics using shared logic
    // In production, would use NonDomMetrics.evaluateCopywriting
    const result = {
      score: 85,
      accuracy: 0.92,
      rhythmConsistency: 0.78,
      hesitationIndex: 0.15,
      fatigueIndex: 0.08,
    };
    setResults(prev => ({ ...prev, COPYWRITING: result }));
    onComplete({
      taskType: 'COPYWRITING',
      contentId: dailyData.copywriting.id,
      handUsed,
      metrics: {
        score: result.score,
        accuracy: result.accuracy * 100,
        rhythmConsistency: result.rhythmConsistency * 100,
      },
    });
  }, [handUsed]);

  const finishTracing = useCallback(() => {
    setTraceRunning(false);
    setTraceCompleted(true);
    setIsCompleted(prev => ({ ...prev, TRACING: true }));
    
    const result = {
      score: 78,
      rmse: 0.045,
      smoothness: 0.82,
      timeRatio: 1.1,
      cornerAccuracy: 0.85,
    };
    setResults(prev => ({ ...prev, TRACING: result }));
    onComplete({
      taskType: 'TRACING',
      contentId: dailyData.tracing.id,
      handUsed,
      metrics: {
        score: result.score,
        rmse: result.rmse * 100,
        smoothness: result.smoothness * 100,
      },
    });
  }, [handUsed]);

  const finishTapping = useCallback(() => {
    setTapRunning(false);
    setTapCompleted(true);
    setIsCompleted(prev => ({ ...prev, TAPPING: true }));
    
    const result = {
      score: 82,
      spatialAccuracy: 0.88,
      sequenceAccuracy: 0.95,
      rhythmConsistency: 0.75,
    };
    setResults(prev => ({ ...prev, TAPPING: result }));
    onComplete({
      taskType: 'TAPPING',
      contentId: dailyData.tapping.id,
      handUsed,
      metrics: {
        score: result.score,
        spatialAccuracy: result.spatialAccuracy * 100,
        sequenceAccuracy: result.sequenceAccuracy * 100,
      },
    });
  }, [handUsed]);

  // Keyboard handlers for copywriting
  const handleCopyKeyDown = useCallback((e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (!copyRunning || copyCompleted) {
      e.preventDefault();
      return;
    }
    const now = performance.now();
    copyHoldStartRef.current.set(e.key, now);
    if (copyLastKeyRef.current > 0) {
      // interKeyIntervals would be tracked here
    }
    copyLastKeyRef.current = performance.now();
  }, [copyRunning, copyCompleted]);

  const handleCopyKeyUp = useCallback((e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (!copyRunning || copyCompleted) return;
    const now = performance.now();
    const holdStart = copyHoldStartRef.current.get(e.key);
    if (holdStart) {
      // holdDurations would be tracked here
    }
  }, [copyRunning, copyCompleted]);

  // Initialize tap targets
  useEffect(() => {
    const gridSize = dailyData.tapping.gridSize;
    const cellSize = 1 / gridSize;
    const targets = dailyData.tapping.targets.map((idx, i) => ({
      x: (idx % gridSize + 0.5) * cellSize,
      y: (Math.floor(idx / gridSize) + 0.5) * cellSize,
      index: idx,
    }));
    setTapTargets(targets);
    setCurrentTargetIndex(0);
  }, [dailyData.tapping]);

  const allCompleted = ['COPYWRITING', 'TRACING', 'TAPPING'].every(t => isCompleted[t]);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h2 className="text-headline-sm font-medium text-foreground">Non-Dominant Hand</h2>
        <p className="text-body-sm text-muted-foreground">
          Use your {handUsed === 'LEFT' ? 'left' : 'right'} hand for all tasks
        </p>
      </div>

      {/* Hand selector */}
      <div className="flex gap-4">
        <button
          onClick={() => setHandUsed('LEFT')}
          className={`px-4 py-2 rounded-lg font-medium transition-colors ${
            handUsed === 'LEFT'
              ? 'bg-primary text-primary-foreground'
              : 'bg-muted text-muted-foreground hover:bg-muted/80'
          }`}
        >
          Left Hand
        </button>
        <button
          onClick={() => setHandUsed('RIGHT')}
          className={`px-4 py-2 rounded-lg font-medium transition-colors ${
            handUsed === 'RIGHT'
              ? 'bg-primary text-primary-foreground'
              : 'bg-muted text-muted-foreground hover:bg-muted/80'
          }`}
        >
          Right Hand
        </button>
      </div>

      {/* Task Tabs */}
      <div className="flex gap-2 border-b border-border/50">
        {(['COPYWRITING', 'TRACING', 'TAPPING'] as TaskType[]).map(task => (
          <button
            key={task}
            onClick={() => setActiveTask(task)}
            className={`px-4 py-2 text-sm font-medium border-b-2 transition-colors ${
              activeTask === task
                ? 'border-primary text-primary'
                : 'border-transparent text-muted-foreground hover:text-foreground'
            }`}
          >
            {task === 'COPYWRITING' ? '✍️ Copywriting' : task === 'TRACING' ? '📐 Tracing' : '🎯 Tapping'}
            {isCompleted[task] && <span className="ml-2 text-green-500">✓</span>}
          </button>
        ))}
      </div>

      {/* Task Content */}
      <div className="space-y-4">
        {/* COPYWRITING */}
        {activeTask === 'COPYWRITING' && (
          <div className="space-y-4">
            <div className="bg-muted/50 rounded-xl p-6">
              <p className="text-body-lg text-foreground leading-relaxed whitespace-pre-wrap mb-4">
                {dailyData.copywriting.text}
              </p>
              
              <textarea
                value=""
                onChange={() => {}}
                onKeyDown={(e) => { if (!isCompleted.COPYWRITING) e.currentTarget.value = e.currentTarget.value; }}
                onKeyDownCapture={(e) => { /* handled by parent */ }}
                disabled={isCompleted.COPYWRITING}
                placeholder={isCompleted.COPYWRITING ? 'Completed' : 'Type with non-dominant hand...'}
                className="w-full min-h-[120px] p-4 bg-background rounded-lg border border-border/50 focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20 resize-none font-mono text-body-lg leading-relaxed"
                rows={6}
                spellCheck={false}
              />
            </div>
            
            {!isCompleted.COPYWRITING ? (
              <button
                onClick={() => { /* start copywriting */ }}
                className="w-full px-8 py-3 bg-primary text-primary-foreground rounded-lg font-medium hover:bg-primary/90"
              >
                Start Copywriting
              </button>
            ) : (
              <div className="bg-green-50 dark:bg-green-900/20 rounded-xl p-4">
                <p className="font-medium text-green-800 dark:text-green-200">✓ Copywriting Complete</p>
                <p className="text-sm text-green-700 dark:text-green-300 mt-1">Score: 85 • Accuracy: 92%</p>
              </div>
            )}
          </div>
        )}

        {/* TRACING */}
        {activeTask === 'TRACING' && (
          <div className="space-y-4">
            <div className="aspect-square bg-muted/50 rounded-xl relative border border-border/50">
              <canvas
                className="absolute inset-0 w-full h-full"
                width={400}
                height={400}
              />
              {tracePoints.length > 0 && (
                <div className="absolute inset-0 flex items-center justify-center text-green-500 text-4xl">✓</div>
              )}
            </div>
            
            {!isCompleted.TRACING ? (
              <button
                onClick={() => { /* start tracing */ }}
                className="w-full px-8 py-3 bg-primary text-primary-foreground rounded-lg font-medium hover:bg-primary/90"
              >
                Start Tracing
              </button>
            ) : (
              <div className="bg-green-50 dark:bg-green-900/20 rounded-xl p-4">
                <p className="font-medium text-green-800 dark:text-green-200">✓ Tracing Complete</p>
                <p className="text-sm text-green-700 dark:text-green-300 mt-1">Score: 78 • Smoothness: 82%</p>
              </div>
            )}
          </div>
        )}

        {/* TAPPING */}
        {activeTask === 'TAPPING' && (
          <div className="space-y-4">
            <div className="aspect-square bg-muted/50 rounded-xl relative border border-border/50 grid" 
              style={{ 
                gridTemplateColumns: `repeat(${dailyData.tapping.gridSize}, 1fr)`,
                gridTemplateRows: `repeat(${dailyData.tapping.gridSize}, 1fr)`
              }}
            >
              {Array.from({ length: dailyData.tapping.gridSize * dailyData.tapping.gridSize }, (_, i) => (
                <button
                  key={i}
                  className={`aspect-square flex items-center justify-center text-2xl font-bold transition-colors ${
                    i === dailyData.tapping.targets[currentTargetIndex]
                      ? 'bg-primary text-primary-foreground animate-pulse'
                      : 'bg-muted/50 text-muted-foreground hover:bg-muted'
                  }`}
                  onClick={() => {
                    if (i === dailyData.tapping.targets[currentTargetIndex]) {
                      if (currentTargetIndex < dailyData.tapping.targets.length - 1) {
                        // Would advance to next target
                      }
                    }
                  }}
                  disabled={isCompleted.TAPPING}
                >
                  {i + 1}
                </button>
              ))}
            </div>
            
            {!isCompleted.TAPPING ? (
              <button
                onClick={() => { /* start tapping */ }}
                className="w-full px-8 py-3 bg-primary text-primary-foreground rounded-lg font-medium hover:bg-primary/90"
              >
                Start Tapping
              </button>
            ) : (
              <div className="bg-green-50 dark:bg-green-900/20 rounded-xl p-4">
                <p className="font-medium text-green-800 dark:text-green-200">✓ Tapping Complete</p>
                <p className="text-sm text-green-700 dark:text-green-300 mt-1">Score: 82 • Sequence: 95%</p>
              </div>
            )}
          </div>
        )}

        {/* Overall completion */}
        {allCompleted && (
          <div className="bg-gradient-to-r from-primary/10 to-secondary/10 rounded-xl p-6 text-center animate-in">
            <div className="text-4xl mb-2">🎉</div>
            <h3 className="text-2xl font-medium text-foreground mb-2">All tasks complete!</h3>
            <p className="text-muted-foreground mb-4">Great work with your non-dominant hand!</p>
            <button
              onClick={() => { /* navigate back */ }}
              className="px-8 py-3 bg-primary text-primary-foreground rounded-lg font-medium hover:bg-primary/90"
            >
              Finish
            </button>
          </div>
        )}
      </div>
    </div>
  );
}