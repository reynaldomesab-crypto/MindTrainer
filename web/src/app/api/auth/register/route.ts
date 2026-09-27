import { NextRequest, NextResponse } from 'next/server';
import api from '@/lib/api/client';

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const response = await api.post('/auth/register', body);
    return NextResponse.json(response.data);
  } catch (error: any) {
    if (error.response?.status === 409) {
      return NextResponse.json({ error: 'Email or username already exists' }, { status: 409 });
    }
    if (error.response?.status === 400) {
      return NextResponse.json({ error: error.response.data?.message || 'Validation error' }, { status: 400 });
    }
    return NextResponse.json({ error: 'Registration failed' }, { status: 500 });
  }
}