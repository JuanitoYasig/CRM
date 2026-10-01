import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ticketApi } from '../../api/ticketApi';
import { EstadoBadge, PrioridadBadge } from '../common/Badge';
import type { Tramite } from '../../types';
import { CreateTicketModal } from './CreateTicketModal';
import { DerivarTicketModal } from './DerivarTicketModal';
import { ResolverTicketModal } from './ResolverTicketModal';
import { Modal } from '../common/Modal';
import {
  Plus,
  Search,
  Filter,
  RefreshCw,
  AlertTriangle,
  ArrowRightLeft,
  CheckCircle,
  Eye,
  Calendar,
  Building,
  Radio,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const TicketListView: React.FC = () => {
  const { tenantId } = useAuth();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedEstado, setSelectedEstado] = useState<string>('TODOS');
  const [selectedDepartamento, setSelectedDepartamento] = useState<string>('TODOS');
  const [onlyOverdue, setOnlyOverdue] = useState<boolean>(false);

  // Modals state
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [derivandoTicket, setDerivandoTicket] = useState<Tramite | null>(null);
  const [resolviendoTicket, setResolviendoTicket] = useState<Tramite | null>(null);
  const [detalleTicket, setDetalleTicket] = useState<Tramite | null>(null);

  const { data: tickets = [], isLoading, isError, refetch } = useQuery({
    queryKey: ['tickets', tenantId],
    queryFn: () => ticketApi.getAll(),
    refetchInterval: 20000,
  });

  // Client-side filtering for fast interactive search & multi-facet filtering
  const filteredTickets = tickets.filter((t) => {
    const matchesSearch =
      t.codigoTramite.toLowerCase().includes(searchTerm.toLowerCase()) ||
      t.asunto.toLowerCase().includes(searchTerm.toLowerCase()) ||
      t.descripcion.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesEstado = selectedEstado === 'TODOS' || t.estado === selectedEstado;
    const matchesDepartamento = selectedDepartamento === 'TODOS' || t.departamentoDestino === selectedDepartamento;
    const matchesOverdue = !onlyOverdue || t.estaVencido;

    return matchesSearch && matchesEstado && matchesDepartamento && matchesOverdue;
  });

  const formatDate = (isoString?: string) => {
    if (!isoString) return 'N/A';
    try {
      const d = new Date(isoString);
      return d.toLocaleDateString('es-EC', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return isoString;
    }
  };

  return (
    <div className="space-y-5">
      {/* Header & Main CTA */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h2 className="text-xl font-bold tracking-tight text-slate-800">
            Bandeja Central de Trámites Municipales
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Seguimiento de requerimientos ciudadanos, derivaciones interdepartamentales y control de SLAs ({tenantId}).
          </p>
        </div>

        <div className="flex items-center space-x-2">
          <button
            onClick={() => refetch()}
            className="p-2 rounded-xl border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 transition-colors cursor-pointer"
            title="Sincronizar trámites"
          >
            <RefreshCw className={`w-4 h-4 ${isLoading ? 'animate-spin text-blue-600' : ''}`} />
          </button>
          <button
            onClick={() => setIsCreateOpen(true)}
            className="flex items-center space-x-1.5 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold rounded-xl shadow-xs transition-colors cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>Nuevo Trámite</span>
          </button>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white rounded-2xl border border-slate-200/80 p-3.5 shadow-xs space-y-3">
        <div className="grid grid-cols-1 md:grid-cols-12 gap-3 text-xs">
          {/* Search box */}
          <div className="relative md:col-span-4">
            <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
            <input
              type="text"
              placeholder="Buscar por código (TRM-...) o asunto..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-9 pr-3 py-2 rounded-xl border border-slate-200 bg-slate-50/50 text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            />
          </div>

          {/* Estado filter */}
          <div className="md:col-span-3">
            <select
              value={selectedEstado}
              onChange={(e) => setSelectedEstado(e.target.value)}
              className="w-full px-3 py-2 rounded-xl border border-slate-200 bg-slate-50/50 text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            >
              <option value="TODOS">Todos los Estados</option>
              <option value="CREADO">Creado</option>
              <option value="EN_REVISION">En Revisión</option>
              <option value="DERIVADO">Derivado</option>
              <option value="RESUELTO">Resuelto</option>
              <option value="CERRADO">Cerrado</option>
            </select>
          </div>

          {/* Departamento filter */}
          <div className="md:col-span-3">
            <select
              value={selectedDepartamento}
              onChange={(e) => setSelectedDepartamento(e.target.value)}
              className="w-full px-3 py-2 rounded-xl border border-slate-200 bg-slate-50/50 text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            >
              <option value="TODOS">Todas las Direcciones</option>
              <option value="OBRAS_PUBLICAS">Obras Públicas</option>
              <option value="RENTAS">Rentas y Recaudación</option>
              <option value="AVALUOS_CATASTROS">Avalúos y Catastros</option>
              <option value="PLANIFICACION">Planificación Urbana</option>
              <option value="AGUA_POTABLE">Agua Potable y Alcantarillado</option>
              <option value="JUSTICIA_VIGILANCIA">Justicia y Vigilancia</option>
            </select>
          </div>

          {/* Overdue SLA toggle */}
          <div className="md:col-span-2 flex items-center">
            <button
              type="button"
              onClick={() => setOnlyOverdue(!onlyOverdue)}
              className={`w-full flex items-center justify-center space-x-1.5 px-3 py-2 rounded-xl border text-xs font-semibold cursor-pointer transition-colors ${
                onlyOverdue
                  ? 'bg-rose-50 border-rose-300 text-rose-700 shadow-xs'
                  : 'bg-slate-50/50 border-slate-200 text-slate-600 hover:bg-slate-100'
              }`}
            >
              <AlertTriangle className={`w-3.5 h-3.5 ${onlyOverdue ? 'text-rose-600' : 'text-slate-400'}`} />
              <span>{onlyOverdue ? 'Solo Vencidos' : 'Filtro SLA'}</span>
            </button>
          </div>
        </div>

        {/* Counter tag */}
        <div className="flex items-center justify-between text-[11px] text-slate-400 pt-1 border-t border-slate-100">
          <span>Mostrando <strong>{filteredTickets.length}</strong> de <strong>{tickets.length}</strong> trámites registrados</span>
          {(searchTerm || selectedEstado !== 'TODOS' || selectedDepartamento !== 'TODOS' || onlyOverdue) && (
            <button
              onClick={() => {
                setSearchTerm('');
                setSelectedEstado('TODOS');
                setSelectedDepartamento('TODOS');
                setOnlyOverdue(false);
              }}
              className="text-blue-600 hover:underline cursor-pointer font-medium"
            >
              Limpiar filtros
            </button>
          )}
        </div>
      </div>

      {/* Main Table */}
      <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        {isLoading ? (
          <div className="p-12 text-center text-slate-400">
            <RefreshCw className="w-6 h-6 animate-spin mx-auto text-blue-600 mb-2" />
            <p className="text-xs font-medium">Cargando trámites del municipio...</p>
          </div>
        ) : isError ? (
          <div className="p-8 text-center text-rose-600 bg-rose-50 text-xs">
            <p className="font-semibold">Error al conectar con el servidor.</p>
            <p className="text-[11px] text-rose-500 mt-1">Verifique que el backend Spring Boot esté en ejecución en el puerto 8080.</p>
          </div>
        ) : filteredTickets.length === 0 ? (
          <div className="p-12 text-center text-slate-400">
            <Filter className="w-8 h-8 mx-auto text-slate-300 mb-2" />
            <p className="text-xs font-semibold text-slate-600">No se encontraron trámites con los criterios seleccionados</p>
            <p className="text-[11px] text-slate-400 mt-0.5">Intente cambiar los filtros o ingrese un nuevo requerimiento.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50/80 border-b border-slate-200/70 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                <tr>
                  <th className="px-4 py-3">Código / Fecha</th>
                  <th className="px-4 py-3">Asunto & Contribuyente</th>
                  <th className="px-4 py-3">Dependencia</th>
                  <th className="px-4 py-3">Prioridad</th>
                  <th className="px-4 py-3">Estado</th>
                  <th className="px-4 py-3">Control SLA</th>
                  <th className="px-4 py-3 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredTickets.map((tramite) => (
                  <tr key={tramite.id} className="hover:bg-slate-50/60 transition-colors">
                    <td className="px-4 py-3.5 whitespace-nowrap">
                      <div className="font-mono font-bold text-blue-600">{tramite.codigoTramite}</div>
                      <div className="text-[11px] text-slate-400 mt-0.5">{formatDate(tramite.createdAt)}</div>
                    </td>

                    <td className="px-4 py-3.5 max-w-xs">
                      <div className="font-semibold text-slate-800 truncate" title={tramite.asunto}>
                        {tramite.asunto}
                      </div>
                      <div className="text-[11px] text-slate-400 truncate mt-0.5">
                        Contribuyente ID: #{tramite.contribuyenteId}
                      </div>
                    </td>

                    <td className="px-4 py-3.5 whitespace-nowrap">
                      <span className="font-medium text-slate-700 bg-slate-100 px-2 py-0.5 rounded-md text-[11px]">
                        {tramite.departamentoDestino}
                      </span>
                    </td>

                    <td className="px-4 py-3.5 whitespace-nowrap">
                      <PrioridadBadge prioridad={tramite.prioridad} />
                    </td>

                    <td className="px-4 py-3.5 whitespace-nowrap">
                      <EstadoBadge estado={tramite.estado} descripcion={tramite.estadoDescripcion} />
                    </td>

                    <td className="px-4 py-3.5 whitespace-nowrap">
                      {tramite.estaVencido ? (
                        <div className="flex items-center space-x-1 text-rose-600 font-bold text-[11px]">
                          <AlertTriangle className="w-3.5 h-3.5 flex-shrink-0" />
                          <span>VENCIDO ({formatDate(tramite.fechaVencimientoSla).split(',')[0]})</span>
                        </div>
                      ) : (
                        <div className="text-[11px] text-emerald-700 font-medium">
                          En plazo: {formatDate(tramite.fechaVencimientoSla)}
                        </div>
                      )}
                    </td>

                    <td className="px-4 py-3.5 whitespace-nowrap text-right">
                      <div className="flex items-center justify-end space-x-1.5">
                        {/* Ver Detalle */}
                        <button
                          onClick={() => setDetalleTicket(tramite)}
                          className="p-1.5 rounded-lg text-slate-500 hover:text-slate-800 hover:bg-slate-100 transition-colors cursor-pointer"
                          title="Ver detalle completo"
                        >
                          <Eye className="w-4 h-4" />
                        </button>

                        {/* Derivar (disponible si no está resuelto o cerrado) */}
                        {tramite.estado !== 'RESUELTO' && tramite.estado !== 'CERRADO' && (
                          <button
                            onClick={() => setDerivandoTicket(tramite)}
                            className="p-1.5 rounded-lg text-amber-600 hover:text-amber-800 hover:bg-amber-50 transition-colors cursor-pointer"
                            title="Derivar a otra dependencia"
                          >
                            <ArrowRightLeft className="w-4 h-4" />
                          </button>
                        )}

                        {/* Resolver (disponible si no está resuelto o cerrado) */}
                        {tramite.estado !== 'RESUELTO' && tramite.estado !== 'CERRADO' && (
                          <button
                            onClick={() => setResolviendoTicket(tramite)}
                            className="p-1.5 rounded-lg text-emerald-600 hover:text-emerald-800 hover:bg-emerald-50 transition-colors cursor-pointer"
                            title="Emitir resolución formal"
                          >
                            <CheckCircle className="w-4 h-4" />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal: Crear Trámite */}
      <CreateTicketModal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
      />

      {/* Modal: Derivar Trámite */}
      <DerivarTicketModal
        tramite={derivandoTicket}
        onClose={() => setDerivandoTicket(null)}
      />

      {/* Modal: Resolver Trámite */}
      <ResolverTicketModal
        isOpen={!!resolviendoTicket}
        ticket={resolviendoTicket}
        onClose={() => setResolviendoTicket(null)}
      />

      {/* Modal: Detalle de Trámite */}
      {detalleTicket && (
        <Modal
          isOpen={!!detalleTicket}
          onClose={() => setDetalleTicket(null)}
          title={`Expediente: ${detalleTicket.codigoTramite}`}
        >
          <div className="space-y-4 text-xs">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <div className="flex items-center space-x-2">
                <EstadoBadge estado={detalleTicket.estado} descripcion={detalleTicket.estadoDescripcion} />
                <PrioridadBadge prioridad={detalleTicket.prioridad} />
              </div>
              <span className="text-slate-400 font-mono text-[11px]">{detalleTicket.tenantId}</span>
            </div>

            <div>
              <h4 className="font-bold text-slate-800 text-sm">{detalleTicket.asunto}</h4>
              <p className="text-slate-600 mt-2 p-3 bg-slate-50 rounded-xl border border-slate-200 whitespace-pre-wrap">
                {detalleTicket.descripcion}
              </p>
            </div>

            <div className="grid grid-cols-2 gap-3 bg-slate-50/50 p-3 rounded-xl border border-slate-200">
              <div className="flex items-center space-x-2 text-slate-600">
                <Building className="w-4 h-4 text-slate-400" />
                <div>
                  <div className="text-[10px] text-slate-400 uppercase font-semibold">Dependencia</div>
                  <div className="font-medium text-slate-800">{detalleTicket.departamentoDestino}</div>
                </div>
              </div>

              <div className="flex items-center space-x-2 text-slate-600">
                <Radio className="w-4 h-4 text-slate-400" />
                <div>
                  <div className="text-[10px] text-slate-400 uppercase font-semibold">Canal de Entrada</div>
                  <div className="font-medium text-slate-800">{detalleTicket.canal}</div>
                </div>
              </div>

              <div className="flex items-center space-x-2 text-slate-600">
                <Calendar className="w-4 h-4 text-slate-400" />
                <div>
                  <div className="text-[10px] text-slate-400 uppercase font-semibold">Fecha Ingreso</div>
                  <div className="font-medium text-slate-800">{formatDate(detalleTicket.createdAt)}</div>
                </div>
              </div>

              <div className="flex items-center space-x-2 text-slate-600">
                <AlertTriangle className={`w-4 h-4 ${detalleTicket.estaVencido ? 'text-rose-500' : 'text-emerald-500'}`} />
                <div>
                  <div className="text-[10px] text-slate-400 uppercase font-semibold">Límite SLA</div>
                  <div className={`font-semibold ${detalleTicket.estaVencido ? 'text-rose-600' : 'text-slate-800'}`}>
                    {formatDate(detalleTicket.fechaVencimientoSla)}
                  </div>
                </div>
              </div>
            </div>

            {detalleTicket.resolucionTexto && (
              <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-xl space-y-1">
                <div className="flex items-center space-x-1.5 text-emerald-800 font-bold">
                  <CheckCircle className="w-4 h-4" />
                  <span>Dictamen de Resolución ({formatDate(detalleTicket.fechaResolucion)})</span>
                </div>
                <p className="text-emerald-900 text-xs whitespace-pre-wrap mt-1">
                  {detalleTicket.resolucionTexto}
                </p>
              </div>
            )}

            <div className="flex justify-end pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setDetalleTicket(null)}
                className="px-4 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold cursor-pointer"
              >
                Cerrar Expediente
              </button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
