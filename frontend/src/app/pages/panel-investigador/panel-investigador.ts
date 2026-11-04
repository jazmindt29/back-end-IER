import { Component, OnInit, inject, DestroyRef, signal } from '@angular/core';
import { finalize, Subscription } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { mensajeHttp } from '../../core/http-error';
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
  private destroyRef = inject(DestroyRef);
  private cargaReservas?: Subscription;
  cargandoPerfil = signal(false);
  cargandoReservas = signal(false);
  guardandoPerfil = signal(false);
  guardandoReserva = signal(false);

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
    this.destroyRef.onDestroy(() => this.cargaReservas?.unsubscribe());
    this.cargandoPerfil.set(true);
    this.api.getMiPerfil().pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.cargandoPerfil.set(false))).subscribe({
      next: p => this.perfil = p,
      error: e => this.errorPerfil = mensajeHttp(e, 'No se pudo cargar tu perfil'),
    });
    this.api.getLaboratorios().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: l => this.laboratorios = l,
      error: e => this.errorReserva = mensajeHttp(e, 'No se pudieron cargar los laboratorios'),
    });
    this.cargarReservas();
  }

  private cargarReservas(): void {
    this.cargaReservas?.unsubscribe();
    const id = this.auth.investigadorId;
    if (id != null) {
      this.cargandoReservas.set(true);
      this.cargaReservas = this.api.getReservas(id).pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.cargandoReservas.set(false))).subscribe({
        next: r => this.reservas = r,
        error: e => this.errorReserva = mensajeHttp(e, 'No se pudieron cargar tus reservas'),
      });
    }
  }

  guardarPerfil(): void {
    if (!this.perfil || this.guardandoPerfil()) return;
    this.guardandoPerfil.set(true);
    this.mensajePerfil = '';
    this.errorPerfil = '';
    this.api.actualizarInvestigador(this.perfil.id, this.perfil).pipe(finalize(() => this.guardandoPerfil.set(false))).subscribe({
      next: (p) => {
        this.perfil = p;
        this.mensajePerfil = 'Perfil actualizado correctamente';
      },
      error: (e) => (this.errorPerfil = mensajeHttp(e, 'No se pudo guardar el perfil')),
    });
  }

  agendar(): void {
    if (this.guardandoReserva()) return;
    this.guardandoReserva.set(true);
    this.mensajeReserva = '';
    this.errorReserva = '';
    const r = this.nuevaReserva;
    this.api.crearReserva({
      fecha: r.fecha,
      horaInicio: r.horaInicio,
      horaFin: r.horaFin,
      laboratorio: { id: r.laboratorioId! },
    }).pipe(finalize(() => this.guardandoReserva.set(false))).subscribe({
      next: () => {
        this.mensajeReserva = 'Reserva registrada';
        this.nuevaReserva = { laboratorioId: null, fecha: '', horaInicio: '', horaFin: '' };
        this.cargarReservas();
      },
      error: (e) => (this.errorReserva = mensajeHttp(e, 'No se pudo crear la reserva')),
    });
  }

  cancelar(reserva: Reserva): void {
    if (this.guardandoReserva() || !window.confirm('¿Confirmas cancelar esta reserva?')) return;
    this.guardandoReserva.set(true);
    this.errorReserva = '';
    this.api.eliminarReserva(reserva.id).pipe(finalize(() => this.guardandoReserva.set(false))).subscribe({
      next: () => this.cargarReservas(),
      error: (e) => (this.errorReserva = mensajeHttp(e, 'No se pudo cancelar la reserva')),
    });
  }

  hora(h: string): string {
    return h.slice(0, 5);
  }
}
