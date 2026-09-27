'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useSession } from 'next-auth/react';
import { NonDomGame } from '@/components/exercises/NonDomGame';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Edit3, MousePointer, TouchApp } from 'lucide-react';

interface NonDomPageProps {
  dailyData: any;
  loading: boolean;
}

function NonDomContent({ dailyData, loading }: NonDomPageProps) {
  const handleComplete = async (result: any) => {
    try {
      await fetch('/api/exercises/nondom/submit', {
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
    <div className="max-w-3xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-medium text-foreground">Non-Dominant Hand</h1>
        <p className="text-muted-foreground">Write, trace, and tap with your non-dominant hand.</p>
      </div>

      <NonDomGame dailyData={dailyData} onComplete={async (result) => {
        try {
          await fetch('/api/exercises/nondom/submit', {
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

export default function NonDomPage() {
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
      const response = await fetch('/api/exercises/nondom/daily?lang=en');
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
      <NonDomContent dailyData={dailyData} loading={loading} />
    </div>
  );
}