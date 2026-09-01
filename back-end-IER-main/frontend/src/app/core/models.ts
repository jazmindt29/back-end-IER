// Interfaces espejo de las entidades del backend

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
  id?: number;
  nombre: string;
  gradoAcademico?: string;  // Nombre alineado con la entidad Java
  area?: string;
  especialidad?: string;
  correo?: string;          // Nombre alineado con la entidad Java
  fotoUrl?: string;
  bioCorta?: string;        // Nombre alineado con la entidad Java
  biografia?: string;
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
  laboratorio: Laboratorio;
  investigador?: Investigador;
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