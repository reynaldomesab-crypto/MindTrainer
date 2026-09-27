'use client';

import { useEffect, useState } from 'react';
import { useSession } from 'next-auth/react';
import { ProgressSummary } from '@/components/charts/ProgressSummary';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Calendar, Target, Zap, Brain, HeartPulse, TrendingUp, TrendingDown, Minus } from 'lucide-react';
import { ProgressDashboard } from '@/types/progress';
import { formatRelativeTime } from '@/lib/utils';

interface ProgressPageProps {
  progress: ProgressDashboard | null;
  loading: boolean;
}

function ProgressContent({ progress, loading }: ProgressPageProps) {
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-pulse-soft text-muted-foreground">Loading your progress...</div>
      </div>
    );
  }

  if (!progress) {
    return (
      <div className="text-center py-12">
        <div className="text-4xl mb-4">📊</div>
        <h3 className="text-xl font-medium mb-2">No progress data yet</h3>
        <p className="text-muted-foreground">Complete some exercises to see your progress!</p>
      </div>
    );
  }

  const metrics = [
    { 
      label: 'Cognitive Age', 
      value: progress.globalMetrics.cognitiveAge, 
      unit: 'yrs', 
      icon: Brain, 
      color: 'text-primary' 
    },
    { 
      label: 'Consistency', 
      value: `${Math.round(progress.globalMetrics.consistencyScore * 100)}%`, 
      unit: '', 
      icon: Target, 
      color: 'text-blue-600' 
    },
    { 
      label: 'Focus Index', 
      value: progress.globalMetrics.focusIndex.toFixed(2), 
      unit: '', 
      icon: Zap, 
      color: 'text-amber-600' 
    },
    { 
      label: 'Motor Symmetry', 
      value: `${Math.round(progress.globalMetrics.motorSymmetry * 100)}%`, 
      unit: '', 
      icon: HeartPulse, 
      color: 'text-green-600' 
    },
  ];

  const getTrendColor = (trend: string) => {
    switch (trend) {
      case 'IMPROVING': return 'text-green-600 dark:text-green-400';
      case 'STABLE': return 'text-blue-600 dark:text-blue-400';
      case 'DECLINING': return 'text-amber-600 dark:text-amber-400';
      default: return 'text-muted-foreground';
    }
  };

  const getTrendIcon = (trend: string) => {
    switch (trend) {
      case 'IMPROVING': return <TrendingUp className="h-4 w-4" />;
      case 'STABLE': return <Minus className="h-4 w-4" />;
      case 'DECLINING': return <TrendingDown className="h-4 w-4" />;
      default: return null;
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-medium text-foreground">Your Progress</h1>
          <p className="text-muted-foreground">Track your cognitive training journey</p>
        </div>
        <div className="flex items-center gap-2 text-sm text-muted-foreground">
          <Calendar className="h-4 w-4 mr-1" />
          <span>Weekly active: {progress.globalMetrics.weeklyActiveDays}/7 days</span>
        </div>
      </div>

      {/* Global Metrics */}
      <div>
        <h2 className="text-title-lg font-medium text-foreground mb-4">Your Metrics</h2>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {metrics.map((metric, index) => (
            <Card key={metric.label}>
              <CardContent className="pt-6">
                <div className="flex items-center justify-between mb-2">
                  <metric.icon className={`h-5 w-5 ${metric.color}`} />
                  <span className={getTrendColor(
                    progress.exerciseProgress[Object.keys(progress.exerciseProgress)[index % 4]]?.trend || 'STABLE'
                  )}>
                    {getTrendIcon(
                      progress.exerciseProgress[Object.keys(progress.exerciseProgress)[index % 4]]?.trend || 'STABLE'
                    )}
                  </span>
                </div>
                <div className="text-3xl font-bold text-foreground">{metric.value}</div>
                <div className="text-xs text-muted-foreground">{metric.label}</div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>

      {/* Streak */}
      <Card>
        <CardContent className="pt-6">
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
        </CardContent>
      </Card>

      {/* Exercise Progress Overview */}
      <div>
        <h2 className="text-title-lg font-medium text-foreground mb-4">Exercise Progress</h2>
        <div className="grid gap-4 sm:grid-cols-2">
          {Object.entries(progress.exerciseProgress).map(([key, exercise]) => (
            <Card key={key}>
              <CardContent className="pt-6">
                <div className="flex items-start justify-between mb-3">
                  <div>
                    <h3 className="font-medium text-title-md capitalize">
                      {key.replace('nondominant', 'Non-Dominant')}
                    </h3>
                    <p className="text-xs text-muted-foreground">Level {exercise.currentLevel.toFixed(1)}</p>
                  </div>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${getTrendColor(exercise.trend)}`}>
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
              </CardContent>
            </Card>
          ))}
        </div>
      </div>

      {/* Insights */}
      {progress.insights.length > 0 && (
        <div>
          <h2 className="text-title-lg font-medium text-foreground mb-4">Insights</h2>
          <div className="space-y-3">
            {progress.insights.slice(0, 5).map((insight) => (
              <Card key={insight.id}>
                <CardContent className="pt-6">
                  <h4 className="font-medium text-body-md mb-1">{insight.title}</h4>
                  <p className="text-sm text-muted-foreground">{insight.description}</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default function ProgressPage() {
  const { data: session, status } = useSession();
  const [progress, setProgress] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (status === 'unauthenticated') {
      // Would redirect in real app
      return;
    }
    if (status === 'authenticated') {
      loadProgress();
    }
  }, [status]);

  const loadProgress = async () => {
    try {
      const response = await fetch('/api/progress/dashboard');
      if (response.ok) {
        const data = await response.json();
        setProgress(data);
      }
    } catch (error) {
      console.error('Failed to load progress:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <ProgressContent progress={progress} loading={loading} />
    </div>
  );
}