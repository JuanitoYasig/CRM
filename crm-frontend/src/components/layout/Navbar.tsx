import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { Building2, LogOut, ShieldCheck, User } from 'lucide-react';

export const Navbar: React.FC = () => {
  const { user, tenantId, setTenantId, logout } = useAuth();

  const tenants = [
    { id: 'gad-central', label: '🏛️ GAD Central' },
    { id: 'gad-milagro', label: '🌴 GAD Milagro' },
    { id: 'gad-loja', label: '🌻 GAD Loja' },
    { id: 'gad-cuenca', label: '🏛️ GAD Cuenca' },
  ];

  return (
    <header className="sticky top-0 z-40 bg-white border-b border-slate-200/80 shadow-xs">
      <div className="flex items-center justify-between px-6 py-3">
        {/* Identidad Institucional */}
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-blue-700 to-indigo-600 flex items-center justify-center text-white shadow-sm">
            <Building2 className="w-5 h-5" />
          </div>
          <div>
            <h1 className="text-base font-bold tracking-tight text-slate-800 leading-tight">
              CRM GAD Municipal
            </h1>
            <p className="text-xs text-slate-400">Atención Ciudadana & Gestión de Trámites</p>
          </div>
        </div>

        {/* Controles Multi-Tenant & Perfil */}
        <div className="flex items-center space-x-4">
          {/* Selector de Tenant Dinámico */}
          <div className="flex items-center space-x-2 bg-slate-50 border border-slate-200 rounded-lg px-3 py-1.5">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Jurisdicción:</span>
            <select
              value={tenantId}
              onChange={(e) => setTenantId(e.target.value)}
              className="bg-transparent text-xs font-bold text-slate-700 focus:outline-none cursor-pointer"
            >
              {tenants.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.label}
                </option>
              ))}
            </select>
          </div>

          {/* Información del Funcionario */}
          {user && (
            <div className="flex items-center space-x-3 pl-3 border-l border-slate-200">
              <div className="text-right hidden sm:block">
                <p className="text-xs font-bold text-slate-700">{user.nombreCompleto}</p>
                <div className="flex items-center justify-end space-x-1 mt-0.5">
                  <ShieldCheck className="w-3 h-3 text-blue-600" />
                  <span className="text-[11px] font-semibold text-blue-600">{user.rolDescripcion}</span>
                </div>
              </div>

              <div className="w-9 h-9 rounded-full bg-slate-100 border border-slate-200 flex items-center justify-center text-slate-600">
                <User className="w-4 h-4" />
              </div>

              <button
                onClick={logout}
                title="Cerrar sesión"
                className="p-2 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition-colors"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
