import React from 'react';
import { LayoutDashboard, FileText, Users } from 'lucide-react';

interface SidebarProps {
  currentTab: string;
  onSelectTab: (tab: string) => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ currentTab, onSelectTab }) => {
  const menuItems = [
    { id: 'dashboard', label: 'Panel & Métricas', icon: LayoutDashboard },
    { id: 'tickets', label: 'Bandeja de Trámites', icon: FileText },
    { id: 'citizens', label: 'Padrón de Ciudadanos', icon: Users },
  ];

  return (
    <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col shrink-0 min-h-[calc(100vh-61px)]">
      <div className="p-4 space-y-1">
        <p className="px-3 text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-2">
          Módulos Institucionales
        </p>

        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = currentTab === item.id;

          return (
            <button
              key={item.id}
              onClick={() => onSelectTab(item.id)}
              className={`w-full flex items-center space-x-3 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                isActive
                  ? 'bg-blue-600 text-white shadow-xs'
                  : 'text-slate-400 hover:text-white hover:bg-slate-800/60'
              }`}
            >
              <Icon className={`w-4 h-4 ${isActive ? 'text-white' : 'text-slate-400'}`} />
              <span>{item.label}</span>
            </button>
          );
        })}
      </div>

      {/* Info de Cumplimiento OWASP en Sidebar */}
      <div className="mt-auto p-4 m-3 rounded-xl bg-slate-800/40 border border-slate-700/50">
        <p className="text-[11px] font-bold text-slate-300 flex items-center space-x-1.5">
          <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
          <span>Seguridad Activa</span>
        </p>
        <p className="text-[10px] text-slate-400 mt-1 leading-relaxed">
          Aislamiento Multi-Tenant estricto y protección activa contra OWASP Top 10 y XXE.
        </p>
      </div>
    </aside>
  );
};
