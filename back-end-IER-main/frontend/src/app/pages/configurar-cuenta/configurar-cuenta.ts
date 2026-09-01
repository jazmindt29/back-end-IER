import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../core/api.service';

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

  estado: 'validando' | 'valido' | 'invalido' | 'listo' = 'validando';
  token = '';
  email = '';
  password = '';
  confirmacion = '';
  error = '';

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';
    if (!this.token) {
      this.estado = 'invalido';
      this.error = 'Falta el token de invitación en el enlace';
      return;
    }
    this.api.validarTokenInvitacion(this.token).subscribe({
      next: (res) => {
        this.email = res.email;
        this.estado = 'valido';
      },
      error: (e) => {
        this.estado = 'invalido';
        this.error = e.error?.mensaje ?? 'El enlace de invitación no es válido o ya expiró';
      },
    });
  }

  guardar(): void {
    this.error = '';
    if (this.password !== this.confirmacion) {
      this.error = 'Las contraseñas no coinciden';
      return;
    }
    this.api.configurarCuenta(this.token, this.password).subscribe({
      next: () => (this.estado = 'listo'),
      error: (e) => (this.error = e.error?.mensaje ?? 'No se pudo activar la cuenta'),
    });
  }

  irALogin(): void {
    this.router.navigate(['/login']);
  }
}
