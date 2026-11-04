import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { mensajeHttp } from '../../core/http-error';

@Component({
  selector: 'app-configurar-cuenta',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './configurar-cuenta.html'
})
export class ConfigurarCuentaComponent implements OnInit {
  private api = inject(ApiService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  estado = signal<'validando' | 'valido' | 'invalido' | 'error' | 'listo'>('validando');
  guardando = signal(false);
  token = '';
  email = '';
  password = '';
  confirmacion = '';
  error = '';

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';
    this.validar();
  }

  validar(): void {
    this.error = '';
    if (!this.token) {
      this.estado.set('invalido');
      this.error = 'Falta el token de invitación en el enlace';
      return;
    }
    this.estado.set('validando');
    this.api.validarTokenInvitacion(this.token).subscribe({
      next: (res) => {
        if (!res.valido) {
          this.estado.set('invalido');
          this.error = 'La invitación no es válida. Solicita un nuevo enlace.';
          return;
        }
        this.email = res.email;
        this.estado.set('valido');
      },
      error: (e: unknown) => {
        this.error = mensajeHttp(e, 'No fue posible validar la invitación. Intenta nuevamente.');
        this.estado.set(e instanceof HttpErrorResponse && e.status === 400 ? 'invalido' : 'error');
      },
    });
  }

  guardar(): void {
    if (this.guardando() || this.estado() !== 'valido') return;
    this.error = '';
    if (this.password !== this.confirmacion) {
      this.error = 'Las contraseñas no coinciden';
      return;
    }
    this.guardando.set(true);
    this.api.configurarCuenta(this.token, this.password)
      .pipe(finalize(() => this.guardando.set(false)))
      .subscribe({
        next: () => {
          this.password = this.confirmacion = '';
          this.estado.set('listo');
        },
        error: (e: unknown) => (this.error = mensajeHttp(e, 'No se pudo activar la cuenta')),
      });
  }

  irALogin(): void {
    this.router.navigate(['/login']);
  }
}
