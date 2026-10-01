import api from './client';
import type {
  CrearTramiteRequest,
  DashboardMetricas,
  DerivarTramiteRequest,
  EstadoTramite,
  ResolverTramiteRequest,
  Tramite,
} from '../types';

export const ticketApi = {
  getAll: async (params?: { estado?: EstadoTramite; departamento?: string }): Promise<Tramite[]> => {
    const response = await api.get<Tramite[]>('/tramites', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Tramite> => {
    const response = await api.get<Tramite>(`/tramites/${id}`);
    return response.data;
  },

  getByCode: async (code: string): Promise<Tramite> => {
    const response = await api.get<Tramite>(`/tramites/codigo/${code}`);
    return response.data;
  },

  create: async (data: CrearTramiteRequest): Promise<Tramite> => {
    const response = await api.post<Tramite>('/tramites', data);
    return response.data;
  },

  derivar: async (id: number, data: DerivarTramiteRequest): Promise<Tramite> => {
    const response = await api.patch<Tramite>(`/tramites/${id}/derivar`, data);
    return response.data;
  },

  resolver: async (id: number, data: ResolverTramiteRequest): Promise<Tramite> => {
    const response = await api.patch<Tramite>(`/tramites/${id}/resolver`, data);
    return response.data;
  },

  getMetrics: async (): Promise<DashboardMetricas> => {
    const response = await api.get<DashboardMetricas>('/tramites/dashboard/metricas');
    return response.data;
  },
};
