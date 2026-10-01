import api from './client';
import type { Contribuyente, RegistrarContribuyenteRequest } from '../types';

export const citizenApi = {
  getAll: async (): Promise<Contribuyente[]> => {
    const response = await api.get<Contribuyente[]>('/contribuyentes');
    return response.data;
  },

  getById: async (id: number): Promise<Contribuyente> => {
    const response = await api.get<Contribuyente>(`/contribuyentes/${id}`);
    return response.data;
  },

  getByDocument: async (doc: string): Promise<Contribuyente> => {
    const response = await api.get<Contribuyente>(`/contribuyentes/identificacion/${doc}`);
    return response.data;
  },

  create: async (data: RegistrarContribuyenteRequest): Promise<Contribuyente> => {
    const response = await api.post<Contribuyente>('/contribuyentes', data);
    return response.data;
  },
};
