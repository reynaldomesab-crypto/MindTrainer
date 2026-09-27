import { redirect } from 'next/navigation';
import { getServerSession } from '@/lib/auth/server';
import { DashboardClient } from './DashboardClient';

export default async function DashboardPage() {
  const session = await getServerSession();
  
  if (!session) {
    redirect('/auth/login');
  }

  return <DashboardClient />;
}