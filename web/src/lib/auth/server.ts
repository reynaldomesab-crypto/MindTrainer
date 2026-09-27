import { getServerSession } from 'next-auth';
import { authOptions } from './auth';

export async function getServerSessionUser() {
  const session = await getServerSession(authOptions);
  return session?.user || null;
}

export async function getServerSession() {
  return getServerSession(authOptions);
}