import React, { createContext, useContext, useState, useEffect } from 'react';
import type { LoginResponse } from '../types';
import { authApi } from '../api/authApi';

interface AuthContextType {
  user: LoginResponse | null;
  token: string | null;
  tenantId: string;
  login: (credentials: { username: string; password: string; tenantId?: string }) => Promise<void>;
  logout: () => void;
  setTenantId: (tenant: string) => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('gad_crm_token'));
  const [tenantId, setTenantState] = useState<string>(() => localStorage.getItem('gad_crm_tenant') || 'gad-central');
  const [user, setUser] = useState<LoginResponse | null>(() => {
    const saved = localStorage.getItem('gad_crm_user');
    return saved ? JSON.parse(saved) : null;
  });

  const setTenantId = (newTenant: string) => {
    setTenantState(newTenant);
    localStorage.setItem('gad_crm_tenant', newTenant);
  };

  const login = async (credentials: { username: string; password: string; tenantId?: string }) => {
    const data = await authApi.login({ ...credentials, tenantId: credentials.tenantId || tenantId });
    setToken(data.token);
    setUser(data);
    setTenantId(data.tenantId);
    localStorage.setItem('gad_crm_token', data.token);
    localStorage.setItem('gad_crm_user', JSON.stringify(data));
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('gad_crm_token');
    localStorage.removeItem('gad_crm_user');
  };

  useEffect(() => {
    const handleUnauthorized = () => logout();
    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        tenantId,
        login,
        logout,
        setTenantId,
        isAuthenticated: !!token && !!user,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe ser utilizado dentro de un AuthProvider');
  }
  return context;
};
