import NextAuth, { NextAuthOptions } from 'next-auth';
import CredentialsProvider from 'next-auth/providers/credentials';
import GoogleProvider from 'next-auth/providers/google';
import GitHubProvider from 'next-auth/providers/github';
import { JWT } from 'next-auth/jwt';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

async function refreshAccessToken(token: JWT): Promise<JWT> {
  try {
    const response = await fetch(`${API_URL}/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken: token.refreshToken }),
    });

    if (!response.ok) throw new Error('Failed to refresh token');

    const data = await response.json();
    return {
      ...token,
      accessToken: data.accessToken,
      refreshToken: data.refreshToken,
      accessTokenExpires: Date.now() + data.expiresIn,
    };
  } catch (error) {
    return { ...token, error: 'RefreshAccessTokenError' };
  }
}

export const authOptions: NextAuthOptions = {
  providers: [
    CredentialsProvider({
      name: 'credentials',
      credentials: {
        email: { label: 'Email', type: 'email' },
        password: { label: 'Password', type: 'password' },
      },
      async authorize(credentials) {
        if (!credentials?.email || !credentials?.password) return null;

        try {
          const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              email: credentials.email,
              password: credentials.password,
            }),
          });

          if (!response.ok) return null;

          const data = await response.json();
          
          return {
            id: data.user.id,
            email: data.user.email,
            name: data.user.username,
            image: data.user.avatarUrl,
            accessToken: data.accessToken,
            refreshToken: data.refreshToken,
          };
        } catch {
          return null;
        }
      },
    }),
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID!,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET!,
      authorization: {
        params: { scope: 'openid email profile' },
      },
    }),
    GitHubProvider({
      clientId: process.env.GITHUB_CLIENT_ID!,
      clientSecret: process.env.GITHUB_CLIENT_SECRET!,
    }),
  ],
  callbacks: {
    async jwt({ token, user, account }) {
      if (user) {
        token.accessToken = user.accessToken;
        token.refreshToken = user.refreshToken;
        token.accessTokenExpires = Date.now() + 15 * 60 * 1000; // 15 minutes
      }

      if (Date.now() < (token.accessTokenExpires as number)) {
        return token;
      }

      return refreshAccessToken(token);
    },
    async session({ session, token }) {
      session.accessToken = token.accessToken as string;
      session.refreshToken = token.refreshToken as string;
      session.user = {
        ...session.user,
        id: token.sub,
      };
      return session;
    },
    async signIn({ user, account }) {
      if (account?.provider === 'google' || account?.provider === 'github') {
        try {
          const response = await fetch(`${API_URL}/auth/oauth/${account.provider}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ token: account.access_token }),
          });

          if (response.ok) {
            const data = await response.json();
            user.accessToken = data.accessToken;
            user.refreshToken = data.refreshToken;
            user.id = data.user.id;
            return true;
          }
        } catch {
          return false;
        }
      }
      return true;
    },
  },
  pages: {
    signIn: '/auth/login',
    error: '/auth/login',
  },
  session: {
    strategy: 'jwt',
    maxAge: 7 * 24 * 60 * 60, // 7 days
  },
  secret: process.env.NEXTAUTH_SECRET,
};

const handler = NextAuth(authOptions);
export { handler as GET, handler as POST };