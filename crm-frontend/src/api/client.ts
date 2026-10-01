import axios from 'axios';

const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('gad_crm_token');
    const tenantId = localStorage.getItem('gad_crm_tenant') || 'gad-central';

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    config.headers['X-Tenant-ID'] = tenantId;

    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('gad_crm_token');
      localStorage.removeItem('gad_crm_user');
      window.dispatchEvent(new Event('auth:unauthorized'));
    }
    return Promise.reject(error);
  }
);

export default api;
