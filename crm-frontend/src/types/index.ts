export type TipoIdentificacion = 'CEDULA' | 'RUC_NATURAL' | 'RUC_PRIVADA' | 'RUC_PUBLICA' | 'PASAPORTE';

export interface Direccion {
  callePrincipal?: string;
  calleSecundaria?: string;
  numeroPredio?: string;
  referencia?: string;
  parroquia?: string;
}

export interface Contribuyente {
  id: number;
  tenantId: string;
  tipoIdentificacion: TipoIdentificacion;
  tipoDescripcion: string;
  numeroIdentificacion: string;
  nombres: string;
  apellidos?: string;
  nombreCompleto: string;
  email?: string;
  telefono?: string;
  direccion?: Direccion;
  activo: boolean;
  createdAt: string;
}

export interface RegistrarContribuyenteRequest {
  tipoIdentificacion: TipoIdentificacion;
  numeroIdentificacion: string;
  nombres: string;
  apellidos?: string;
  email?: string;
  telefono?: string;
  direccion?: Direccion;
}

export type EstadoTramite = 'CREADO' | 'EN_REVISION' | 'DERIVADO' | 'RESUELTO' | 'CERRADO';
export type PrioridadTramite = 'BAJA' | 'MEDIA' | 'ALTA' | 'URGENTE';
export type CanalRecepcion = 'VENTANILLA_PRESENCIAL' | 'PORTAL_WEB' | 'APP_MOVIL' | 'LINEA_TELEFONICA';

export interface Tramite {
  id: number;
  tenantId: string;
  codigoTramite: string;
  asunto: string;
  descripcion: string;
  departamentoDestino: string;
  contribuyenteId: number;
  estado: EstadoTramite;
  estadoDescripcion: string;
  prioridad: PrioridadTramite;
  canal: CanalRecepcion;
  fechaVencimientoSla: string;
  estaVencido: boolean;
  fechaResolucion?: string;
  resolucionTexto?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface CrearTramiteRequest {
  contribuyenteId: number;
  tipoServicio?: string;
  asunto: string;
  descripcion: string;
  departamentoDestino: string;
  prioridad: PrioridadTramite;
  canal: CanalRecepcion;
}

export interface DerivarTramiteRequest {
  nuevoDepartamento: string;
  observacion?: string;
}

export interface ResolverTramiteRequest {
  resolucionTexto: string;
}

export interface DashboardMetricas {
  tenantId: string;
  totalTramites: number;
  tramitesCreados: number;
  tramitesEnRevision: number;
  tramitesDerivados: number;
  tramitesResueltos: number;
  tramitesCerrados: number;
  tramitesVencidosSla: number;
  tramitesPorDepartamento: Record<string, number>;
}

export type Rol = 'ADMIN_GENERAL' | 'DIRECTOR_DEPARTAMENTAL' | 'VENTANILLA_ATENCION' | 'TECNICO_OPERATIVO' | 'AUDITOR_INTERNO';

export interface LoginResponse {
  token: string;
  tokenType: string;
  username: string;
  nombreCompleto: string;
  departamento: string;
  rol: Rol;
  rolDescripcion: string;
  tenantId: string;
  expiraEnSegundos: number;
}
