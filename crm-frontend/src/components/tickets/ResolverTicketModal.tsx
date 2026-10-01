import React, { useState } from 'react';
import type { Tramite } from '../../types';
import { ticketApi } from '../../api/ticketApi';
import { Modal } from '../common/Modal';
import { CheckCircle2, AlertCircle, FileText } from 'lucide-react';
import { useQueryClient } from '@tanstack/react-query';

interface ResolverTicketModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: () => void;
  ticket: Tramite | null;
}

export const ResolverTicketModal: React.FC<ResolverTicketModalProps> = ({
  isOpen,
  onClose,
  onSuccess,
  ticket,
}) => {
  const queryClient = useQueryClient();
  const [resolucionTexto, setResolucionTexto] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!ticket) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!resolucionTexto.trim() || resolucionTexto.trim().length < 10) {
      setError('El dictamen de resolución debe contener al menos 10 caracteres con la justificación técnica.');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      await ticketApi.resolver(ticket.id, {
        resolucionTexto: resolucionTexto.trim(),
      });
      queryClient.invalidateQueries({ queryKey: ['tickets'] });
      queryClient.invalidateQueries({ queryKey: ['metrics'] });
      setResolucionTexto('');
      if (onSuccess) onSuccess();
      onClose();
    } catch (err: unknown) {
      const errMessage = (err as { response?: { data?: { detail?: string; message?: string } } })?.response?.data?.detail
        || (err as { response?: { data?: { detail?: string; message?: string } } })?.response?.data?.message
        || 'Error al dictar la resolución del trámite.';
      setError(errMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`Dictar Resolución Técnica - ${ticket.codigoTramite}`}
    >
      <form onSubmit={handleSubmit} className="space-y-4 text-xs">
        {error && (
          <div className="p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-xl flex items-start space-x-2">
            <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5" />
            <span>{error}</span>
          </div>
        )}

        <div className="bg-slate-50 border border-slate-200 rounded-xl p-3 text-xs text-slate-700 space-y-1">
          <p><span className="font-bold text-slate-900">Asunto:</span> {ticket.asunto}</p>
          <p><span className="font-bold text-slate-900">Departamento Actual:</span> {ticket.departamentoDestino}</p>
          <p><span className="font-bold text-slate-900">Prioridad:</span> {ticket.prioridad}</p>
        </div>

        <div>
          <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1">
            Dictamen Técnico / Resolución Formal <span className="text-rose-500">*</span>
          </label>
          <div className="relative">
            <div className="absolute top-3 left-3 text-slate-400 pointer-events-none">
              <FileText className="w-4 h-4" />
            </div>
            <textarea
              required
              rows={4}
              value={resolucionTexto}
              onChange={(e) => setResolucionTexto(e.target.value)}
              placeholder="Describa las acciones ejecutadas, informe técnico/jurídico municipal y dictamen emitido..."
              className="w-full pl-9 pr-3 py-2 text-xs border border-slate-200 rounded-xl bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 text-slate-800"
            />
          </div>
          <p className="text-[11px] text-slate-400 mt-1">
            Al dictar resolución, el estado del trámite transicionará a <span className="font-semibold text-emerald-600">RESUELTO</span> y se detendrá el cronómetro SLA.
          </p>
        </div>

        <div className="flex justify-end space-x-2 pt-3 border-t border-slate-100">
          <button
            type="button"
            onClick={onClose}
            disabled={loading}
            className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-50 rounded-xl border border-slate-200 transition-colors cursor-pointer"
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={loading}
            className="px-4 py-2 text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl shadow-xs transition-colors flex items-center space-x-1.5 disabled:opacity-50 cursor-pointer"
          >
            <CheckCircle2 className="w-4 h-4" />
            <span>{loading ? 'Emitiendo Resolución...' : 'Emitir Resolución'}</span>
          </button>
        </div>
      </form>
    </Modal>
  );
};
