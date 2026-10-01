import React, { useState } from 'react';
import { Modal } from '../common/Modal';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ticketApi } from '../../api/ticketApi';
import type { Tramite } from '../../types';

interface DerivarTicketModalProps {
  tramite: Tramite | null;
  onClose: () => void;
}

export const DerivarTicketModal: React.FC<DerivarTicketModalProps> = ({ tramite, onClose }) => {
  const queryClient = useQueryClient();
  const [nuevoDepartamento, setNuevoDepartamento] = useState('RENTAS');
  const [observacion, setObservacion] = useState('');
  const [error, setError] = useState<string | null>(null);

  const mutation = useMutation({
    mutationFn: () =>
      ticketApi.derivar(tramite!.id, {
        nuevoDepartamento,
        observacion,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tickets'] });
      queryClient.invalidateQueries({ queryKey: ['metrics'] });
      onClose();
      setObservacion('');
    },
    onError: (err: any) => {
      setError(err.response?.data?.message || 'Error al derivar trámite');
    },
  });

  if (!tramite) return null;

  return (
    <Modal isOpen={!!tramite} onClose={onClose} title={`Derivar Trámite: ${tramite.codigoTramite}`}>
      {error && (
        <div className="mb-4 p-3 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs">
          {error}
        </div>
      )}

      <form
        onSubmit={(e) => {
          e.preventDefault();
          mutation.mutate();
        }}
        className="space-y-4 text-xs"
      >
        <div>
          <p className="text-slate-500 mb-1 font-semibold">Asunto del Trámite:</p>
          <p className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 text-slate-800 font-medium">
            {tramite.asunto}
          </p>
        </div>

        <div>
          <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
            Reasignar a Dirección Municipal
          </label>
          <select
            value={nuevoDepartamento}
            onChange={(e) => setNuevoDepartamento(e.target.value)}
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
            Motivo / Instrucción de Derivación
          </label>
          <textarea
            required
            rows={3}
            value={observacion}
            onChange={(e) => setObservacion(e.target.value)}
            placeholder="Indique el motivo técnico por el cual se traslada la competencia..."
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
            {mutation.isPending ? 'Derivando...' : 'Confirmar Derivación'}
          </button>
        </div>
      </form>
    </Modal>
  );
};
