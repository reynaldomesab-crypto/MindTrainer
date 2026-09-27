'use client';

import { ProgressDashboard } from '@/types/progress';
import { cn, getTrendColor, getTrendIcon } from '@/lib/utils';
import { TrendingUp, TrendingDown, Minus, Target, Zap, Brain, HeartPulse } from 'lucide-react';

interface ProgressSummaryProps {
  progress: ProgressDashboard;
}

export function ProgressSummary({ progress }: ProgressSummaryProps) {
  const metrics = [
    { label: 'Cognitive Age', value: progress.globalMetrics.cognitiveAge, unit: 'yrs', icon: Brain, color: 'text-primary' },
    { label: 'Consistency', value: `${Math.round(progress.globalMetrics.consistencyScore * 100)}%`, unit: '', icon: Target, color: 'text-blue-600' },
    { label: 'Focus Index', value: progress.globalMetrics.focusIndex.toFixed(2), unit: '', icon: Zap, color: 'text-amber-600' },
    { label: 'Motor Symmetry', value: `${Math.round(progress.globalMetrics.motorSymmetry * 100)}%`, unit: '', icon: HeartPulse, color: 'text-green-600' },
  ];

  return (
    <section className="space-y-6 animate-in">
      {/* Global Metrics */}
      <div>
        <h2 className="text-title-lg font-medium text-foreground mb-4">Your Metrics</h2>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {metrics.map((metric, index) => (
            <div
              key={metric.label}
              className={cn(
                'p-5 rounded-xl border bg-card transition-all duration-200',
                'hover:shadow-card-hover'
              )}
            >
              <div className="flex items-center justify-between mb-2">
                <metric.icon className={`h-5 w-5 ${metric.color}`} />
                <span className={`text-xs font-medium ${getTrendColor(progress.exerciseProgress[Object.keys(progress.exerciseProgress)[index % 4]]?.trend || 'STABLE')}`}>
                  {getTrendIcon(progress.exerciseProgress[Object.keys(progress.exerciseProgress)[index % 4]]?.trend || 'STABLE')}
                </span>
              </div>
              <div className="text-3xl font-bold text-foreground">{metric.value}</div>
              <div className="text-xs text-muted-foreground">{metric.label}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Streak */}
      <div className="p-5 rounded-xl border bg-gradient-to-r from-primary/10 to-secondary/10">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-xs text-muted-foreground mb-1">Current Streak</p>
            <p className="text-4xl font-bold text-foreground">{progress.streaks.current} days</p>
          </div>
          <div className="text-right">
            <p className="text-xs text-muted-foreground mb-1">Longest Streak</p>
            <p className="text-2xl font-medium text-primary">{progress.streaks.longest} days</p>
          </div>
        </div>
        {progress.streaks.streakFreezeUsed && (
          <p className="mt-2 text-xs text-amber-600 dark:text-amber-400">Streak freeze used this week</p>
        )}
      </div>

      {/* Exercise Progress Overview */}
      <div>
        <h2 className="text-title-lg font-medium text-foreground mb-4">Exercise Progress</h2>
        <div className="grid gap-4 sm:grid-cols-2">
          {Object.entries(progress.exerciseProgress).map(([key, exercise]) => (
            <div key={key} className="p-5 rounded-xl border bg-card">
              <div className="flex items-start justify-between mb-3">
                <div>
                  <h3 className="font-medium text-title-md capitalize">{key.replace('nondominant', 'Non-Dominant')}</h3>
                  <p className="text-xs text-muted-foreground">Level {exercise.currentLevel.toFixed(1)}</p>
                </div>
                <span className={cn('px-2 py-1 rounded-full text-xs font-medium', getTrendColor(exercise.trend))}>
                  {getTrendIcon(exercise.trend)} {exercise.trend}
                </span>
              </div>
              <div className="h-2 bg-muted rounded-full overflow-hidden">
                <div
                  className="h-full bg-primary rounded-full transition-all duration-500"
                  style={{ width: `${Math.min(exercise.currentLevel * 25, 100)}%` }}
                />
              </div>
              <p className="mt-2 text-xs text-muted-foreground">
                {exercise.sessionsCompleted} sessions • Last: {exercise.lastSession ? formatRelativeTime(exercise.lastSession) : 'Never'}
              </p>
            </div>
          ))}
        </div>
      </div>

      {/* Insights */}
      {progress.insights.length > 0 && (
        <div>
          <h2 className="text-title-lg font-medium text-foreground mb-4">Insights</h2>
          <div className="space-y-3">
            {progress.insights.slice(0, 3).map((insight) => (
              <div
                key={insight.id}
                className="p-4 rounded-xl border bg-card/50"
              >
                <h4 className="font-medium text-body-md mb-1">{insight.title}</h4>
                <p className="text-sm text-muted-foreground">{insight.description}</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </section>
  );
}

function formatRelativeTime(date: Date): string {
  const d = new Date(date);
  const now = new Date();
  const diffMs = now.getTime() - d.getTime();
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  
  if (diffDays === 0) return 'Today';
  if (diffDays === 1) return 'Yesterday';
  if (diffDays < 7) return `${diffDays}d ago`;
  if (diffDays < 30) return `${Math.floor(diffDays / 7)}w ago`;
  return `${Math.floor(diffDays / 30)}mo ago`;
}