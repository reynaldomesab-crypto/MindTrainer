'use client';

import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { DashboardHeader } from '@/components/layout/DashboardHeader';
import { ExerciseCard } from '@/components/exercises/ExerciseCard';
import { ProgressSummary } from '@/components/charts/ProgressSummary';
import { useProgress } from '@/hooks/useProgress';
import { ExerciseType } from '@/types/exercise';

export function DashboardClient() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const { data: progress, isLoading } = useProgress();
  const [completedToday, setCompletedToday] = useState<Set<string>>(new Set());

  useEffect(() => {
    if (status === 'unauthenticated') {
      router.push('/auth/login');
    }
  }, [status, router]);

  useEffect(() => {
    if (progress?.todayStatus) {
      const completed = new Set(
        progress.todayStatus
          .filter((s) => s.completed)
          .map((s) => s.exerciseType.toLowerCase())
      );
      setCompletedToday(completed);
    }
  }, [progress]);

  if (status === 'loading' || isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-pulse-soft">Loading...</div>
      </div>
    );
  }

  const exercises: ExerciseType[] = [
    { id: 'schulte', name: 'Schulte Tables', description: 'Find numbers 1-25 in order. 30 seconds.', icon: '🔢', color: 'primary' },
    { id: 'blindfold', name: 'Blindfold Writing', description: 'Type with eyes closed. Focus on rhythm.', icon: '⌨️', color: 'secondary' },
    { id: 'nondominant', name: 'Non-Dominant Hand', description: 'Write, trace, tap with other hand.', icon: '✍️', color: 'accent' },
    { id: 'stroop', name: 'Stroop Challenge', description: 'Name the ink color, ignore the word.', icon: '🎨', color: 'destructive' },
  ];

  const handleExerciseClick = (exerciseId: string) => {
    router.push(`/${exerciseId}`);
  };

  const allCompleted = exercises.every((e) => completedToday.has(e.id));

  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader user={session?.user} />
      
      <main className="max-w-4xl mx-auto px-4 py-8">
        {/* Welcome Section */}
        <section className="mb-8 animate-in">
          <h1 className="text-display-sm font-medium text-foreground mb-2">
            Good {new Date().getHours() < 12 ? 'morning' : new Date().getHours() < 18 ? 'afternoon' : 'evening'}!
          </h1>
          <p className="text-body-lg text-muted-foreground">
            Your daily brain practice is ready. One exercise of each type, no pressure.
          </p>
        </section>

        {/* Exercise Grid */}
        <section className="mb-8">
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {exercises.map((exercise) => (
              <ExerciseCard
                key={exercise.id}
                exercise={exercise}
                completed={completedToday.has(exercise.id)}
                onClick={() => handleExerciseClick(exercise.id)}
                disabled={completedToday.has(exercise.id)}
              />
            ))}
          </div>

          {allCompleted && (
            <div className="mt-6 p-6 text-center bg-green-50 dark:bg-green-900/20 rounded-xl animate-in">
              <div className="text-4xl mb-2">🎉</div>
              <h2 className="text-headline-sm font-medium text-green-800 dark:text-green-200 mb-1">
                All done for today!
              </h2>
              <p className="text-body-md text-green-700 dark:text-green-300">
                Rest is productive too. See you tomorrow.
              </p>
            </div>
          )}
        </section>

        {/* Progress Summary */}
        {progress && <ProgressSummary progress={progress} />}
      </main>
    </div>
  );
}