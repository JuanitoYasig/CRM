import React, { useState } from 'react';
import { Modal } from '../common/Modal';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { citizenApi } from '../../api/citizenApi';
import type { RegistrarContribuyenteRequest, TipoIdentificacion } from '../../types';
import { CheckCircle2, AlertCircle, User, MapPin, Mail, Phone } from 'lucide-react';

interface CreateCitizenModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

// Client-side implementation of Ecuador Document Validator (Módulo 10 & 11)
function validarCedulaEcuador(cedula: string): boolean {
  if (!/^\d{10}$/.test(cedula)) return false;
  const provincia = parseInt(cedula.substring(0, 2), 10);
  if ((provincia < 1 || provincia > 24) && provincia !== 30) return false;
  const tercerDigito = parseInt(cedula.charAt(2), 10);
  if (tercerDigito < 0 || tercerDigito >= 6) return false;

  const coeficientes = [2, 1, 2, 1, 2, 1, 2, 1, 2];
  let suma = 0;
  for (let i = 0; i < coeficientes.length; i++) {
    let prod = parseInt(cedula.charAt(i), 10) * coeficientes[i];
    if (prod >= 10) prod -= 9;
    suma += prod;
  }
  const verificadorCalc = suma % 10 === 0 ? 0 : 10 - (suma % 10);
  return verificadorCalc === parseInt(cedula.charAt(9), 10);
}

function validarRucNatural(ruc: string): boolean {
  if (!/^\d{13}$/.test(ruc)) return false;
  if (!ruc.endsWith('001')) return false;
  return validarCedulaEcuador(ruc.substring(0, 10));
}

