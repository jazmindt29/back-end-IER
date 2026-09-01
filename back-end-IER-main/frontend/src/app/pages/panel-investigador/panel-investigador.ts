import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { Investigador, Laboratorio, Reserva } from '../../core/models';

@Component({
  selector: 'app-panel-investigador',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './panel-investigador.html'
})
export class PanelInvestigadorComponent implements OnInit {
  private api = inject(ApiService);
  private auth = inject(AuthService);

  pestania: 'perfil' | 'reservas' = 'perfil';

  perfil: Investigador | null = null;
  mensajePerfil = '';
  errorPerfil = '';

  laboratorios: Laboratorio[] = [];
  reservas: Reserva[] = [];
  nuevaReserva = { laboratorioId: null as number | null, fecha: '', horaInicio: '', horaFin: '' };
  mensajeReserva = '';
  errorReserva = '';

  ngOnInit(): void {
    this.api.getMiPerfil().subscribe((p) => (this.perfil = p));
    this.api.getLaboratorios().subscribe((l) => (this.laboratorios = l));
    this.cargarReservas();
  }

  private cargarReservas(): void {
    const id = this.auth.investigadorId;
    if (id != null) {
      this.api.getReservas(id).subscribe((r) => (this.reservas = r));
    }
  }

  guardarPerfil(): void {
    if (!this.perfil) return;
    this.mensajePerfil = '';
    this.errorPerfil = '';

    // Usamos guardarMiPerfil en lugar de actualizarInvestigador(this.perfil.id, ...)
    this.api.guardarMiPerfil(this.perfil).subscribe({
      next: (p) => {
        this.perfil = p;
        this.mensajePerfil = 'Perfil actualizado correctamente';
      },
      error: (e) => (this.errorPerfil = e.error?.mensaje ?? 'No se pudo guardar el perfil'),
    });
  
  }

  agendar(): void {
    this.mensajeReserva = '';
    this.errorReserva = '';
    const r = this.nuevaReserva;
    this.api.crearReserva({
      fecha: r.fecha,
      horaInicio: r.horaInicio,
      horaFin: r.horaFin,
      laboratorio: { id: r.laboratorioId! } as Laboratorio,
    }).subscribe({
      next: () => {
        this.mensajeReserva = 'Reserva registrada';
        this.nuevaReserva = { laboratorioId: null, fecha: '', horaInicio: '', horaFin: '' };
        this.cargarReservas();
      },
      error: (e) => (this.errorReserva = e.error?.mensaje ?? 'No se pudo crear la reserva'),
    });
  }

  cancelar(reserva: Reserva): void {
    this.api.eliminarReserva(reserva.id).subscribe({
      next: () => this.cargarReservas(),
      error: (e) => (this.errorReserva = e.error?.mensaje ?? 'No se pudo cancelar la reserva'),
    });
  }

  hora(h: string): string {
    return h.slice(0, 5);
  }
}
