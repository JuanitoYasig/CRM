import React from 'react';
import type { EstadoTramite, PrioridadTramite } from '../../types';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'default' | 'success' | 'warning' | 'danger' | 'info' | 'neutral';
  size?: 'sm' | 'md';
}

export const Badge: React.FC<BadgeProps> = ({ children, variant = 'default', size = 'sm' }) => {
  const styles = {
    default: 'bg-slate-100 text-slate-800 border-slate-200',
    neutral: 'bg-gray-100 text-gray-700 border-gray-200',
    info: 'bg-blue-50 text-blue-700 border-blue-200',
    success: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    warning: 'bg-amber-50 text-amber-700 border-amber-200',
    danger: 'bg-rose-50 text-rose-700 border-rose-200',
  };

  const sizes = {
    sm: 'text-xs px-2.5 py-0.5',
    md: 'text-sm px-3 py-1',
  };

  return (
    <span className={`inline-flex items-center font-medium rounded-full border ${styles[variant]} ${sizes[size]}`}>
      {children}
    </span>
  );
};

export const EstadoBadge: React.FC<{ estado: EstadoTramite; descripcion?: string }> = ({ estado, descripcion }) => {
  const map: Record<EstadoTramite, { variant: 'info' | 'warning' | 'default' | 'success' | 'neutral'; label: string }> = {
    CREADO: { variant: 'info', label: 'Creado' },
    EN_REVISION: { variant: 'warning', label: 'En Revisión' },
    DERIVADO: { variant: 'default', label: 'Derivado' },
    RESUELTO: { variant: 'success', label: 'Resuelto' },
    CERRADO: { variant: 'neutral', label: 'Cerrado' },
  };

  const cfg = map[estado] || { variant: 'default', label: estado };
  return <Badge variant={cfg.variant}>{descripcion || cfg.label}</Badge>;
};

export const PrioridadBadge: React.FC<{ prioridad: PrioridadTramite }> = ({ prioridad }) => {
  const map: Record<PrioridadTramite, { variant: 'neutral' | 'info' | 'warning' | 'danger'; label: string }> = {
    BAJA: { variant: 'neutral', label: 'Baja' },
    MEDIA: { variant: 'info', label: 'Media' },
    ALTA: { variant: 'warning', label: 'Alta' },
    URGENTE: { variant: 'danger', label: 'Urgente' },
  };

  const cfg = map[prioridad] || { variant: 'neutral', label: prioridad };
  return <Badge variant={cfg.variant}>{cfg.label}</Badge>;
};
