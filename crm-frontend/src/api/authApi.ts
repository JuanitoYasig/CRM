import api from './client';
import type { LoginResponse } from '../types';

export const authApi = {
  login: async (credentials: { username: string; password: string; tenantId?: string }): Promise<LoginResponse> => {
    const response = await api.post<LoginResponse>('/auth/login', credentials);
    return response.data;
  },

  getProfile: async () => {
    const response = await api.get('/auth/me');
    return response.data;
  },
};
