// Contratos de lectura del API; no son entidades JPA.

export interface Rol {
  id: number;
  nombre: string;
}

export interface Usuario {
  id: number;
  username: string;
  enable?: boolean;
  createdAt?: string;
  rol?: Rol;
}

export interface Investigador {
  id: number;
  nombre: string;
  area?: string;
  especialidad?: string;
  grado?: string;
  bio?: string;
  biografia?: string;
  fotoUrl?: string;
  correoInstitucional?: string;
  usuario?: Usuario;
}

export interface Laboratorio {
  id: number;
  nombre: string;
  descripcion?: string;
  imagenUrl?: string;
  encargado?: Investigador;
}

export interface Proyecto {
  id: number;
  titulo: string;
  descripcion?: string;
  estado?: string;
  esDestacado?: boolean;
  imagenUrl?: string;
  investigadores?: Investigador[];
}

export interface Componente {
  id: number;
  nombre: string;
  descripcion?: string;
  tipo?: string;
  laboratorio?: Laboratorio;
}

export interface Aviso {
  id: number;
  titulo: string;
  contenido: string;
  fechaPublicacion?: string;
  imagenUrl?: string;
  autor?: Usuario;
}

export interface Reserva {
  id: number;
  fecha: string;        // yyyy-MM-dd
  horaInicio: string;   // HH:mm:ss
  horaFin: string;
  laboratorio: Pick<Laboratorio, 'id' | 'nombre'>;
  investigador?: Pick<Investigador, 'id' | 'nombre'>;
}

export interface ReservaRequest {
  fecha: string;
  horaInicio: string;
  horaFin: string;
  laboratorio: { id: number };
  investigador?: { id: number };
}

export interface Actividad {
  id: number;
  titulo: string;
  fecha?: string;
  descripcion?: string;
}

export interface Auditoria {
  id: number;
  createdAt?: string;
  accion?: string;
  entidad?: string;
  entidadId?: number;
  updatedBy?: number;
}

export interface LoginResponse {
  token: string;
  username: string;
  rol: string;
  userId: number;
  investigadorId?: number;
}
