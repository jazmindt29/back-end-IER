import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ApiService } from '../../core/api.service';
import { mensajeHttp } from '../../core/http-error';

@Component({
  selector: 'app-invitaciones-admin',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './invitaciones-admin.html',
})
export class InvitacionesAdminComponent {
  private api = inject(ApiService);
  private destroyRef = inject(DestroyRef);

  invitacion = { nombre: '', email: '' };
  enviando = signal(false);
  mensaje = '';
  advertencia = '';
  error = '';

  invitar(reenviar = false): void {
    if (this.enviando()) return;
    this.enviando.set(true);
    this.mensaje = '';
    this.advertencia = '';
    this.error = '';
    const { nombre, email } = this.invitacion;
    const solicitud = reenviar
      ? this.api.reenviarInvitacion(nombre, email)
      : this.api.invitarInvestigador(nombre, email);
    solicitud.pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.enviando.set(false))).subscribe({
      next: r => {
        if (!r.enviado) {
          this.advertencia = r.mensaje;
          return;
        }
        this.mensaje = r.mensaje;
        if (this.invitacion.nombre === nombre && this.invitacion.email === email) {
          this.invitacion = { nombre: '', email: '' };
        }
      },
      error: e => this.error = mensajeHttp(e, 'No se pudo procesar la invitación'),
    });
  }
}
