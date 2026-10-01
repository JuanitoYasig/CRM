import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { ticketApi } from '../../api/ticketApi';
import { StatCard } from '../common/StatCard';
import { FileText, Clock, CheckCircle2, AlertTriangle, Building2, RefreshCw } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const DashboardView: React.FC = () => {
  const { tenantId } = useAuth();

  const { data: metrics, isLoading, isError, refetch } = useQuery({
    queryKey: ['metrics', tenantId],
    queryFn: () => ticketApi.getMetrics(),
    refetchInterval: 15000, // Actualización automática cada 15 segundos
  });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center p-12 text-slate-400">
        <RefreshCw className="w-6 h-6 animate-spin mr-2 text-blue-600" />
        <span className="text-xs font-semibold">Cargando métricas institucionales...</span>
      </div>
    );
  }

  if (isError || !metrics) {
    return (
      <div className="p-6 bg-rose-50 border border-rose-200 rounded-2xl text-rose-700 text-xs">
        No se pudieron sincronizar las métricas con el servidor. Verifique la conexión con el backend de Spring Boot.
      </div>
    );
  }

  const enProceso = metrics.tramitesCreados + metrics.tramitesEnRevision + metrics.tramitesDerivados;

  return (
    <div className="space-y-6">
      {/* Encabezado del Dashboard */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-bold tracking-tight text-slate-800">Panel Directivo de Gestión Municipal</h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Monitoreo en tiempo real de trámites y cumplimiento de SLAs para{' '}
            <span className="font-bold text-slate-600 uppercase">{tenantId}</span>
          </p>
        </div>

        <button
          onClick={() => refetch()}
          className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg border border-slate-200 bg-white text-xs font-semibold text-slate-600 hover:bg-slate-50 transition-colors cursor-pointer"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          <span>Actualizar</span>
        </button>
      </div>

      {/* Tarjetas de Métricas Principales (KPIs) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Trámites"
          value={metrics.totalTramites}
          subtitle="Registrados en la jurisdicción"
          icon={FileText}
          color="blue"
        />
        <StatCard
          title="En Gestión / Trámite"
          value={enProceso}
          subtitle="Pendientes y derivados"
          icon={Clock}
          color="amber"
        />
        <StatCard
          title="Resueltos / Cerrados"
          value={metrics.tramitesResueltos + metrics.tramitesCerrados}
          subtitle="Atención concluida"
          icon={CheckCircle2}
          color="emerald"
        />
        <StatCard
          title="Vencidos Fuera de SLA"
          value={metrics.tramitesVencidosSla}
          subtitle="Requieren atención urgente"
          icon={AlertTriangle}
          color="rose"
        />
      </div>

      {/* Distribución por Dirección Municipal */}
      <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs">
        <div className="flex items-center space-x-2.5 mb-5">
          <Building2 className="w-5 h-5 text-blue-600" />
          <h3 className="text-sm font-bold text-slate-800">Carga de Trabajo por Dependencia Municipal</h3>
        </div>

        {Object.keys(metrics.tramitesPorDepartamento).length === 0 ? (
          <p className="text-xs text-slate-400 py-4 text-center">No hay trámites registrados en esta jurisdicción aún.</p>
        ) : (
          <div className="space-y-4">
            {Object.entries(metrics.tramitesPorDepartamento).map(([depto, count]) => {
              const porcentaje = metrics.totalTramites > 0 ? Math.round((count / metrics.totalTramites) * 100) : 0;

              return (
                <div key={depto} className="space-y-1.5">
                  <div className="flex items-center justify-between text-xs">
                    <span className="font-semibold text-slate-700">{depto}</span>
                    <span className="text-slate-500 font-bold">
                      {count} {count === 1 ? 'caso' : 'casos'} ({porcentaje}%)
                    </span>
                  </div>
                  <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-blue-600 rounded-full transition-all duration-500"
                      style={{ width: `${porcentaje}%` }}
                    ></div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};
