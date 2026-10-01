import React, { useState } from 'react';
import { Modal } from '../common/Modal';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ticketApi } from '../../api/ticketApi';
import type { CanalRecepcion, CrearTramiteRequest, PrioridadTramite } from '../../types';

interface CreateTicketModalProps {
  isOpen: boolean;
  onClose: () => void;
  defaultCitizenId?: number;
}

export const CreateTicketModal: React.FC<CreateTicketModalProps> = ({
  isOpen,
  onClose,
  defaultCitizenId = 1,
}) => {
  const queryClient = useQueryClient();
  const [contribuyenteId, setContribuyenteId] = useState<number>(defaultCitizenId);
  const [asunto, setAsunto] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [departamentoDestino, setDepartamentoDestino] = useState('OBRAS_PUBLICAS');
  const [prioridad, setPrioridad] = useState<PrioridadTramite>('MEDIA');
  const [canal, setCanal] = useState<CanalRecepcion>('VENTANILLA_PRESENCIAL');
  const [tipoServicio, setTipoServicio] = useState('ORDINARIO');
  const [error, setError] = useState<string | null>(null);

  const mutation = useMutation({
    mutationFn: (data: CrearTramiteRequest) => ticketApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tickets'] });
      queryClient.invalidateQueries({ queryKey: ['metrics'] });
      onClose();
      setAsunto('');
      setDescripcion('');
    },
    onError: (err: any) => {
      setError(err.response?.data?.message || 'Error al emitir el trámite');
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    mutation.mutate({
      contribuyenteId,
      tipoServicio,
      asunto,
      descripcion,
      departamentoDestino,
      prioridad,
      canal,
    });
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Ingreso de Nuevo Trámite Ciudadano">
      {error && (
        <div className="mb-4 p-3 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4 text-xs">
        <div>
          <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
            ID de Contribuyente Registrado
          </label>
          <input
            type="number"
            required
            value={contribuyenteId}
            onChange={(e) => setContribuyenteId(Number(e.target.value))}
            className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
          />
          <p className="text-[11px] text-slate-400 mt-1">El contribuyente debe existir en el padrón de este GAD.</p>
        </div>

        <div>
          <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
            Asunto del Trámite / Incidencia
          </label>
          <input
            type="text"
            required
            value={asunto}
            onChange={(e) => setAsunto(e.target.value)}
            placeholder="ej: Solicitud de bacheo en calle Bolívar y Rocafuerte"
            className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
              Dirección Municipal
            </label>
            <select
              value={departamentoDestino}
              onChange={(e) => setDepartamentoDestino(e.target.value)}
              className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            >
              <option value="OBRAS_PUBLICAS">Obras Públicas</option>
              <option value="RENTAS">Rentas y Recaudación</option>
              <option value="AVALUOS_CATASTROS">Avalúos y Catastros</option>
              <option value="PLANIFICACION">Planificación Urbana</option>
              <option value="AGUA_POTABLE">Agua Potable y Alcantarillado</option>
              <option value="JUSTICIA_VIGILANCIA">Justicia y Vigilancia</option>
            </select>
          </div>

          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
              Prioridad / SLA
            </label>
            <select
              value={prioridad}
              onChange={(e) => setPrioridad(e.target.value as PrioridadTramite)}
              className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            >
              <option value="BAJA">Baja (5 días)</option>
              <option value="MEDIA">Media (3 días)</option>
              <option value="ALTA">Alta (24 horas)</option>
              <option value="URGENTE">Urgente (8 horas)</option>
            </select>
          </div>
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
              Tipo de Servicio (Regla SLA)
            </label>
            <select
              value={tipoServicio}
              onChange={(e) => setTipoServicio(e.target.value)}
              className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            >
              <option value="ORDINARIO">Trámite Ordinario</option>
              <option value="EMERGENCIA_VIAL">Emergencia Vial (SLA 6h)</option>
              <option value="AGUA_POTABLE_ROTURA">Rotura Matriz Agua (SLA 6h)</option>
            </select>
          </div>

          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
              Canal de Recepción
            </label>
            <select
              value={canal}
              onChange={(e) => setCanal(e.target.value as CanalRecepcion)}
              className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            >
              <option value="VENTANILLA_PRESENCIAL">Ventanilla Única</option>
              <option value="PORTAL_WEB">Portal Web</option>
              <option value="APP_MOVIL">App Móvil GAD</option>
              <option value="LINEA_TELEFONICA">Línea 1800</option>
            </select>
          </div>
        </div>

        <div>
          <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
            Detalle / Descripción de la Solicitud
          </label>
          <textarea
            required
            rows={3}
            value={descripcion}
            onChange={(e) => setDescripcion(e.target.value)}
            placeholder="Especifique los antecedentes, ubicación y requerimiento técnico..."
            className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
          ></textarea>
        </div>

        <div className="flex items-center justify-end space-x-2 pt-3 border-t border-slate-100">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-50 font-semibold cursor-pointer"
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={mutation.isPending}
            className="px-5 py-2 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold shadow-xs disabled:opacity-60 cursor-pointer"
          >
            {mutation.isPending ? 'Emitiendo...' : 'Registrar Trámite'}
          </button>
        </div>
      </form>
    </Modal>
  );
};