export const CreateCitizenModal: React.FC<CreateCitizenModalProps> = ({
  isOpen,
  onClose,
  onSuccess,
}) => {
  const queryClient = useQueryClient();

  const [tipoIdentificacion, setTipoIdentificacion] = useState<TipoIdentificacion>('CEDULA');
  const [numeroIdentificacion, setNumeroIdentificacion] = useState('');
  const [nombres, setNombres] = useState('');
  const [apellidos, setApellidos] = useState('');
  const [email, setEmail] = useState('');
  const [telefono, setTelefono] = useState('');

  // Dirección
  const [callePrincipal, setCallePrincipal] = useState('');
  const [calleSecundaria, setCalleSecundaria] = useState('');
  const [numeroPredio, setNumeroPredio] = useState('');
  const [referencia, setReferencia] = useState('');
  const [parroquia, setParroquia] = useState('');

  const [error, setError] = useState<string | null>(null);

  // Dynamic document validation feedback
  const isDocumentValid = () => {
    if (!numeroIdentificacion) return null;
    if (tipoIdentificacion === 'CEDULA') {
      return validarCedulaEcuador(numeroIdentificacion);
    }
    if (tipoIdentificacion === 'RUC_NATURAL') {
      return validarRucNatural(numeroIdentificacion);
    }
    if (tipoIdentificacion === 'PASAPORTE') {
      return /^[a-zA-Z0-9]{3,20}$/.test(numeroIdentificacion);
    }
    // RUC privada / publica
    return /^\d{13}$/.test(numeroIdentificacion);
  };

  const docValidation = isDocumentValid();

  const mutation = useMutation({
    mutationFn: (data: RegistrarContribuyenteRequest) => citizenApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['citizens'] });
      resetForm();
      if (onSuccess) onSuccess();
      onClose();
    },
    onError: (err: unknown) => {
      const msg = (err as { response?: { data?: { detail?: string; message?: string } } })?.response?.data?.detail
        || (err as { response?: { data?: { detail?: string; message?: string } } })?.response?.data?.message
        || 'Error al registrar el contribuyente en el padrón.';
      setError(msg);
    },
  });

  const resetForm = () => {
    setTipoIdentificacion('CEDULA');
    setNumeroIdentificacion('');
    setNombres('');
    setApellidos('');
    setEmail('');
    setTelefono('');
    setCallePrincipal('');
    setCalleSecundaria('');
    setNumeroPredio('');
    setReferencia('');
    setParroquia('');
    setError(null);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (tipoIdentificacion === 'CEDULA' && !validarCedulaEcuador(numeroIdentificacion)) {
      setError('La Cédula ecuatoriana ingresada no cumple el algoritmo Módulo 10 de la Dirección General de Registro Civil.');
      return;
    }

    if (tipoIdentificacion === 'RUC_NATURAL' && !validarRucNatural(numeroIdentificacion)) {
      setError('El RUC persona natural debe constar de una Cédula válida y terminar en 001.');
      return;
    }

    const payload: RegistrarContribuyenteRequest = {
      tipoIdentificacion,
      numeroIdentificacion: numeroIdentificacion.trim(),
      nombres: nombres.trim(),
      apellidos: apellidos.trim() || undefined,
      email: email.trim() || undefined,
      telefono: telefono.trim() || undefined,
      direccion: {
        callePrincipal: callePrincipal.trim() || undefined,
        calleSecundaria: calleSecundaria.trim() || undefined,
        numeroPredio: numeroPredio.trim() || undefined,
        referencia: referencia.trim() || undefined,
        parroquia: parroquia.trim() || undefined,
      },
    };

    mutation.mutate(payload);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Registro de Contribuyente en Padrón GAD">
      {error && (
        <div className="mb-4 p-3 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs flex items-start space-x-2">
          <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4 text-xs">
        {/* Identificación */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
              Tipo Identificación <span className="text-rose-500">*</span>
            </label>
            <select
              value={tipoIdentificacion}
              onChange={(e) => setTipoIdentificacion(e.target.value as TipoIdentificacion)}
              className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            >
              <option value="CEDULA">Cédula de Identidad</option>
              <option value="RUC_NATURAL">RUC Persona Natural</option>
              <option value="RUC_PRIVADA">RUC Sociedad Privada</option>
              <option value="RUC_PUBLICA">RUC Institución Pública</option>
              <option value="PASAPORTE">Pasaporte Extranjero</option>
            </select>
          </div>

          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="block font-bold text-slate-700 uppercase tracking-wider">
                N° Documento <span className="text-rose-500">*</span>
              </label>
              {docValidation !== null && (
                <span className={`text-[10px] font-bold ${docValidation ? 'text-emerald-600' : 'text-rose-500'}`}>
                  {docValidation ? '✓ Válido (M10)' : '✕ Inválido'}
                </span>
              )}
            </div>
            <input
              type="text"
              required
              maxLength={tipoIdentificacion.startsWith('RUC') ? 13 : 10}
              placeholder={tipoIdentificacion === 'CEDULA' ? 'ej: 1710034065' : 'ej: 1710034065001'}
              value={numeroIdentificacion}
              onChange={(e) => setNumeroIdentificacion(e.target.value.trim())}
              className={`w-full px-3 py-2 rounded-xl border font-mono text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 ${
                docValidation === null
                  ? 'border-slate-200 focus:ring-blue-500/20'
                  : docValidation
                  ? 'border-emerald-400 focus:ring-emerald-500/20'
                  : 'border-rose-300 focus:ring-rose-500/20'
              }`}
            />
          </div>
        </div>

        {/* Nombres y Apellidos */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
              Nombres o Razón Social <span className="text-rose-500">*</span>
            </label>
            <div className="relative">
              <User className="w-3.5 h-3.5 absolute left-3 top-2.5 text-slate-400" />
              <input
                type="text"
                required
                value={nombres}
                onChange={(e) => setNombres(e.target.value)}
                placeholder="ej: Juan Carlos"
                className="w-full pl-8 pr-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
              />
            </div>
          </div>

          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
              Apellidos
            </label>
            <input
              type="text"
              value={apellidos}
              onChange={(e) => setApellidos(e.target.value)}
              placeholder="ej: Pérez Morales"
              className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            />
          </div>
        </div>

        {/* Contacto: Email & Teléfono */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
              Correo Electrónico
            </label>
            <div className="relative">
              <Mail className="w-3.5 h-3.5 absolute left-3 top-2.5 text-slate-400" />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="ciudadano@ejemplo.gob.ec"
                className="w-full pl-8 pr-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
              />
            </div>
          </div>

          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
              Teléfono Celular / Convencional
            </label>
            <div className="relative">
              <Phone className="w-3.5 h-3.5 absolute left-3 top-2.5 text-slate-400" />
              <input
                type="tel"
                value={telefono}
                onChange={(e) => setTelefono(e.target.value)}
                placeholder="0991234567"
                className="w-full pl-8 pr-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
              />
            </div>
          </div>
        </div>

        {/* Dirección Domiciliaria / Catastral */}
        <div className="pt-2 border-t border-slate-100">
          <div className="flex items-center space-x-1.5 mb-2 text-slate-700 font-bold uppercase tracking-wider text-[11px]">
            <MapPin className="w-3.5 h-3.5 text-blue-600" />
            <span>Ubicación y Domicilio Tributario</span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-2.5">
            <div className="sm:col-span-2">
              <input
                type="text"
                value={callePrincipal}
                onChange={(e) => setCallePrincipal(e.target.value)}
                placeholder="Calle Principal"
                className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
              />
            </div>
            <div>
              <input
                type="text"
                value={numeroPredio}
                onChange={(e) => setNumeroPredio(e.target.value)}
                placeholder="N° Predio / Casa"
                className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5 mt-2">
            <div>
              <input
                type="text"
                value={calleSecundaria}
                onChange={(e) => setCalleSecundaria(e.target.value)}
                placeholder="Intersección / Calle Secundaria"
                className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
              />
            </div>
            <div>
              <input
                type="text"
                value={parroquia}
                onChange={(e) => setParroquia(e.target.value)}
                placeholder="Parroquia Urbana / Rural"
                className="w-full px-3 py-2 rounded-xl border border-slate-200 text-slate-800 bg-slate-50/50 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
              />
            </div>
          </div>
        </div>

        {/* Buttons */}
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
            className="px-5 py-2 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold shadow-xs disabled:opacity-60 cursor-pointer flex items-center space-x-1.5"
          >
            <CheckCircle2 className="w-4 h-4" />
            <span>{mutation.isPending ? 'Registrando...' : 'Registrar en Padrón'}</span>
          </button>
        </div>
      </form>
    </Modal>
  );
};
