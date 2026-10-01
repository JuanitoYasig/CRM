import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { citizenApi } from '../../api/citizenApi';
import type { Contribuyente } from '../../types';
import { CreateCitizenModal } from './CreateCitizenModal';
import { CreateTicketModal } from '../tickets/CreateTicketModal';
import { Modal } from '../common/Modal';
import {
  Plus,
  Search,
  Users,
  RefreshCw,
  Mail,
  Phone,
  MapPin,
  FilePlus2,
  Eye,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const CitizenListView: React.FC = () => {
  const { tenantId } = useAuth();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTipo, setSelectedTipo] = useState<string>('TODOS');

  // Modals state
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [ticketCitizenId, setTicketCitizenId] = useState<number | null>(null);
  const [detalleCitizen, setDetalleCitizen] = useState<Contribuyente | null>(null);

  const { data: citizens = [], isLoading, isError, refetch } = useQuery({
    queryKey: ['citizens', tenantId],
    queryFn: () => citizenApi.getAll(),
  });

  const filteredCitizens = citizens.filter((c) => {
    const term = searchTerm.toLowerCase();
    const matchesSearch =
      c.numeroIdentificacion.toLowerCase().includes(term) ||
      c.nombreCompleto.toLowerCase().includes(term) ||
      (c.email && c.email.toLowerCase().includes(term)) ||
      (c.direccion?.parroquia && c.direccion.parroquia.toLowerCase().includes(term));

    const matchesTipo = selectedTipo === 'TODOS' || c.tipoIdentificacion === selectedTipo;

    return matchesSearch && matchesTipo;
  });

  const formatDate = (isoString?: string) => {
    if (!isoString) return 'N/A';
    try {
      const d = new Date(isoString);
      return d.toLocaleDateString('es-EC', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
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
            Padrón Municipal de Contribuyentes
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Registro unificado de ciudadanos, personas naturales y empresas del cantón ({tenantId}).
          </p>
        </div>

        <div className="flex items-center space-x-2">
          <button
            onClick={() => refetch()}
            className="p-2 rounded-xl border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 transition-colors cursor-pointer"
            title="Sincronizar padrón"
          >
            <RefreshCw className={`w-4 h-4 ${isLoading ? 'animate-spin text-blue-600' : ''}`} />
          </button>
          <button
            onClick={() => setIsCreateOpen(true)}
            className="flex items-center space-x-1.5 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold rounded-xl shadow-xs transition-colors cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>Registrar Contribuyente</span>
          </button>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white rounded-2xl border border-slate-200/80 p-3.5 shadow-xs space-y-3">
        <div className="grid grid-cols-1 md:grid-cols-12 gap-3 text-xs">
          {/* Search box */}
          <div className="relative md:col-span-8">
            <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
            <input
              type="text"
              placeholder="Buscar por Cédula, RUC, Apellido, Razón Social o Parroquia..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-9 pr-3 py-2 rounded-xl border border-slate-200 bg-slate-50/50 text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            />
          </div>

          {/* Tipo Identificación filter */}
          <div className="md:col-span-4">
            <select
              value={selectedTipo}
              onChange={(e) => setSelectedTipo(e.target.value)}
              className="w-full px-3 py-2 rounded-xl border border-slate-200 bg-slate-50/50 text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            >
              <option value="TODOS">Todos los Tipos de Documento</option>
              <option value="CEDULA">Cédula de Identidad</option>
              <option value="RUC_NATURAL">RUC Persona Natural</option>
              <option value="RUC_PRIVADA">RUC Sociedad Privada</option>
              <option value="RUC_PUBLICA">RUC Entidad Pública</option>
              <option value="PASAPORTE">Pasaporte</option>
            </select>
          </div>
        </div>

        {/* Counter footer */}
        <div className="flex items-center justify-between text-[11px] text-slate-400 pt-1 border-t border-slate-100">
          <span>Mostrando <strong>{filteredCitizens.length}</strong> de <strong>{citizens.length}</strong> contribuyentes registrados</span>
          {(searchTerm || selectedTipo !== 'TODOS') && (
            <button
              onClick={() => {
                setSearchTerm('');
                setSelectedTipo('TODOS');
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
            <p className="text-xs font-medium">Consultando padrón cantonal...</p>
          </div>
        ) : isError ? (
          <div className="p-8 text-center text-rose-600 bg-rose-50 text-xs">
            <p className="font-semibold">Error al conectar con la base de datos municipal.</p>
            <p className="text-[11px] text-rose-500 mt-1">Verifique la conexión con el servidor Spring Boot.</p>
          </div>
        ) : filteredCitizens.length === 0 ? (
          <div className="p-12 text-center text-slate-400">
            <Users className="w-8 h-8 mx-auto text-slate-300 mb-2" />
            <p className="text-xs font-semibold text-slate-600">No se encontraron contribuyentes en este registro</p>
            <p className="text-[11px] text-slate-400 mt-0.5">Registre un nuevo ciudadano o ajuste los términos de búsqueda.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50/80 border-b border-slate-200/70 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                <tr>
                  <th className="px-4 py-3">ID / Documento</th>
                  <th className="px-4 py-3">Nombres / Razón Social</th>
                  <th className="px-4 py-3">Contacto</th>
                  <th className="px-4 py-3">Domicilio Tributario</th>
                  <th className="px-4 py-3">Fecha Alta</th>
                  <th className="px-4 py-3 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredCitizens.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-50/60 transition-colors">
                    <td className="px-4 py-3.5 whitespace-nowrap">
                      <div className="font-mono font-bold text-slate-900">{c.numeroIdentificacion}</div>
                      <div className="inline-block mt-0.5 px-2 py-0.5 rounded text-[10px] font-semibold bg-slate-100 text-slate-600">
                        {c.tipoIdentificacion}
                      </div>
                    </td>

                    <td className="px-4 py-3.5">
                      <div className="font-semibold text-slate-800">{c.nombreCompleto}</div>
                      <div className="text-[11px] text-slate-400">ID Municipal: #{c.id}</div>
                    </td>

                    <td className="px-4 py-3.5 whitespace-nowrap">
                      {c.email ? (
                        <div className="flex items-center space-x-1 text-slate-600 text-[11px]">
                          <Mail className="w-3.5 h-3.5 text-slate-400" />
                          <span className="truncate max-w-[160px]">{c.email}</span>
                        </div>
                      ) : (
                        <span className="text-slate-300 italic text-[11px]">Sin correo</span>
                      )}
                      {c.telefono && (
                        <div className="flex items-center space-x-1 text-slate-500 text-[11px] mt-0.5">
                          <Phone className="w-3.5 h-3.5 text-slate-400" />
                          <span>{c.telefono}</span>
                        </div>
                      )}
                    </td>

                    <td className="px-4 py-3.5 max-w-xs">
                      {c.direccion?.callePrincipal ? (
                        <div className="text-slate-700 truncate" title={`${c.direccion.callePrincipal} ${c.direccion.numeroPredio || ''}`}>
                          {c.direccion.callePrincipal} {c.direccion.numeroPredio || ''}
                          {c.direccion.parroquia && (
                            <span className="text-[11px] text-slate-400 block">
                              Pq. {c.direccion.parroquia}
                            </span>
                          )}
                        </div>
                      ) : (
                        <span className="text-slate-300 italic text-[11px]">No especificado</span>
                      )}
                    </td>

                    <td className="px-4 py-3.5 whitespace-nowrap text-slate-500">
                      {formatDate(c.createdAt)}
                    </td>

                    <td className="px-4 py-3.5 whitespace-nowrap text-right">
                      <div className="flex items-center justify-end space-x-1.5">
                        {/* Shortcut: Crear Trámite para este contribuyente */}
                        <button
                          onClick={() => setTicketCitizenId(c.id)}
                          className="flex items-center space-x-1 px-2.5 py-1.5 rounded-lg bg-blue-50 text-blue-700 hover:bg-blue-100 font-semibold transition-colors cursor-pointer text-[11px]"
                          title="Iniciar nuevo trámite para este ciudadano"
                        >
                          <FilePlus2 className="w-3.5 h-3.5" />
                          <span>Trámite</span>
                        </button>

                        {/* Ver Detalle */}
                        <button
                          onClick={() => setDetalleCitizen(c)}
                          className="p-1.5 rounded-lg text-slate-500 hover:text-slate-800 hover:bg-slate-100 transition-colors cursor-pointer"
                          title="Ver ficha completa"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal: Registrar Contribuyente */}
      <CreateCitizenModal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
      />

      {/* Modal: Crear Trámite preseleccionando el ciudadano */}
      {ticketCitizenId && (
        <CreateTicketModal
          isOpen={!!ticketCitizenId}
          onClose={() => setTicketCitizenId(null)}
          defaultCitizenId={ticketCitizenId}
        />
      )}

      {/* Modal: Detalle Contribuyente */}
      {detalleCitizen && (
        <Modal
          isOpen={!!detalleCitizen}
          onClose={() => setDetalleCitizen(null)}
          title={`Ficha Contribuyente: ${detalleCitizen.nombreCompleto}`}
        >
          <div className="space-y-4 text-xs">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-blue-50 text-blue-700">
                {detalleCitizen.tipoDescripcion || detalleCitizen.tipoIdentificacion}
              </span>
              <span className="font-mono font-bold text-slate-800 text-sm">
                {detalleCitizen.numeroIdentificacion}
              </span>
            </div>

            <div className="grid grid-cols-2 gap-3 bg-slate-50/60 p-3 rounded-xl border border-slate-200">
              <div>
                <div className="text-[10px] text-slate-400 uppercase font-semibold">Jurisdicción GAD</div>
                <div className="font-bold text-slate-800 uppercase">{detalleCitizen.tenantId}</div>
              </div>
              <div>
                <div className="text-[10px] text-slate-400 uppercase font-semibold">Estado en Padrón</div>
                <div className="font-semibold text-emerald-600">ACTIVO Y HABILITADO</div>
              </div>
              <div>
                <div className="text-[10px] text-slate-400 uppercase font-semibold">Correo Electrónico</div>
                <div className="text-slate-700">{detalleCitizen.email || 'No registrado'}</div>
              </div>
              <div>
                <div className="text-[10px] text-slate-400 uppercase font-semibold">Teléfono de Contacto</div>
                <div className="text-slate-700">{detalleCitizen.telefono || 'No registrado'}</div>
              </div>
            </div>

            {detalleCitizen.direccion && (
              <div className="p-3 bg-white rounded-xl border border-slate-200 space-y-1.5">
                <div className="flex items-center space-x-1.5 font-bold text-slate-800">
                  <MapPin className="w-4 h-4 text-blue-600" />
                  <span>Domicilio y Referencia Catastral</span>
                </div>
                <p className="text-slate-700">
                  <span className="font-semibold">Calle:</span> {detalleCitizen.direccion.callePrincipal || 'N/A'}{' '}
                  {detalleCitizen.direccion.numeroPredio ? `N° ${detalleCitizen.direccion.numeroPredio}` : ''}
                  {detalleCitizen.direccion.calleSecundaria ? ` e intersección ${detalleCitizen.direccion.calleSecundaria}` : ''}
                </p>
                {detalleCitizen.direccion.referencia && (
                  <p className="text-slate-500 italic text-[11px]">
                    Ref: {detalleCitizen.direccion.referencia}
                  </p>
                )}
                {detalleCitizen.direccion.parroquia && (
                  <p className="text-slate-600 text-[11px]">
                    <span className="font-semibold">Parroquia:</span> {detalleCitizen.direccion.parroquia}
                  </p>
                )}
              </div>
            )}

            <div className="flex items-center justify-between pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => {
                  const id = detalleCitizen.id;
                  setDetalleCitizen(null);
                  setTicketCitizenId(id);
                }}
                className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold cursor-pointer flex items-center space-x-1.5"
              >
                <FilePlus2 className="w-4 h-4" />
                <span>Generar Trámite para este Contribuyente</span>
              </button>

              <button
                type="button"
                onClick={() => setDetalleCitizen(null)}
                className="px-4 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold cursor-pointer"
              >
                Cerrar
              </button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
