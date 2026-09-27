import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { getSession, signOut } from 'next-auth/react';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Request interceptor to add auth token
api.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    const session = await getSession();
    if (session?.accessToken) {
      config.headers.Authorization = `Bearer ${session.accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const session = await getSession();
        if (session?.refreshToken) {
          const response = await axios.post(
            `${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1'}/auth/refresh`,
            { refreshToken: session.refreshToken }
          );

          const { accessToken, refreshToken } = response.data;
          
          // Update session (this would need to be handled by next-auth callbacks)
          // For now, we'll just retry with new token
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return api(originalRequest);
        }
      } catch (refreshError) {
        await signOut({ callbackUrl: '/auth/login' });
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;