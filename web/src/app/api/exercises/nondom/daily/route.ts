import { NextRequest, NextResponse } from 'next/server';
import { getServerSession } from '@/lib/auth/server';
import api from '@/lib/api/client';

export async function GET(request: NextRequest) {
  const session = await getServerSession();
  if (!session) {
    return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
  }

  const { searchParams } = new URL(request.url);
  const lang = searchParams.get('lang') || 'en';

  try {
    const response = await api.get('/exercises/nondom/daily', {
      params: { lang },
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