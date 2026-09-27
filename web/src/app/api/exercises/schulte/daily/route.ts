import { NextRequest, NextResponse } from 'next/server';
import { getServerSession } from '@/lib/auth/server';
import api from '@/lib/api/client';

export async function GET(request: NextRequest) {
  const session = await getServerSession();
  if (!session) {
    return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
  }

  const { searchParams } = new URL(request.url);
  const size = searchParams.get('size') || '5';

  try {
    const response = await api.get('/exercises/schulte/daily', {
      params: { size: parseInt(size) },
      headers: { Authorization: `Bearer ${session.accessToken}` },
    });
    return NextResponse.json(response.data);
  } catch (error: any) {
    if (error.response?.status === 403) {
      return NextResponse.json({ error: 'Already completed today' }, { status: 403 });
    }
    return NextResponse.json({ error: 'Failed to fetch daily' }, { status: 500 });
  }
}