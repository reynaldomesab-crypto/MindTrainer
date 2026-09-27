'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useSession } from 'next-auth/react';
import { StroopGame } from '@/components/exercises/StroopGame';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Zap, Brain, RotateCcw } from 'lucide-react';

interface StroopPageProps {
  dailyData: any;
  loading: boolean;
}

function StroopContent({ dailyData, loading }: StroopPageProps) {
  const handleComplete = async (result: any) => {
    try {
      await fetch('/api/exercises/stroop/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(result),
      });
    } catch (error) {
      console.error('Failed to submit:', error);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-pulse-soft text-muted-foreground">Loading today's challenge...</div>
      </div>
    );
  }

  if (!dailyData) {
    return (
      <div className="text-center py-12">
        <div className="text-4xl mb-4">📅</div>
        <h3 className="text-xl font-medium mb-2">Already completed today!</h3>
        <p className="text-muted-foreground">Come back tomorrow for a new challenge.</p>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-medium text-foreground">Stroop Challenge</h1>
        <p className="text-muted-foreground">Name the ink color, ignore the word. Multiple modes.</p>
      </div>

      <div className="grid grid-cols-3 gap-4">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-primary/10 rounded-lg">
                <Zap className="h-5 w-5 text-primary" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Mode</p>
                <p className="text-xl font-medium capitalize">{dailyData.mode.toLowerCase()}</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-purple-100 dark:bg-purple-900/30 rounded-lg">
                <Brain className="h-5 w-5 text-purple-600 dark:text-purple-400" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Colors</p>
                <p className="text-xl font-medium">{dailyData.config.colorCount}</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-amber-100 dark:bg-amber-900/30 rounded-lg">
                <RotateCcw className="h-5 w-5 text-amber-600 dark:text-amber-400" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Trials</p>
                <p className="text-xl font-medium">{dailyData.config.trialCount || 60}</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <StroopGame dailyData={dailyData} onComplete={async (result) => {
        try {
          await fetch('/api/exercises/stroop/submit', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(result),
          });
        } catch (error) {
          console.error('Failed to submit:', error);
        }
      }} />
    </div>
  );
}

export default function StroopPage() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const [dailyData, setDailyData] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (status === 'unauthenticated') {
      router.push('/auth/login');
      return;
    }
    if (status === 'authenticated') {
      loadDaily();
    }
  }, [status, router]);

  const loadDaily = async () => {
    try {
      const response = await fetch('/api/exercises/stroop/daily?mode=CLASSIC&lang=en');
      if (response.ok) {
        const data = await response.json();
        setDailyData(data);
      } else if (response.status === 403) {
        setDailyData(null);
      }
    } catch (error) {
      console.error('Failed to load daily:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <StroopContent dailyData={dailyData} loading={loading} />
    </div>
  );
}