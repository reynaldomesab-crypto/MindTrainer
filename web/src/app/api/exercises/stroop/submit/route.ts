import { NextRequest, NextResponse } from 'next/server';
import { getServerSession } from '@/lib/auth/server';
import api from '@/lib/api/client';

export async function POST(request: NextRequest) {
  const session = await getServerSession();
  if (!session) {
    return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
  }

  try {
    const body = await request.json();
    const response = await api.post('/exercises/stroop/submit', body, {
      headers: { Authorization: `Bearer ${session.accessToken}` },
    });
    return NextResponse.json(response.data);
  } catch (error: any) {
    if (error.response?.status === 403) {
      return NextResponse.json({ error: 'Already completed today' }, { status: 403 });
    }
    return NextResponse.json({ error: 'Failed to submit' }, { status: 500 });
  }
}